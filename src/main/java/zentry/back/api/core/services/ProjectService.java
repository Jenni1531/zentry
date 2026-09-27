package zentry.back.api.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import zentry.back.api.core.dtos.*;
import zentry.back.api.core.models.*;
import zentry.back.api.core.repositories.*;
import zentry.back.api.global.mappers;
import zentry.back.api.realtime.service.ConversationService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Proyectos colaborativos: drive de archivos, tareas, notas, libro por capítulos, obra compartida
 * en el editor del Estudio, chat de grupo y publicación al feed.
 *
 * Identidad: la clave única de un usuario es su EMAIL (lo que llega en Principal). Antes se
 * guardaba el @usuario al invitar y el control de acceso comparaba contra el email, así que los
 * colaboradores nunca podían entrar. Ahora todo se guarda y compara por email (con alias legacy).
 */
@Service
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
    private static final Set<String> TYPES = Set.of("general", "book", "image", "video", "audio");

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectLikeRepository projectLikeRepository;
    private final ProjectChapterRepository chapterRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final StudioProjectRepository studioProjectRepository;
    private final GamificationEventService gamificationEventService;
    private final NotificationService notificationService;
    private final ConversationService conversationService;
    private final PostService postService;
    private final CosmeticsService cosmeticsService;

    public ProjectService(ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository,
                          ProjectLikeRepository projectLikeRepository, ProjectChapterRepository chapterRepository,
                          UserRepository userRepository, ProfileRepository profileRepository,
                          StudioProjectRepository studioProjectRepository, GamificationEventService gamificationEventService,
                          NotificationService notificationService, ConversationService conversationService,
                          PostService postService, CosmeticsService cosmeticsService) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectLikeRepository = projectLikeRepository;
        this.chapterRepository = chapterRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.studioProjectRepository = studioProjectRepository;
        this.gamificationEventService = gamificationEventService;
        this.notificationService = notificationService;
        this.conversationService = conversationService;
        this.postService = postService;
        this.cosmeticsService = cosmeticsService;
    }

    // =====================================================================
    // Migración: normaliza dueños y miembros al email (clave única)
    // =====================================================================

    @jakarta.annotation.PostConstruct
    public void normalizeMembership() {
        try {
            Map<String, String> aliasToEmail = new HashMap<>();
            for (User u : userRepository.findAll()) {
                if (u.getEmail() == null) continue;
                if (u.getHandle() != null) aliasToEmail.put(u.getHandle().toLowerCase(), u.getEmail());
                aliasToEmail.put(u.getEmail().split("@")[0].toLowerCase(), u.getEmail());
            }
            for (Project p : projectRepository.findAll()) {
                String owner = p.getCreatedBy();
                if (owner != null && !owner.contains("@") && aliasToEmail.containsKey(owner.toLowerCase())) {
                    p.setCreatedBy(aliasToEmail.get(owner.toLowerCase()));
                    projectRepository.save(p);
                }
            }
            for (ProjectMember m : projectMemberRepository.findAll()) {
                String name = m.getUsername();
                if (name == null || name.contains("@")) continue;
                String email = aliasToEmail.get(name.toLowerCase());
                if (email == null) continue;
                projectMemberRepository.delete(m);
                if (!projectMemberRepository.existsByProjectIdAndUsername(m.getProjectId(), email)) {
                    projectMemberRepository.save(ProjectMember.builder()
                            .projectId(m.getProjectId()).username(email).role(m.getRole()).joinedAt(m.getJoinedAt()).build());
                }
            }
        } catch (Exception e) {
            log.warn("No se pudo normalizar la membresía de proyectos: {}", e.getMessage());
        }
    }

    // =====================================================================
    // Lectura
    // =====================================================================

    /** Mis proyectos: los que creé + donde colaboro */
    public List<ProjectResponse> getProjectsByUser(String principal) {
        User user = requireUser(principal);
        Set<String> ids = identities(user);

        Map<Long, Project> result = new LinkedHashMap<>();
        projectRepository.findByCreatedByIn(ids).forEach(p -> result.put(p.getId(), p));
        List<Long> memberOf = projectMemberRepository.findByUsernameIn(ids).stream()
                .map(ProjectMember::getProjectId).filter(id -> !result.containsKey(id)).toList();
        projectRepository.findAllById(memberOf).forEach(p -> result.put(p.getId(), p));

        return result.values().stream()
                .sorted(Comparator.comparing(Project::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(p -> toResponse(p, user, false))
                .toList();
    }

    /** Proyectos públicos (Explorar) */
    public Page<ProjectResponse> getPublicProjects(String principal, Pageable pageable) {
        User viewer = tryUser(principal);
        return projectRepository.findByVisibilityOrderByUpdatedAtDesc("public", pageable)
                .map(p -> toResponse(p, viewer, false));
    }

    public ProjectResponse getProjectById(Long id, String principal) {
        User user = tryUser(principal);
        Project project = findProjectOrThrow(id);
        assertCanView(project, user);
        return toResponse(project, user, true);
    }

    /** Explorar: proyectos públicos que coinciden con la búsqueda */
    public List<ProjectResponse> searchPublicProjects(String query, String principal) {
        if (query == null || query.isBlank()) return List.of();
        User viewer = tryUser(principal);
        return projectRepository.searchPublic(query.trim().toLowerCase()).stream()
                .limit(20).map(p -> toResponse(p, viewer, false)).toList();
    }

    public List<ProjectResponse> searchProjects(String principal, String query) {
        User user = requireUser(principal);
        String q = query == null ? "" : query.toLowerCase();
        List<ProjectResponse> mine = getProjectsByUser(principal).stream()
                .filter(p -> (p.getTitle() != null && p.getTitle().toLowerCase().contains(q))
                        || (p.getTags() != null && p.getTags().toLowerCase().contains(q)))
                .toList();
        Set<Long> mineIds = mine.stream().map(ProjectResponse::getId).collect(Collectors.toSet());
        List<ProjectResponse> publicOnes = projectRepository.searchPublic(q).stream()
                .filter(p -> !mineIds.contains(p.getId()))
                .map(p -> toResponse(p, user, false))
                .toList();
        List<ProjectResponse> all = new ArrayList<>(mine);
        all.addAll(publicOnes);
        return all;
    }

    // =====================================================================
    // Crear / editar / eliminar
    // =====================================================================

    @Transactional
    public ProjectResponse createProject(String principal, ProjectRequestDTO dto) {
        User user = requireUser(principal);
        String type = dto.getProjectType() != null && TYPES.contains(dto.getProjectType().toLowerCase())
                ? dto.getProjectType().toLowerCase() : "general";

        Project project = Project.builder()
                .title(dto.getTitle() != null && !dto.getTitle().isBlank() ? dto.getTitle().trim() : "Nuevo Proyecto")
                .description(dto.getDescription())
                .category(dto.getCategory() != null ? dto.getCategory() : defaultCategory(type))
                .priority(dto.getPriority() != null ? dto.getPriority() : "media")
                .status("active")
                .deadline(dto.getDeadline() != null && !dto.getDeadline().isBlank() ? dto.getDeadline() : "Sin fecha límite")
                .createdBy(user.getEmail())
                .tags(dto.getTags() != null ? String.join(",", dto.getTags()) : "")
                .visibility("public".equalsIgnoreCase(dto.getVisibility()) ? "public" : "private")
                .projectType(type)
                .coverUrl(dto.getCoverUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        addActivity(project, user, "creó el proyecto", project.getTitle(), "member");
        Project saved = projectRepository.save(project);

        projectMemberRepository.save(ProjectMember.builder()
                .projectId(saved.getId()).username(user.getEmail()).role("OWNER").joinedAt(LocalDateTime.now()).build());

        // Chat de grupo del proyecto (tipo canal de Discord)
        try {
            saved.setConversationId(conversationService.createProjectChat(user.getId(), "📁 " + saved.getTitle(), List.of()));
        } catch (Exception e) {
            log.warn("No se pudo crear el chat del proyecto {}: {}", saved.getId(), e.getMessage());
        }

        // Obra compartida en el editor del Estudio para proyectos de imagen/video/audio
        if (Set.of("image", "video", "audio").contains(type)) {
            StudioProject work = studioProjectRepository.save(StudioProject.builder()
                    .title(saved.getTitle())
                    .description(saved.getDescription())
                    .type(switch (type) {
                        case "video" -> ContentType.VIDEO;
                        case "audio" -> ContentType.AUDIO;
                        default -> ContentType.IMAGE;
                    })
                    .ownerUsername(user.getHandle() != null ? user.getHandle() : user.getEmail())
                    .projectId(saved.getId())
                    .rewardCoins(50)
                    .build());
            saved.setStudioProjectId(work.getId());
        }
        saved = projectRepository.save(saved);

        gamificationEventService.recordMissionProgress(user.getId(), "create_project", 1);
        return toResponse(saved, user, true);
    }

    @Transactional
    public ProjectResponse updateProject(Long id, String principal, ProjectRequestDTO dto) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(id);
        assertOwnership(project, user);

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            project.setTitle(dto.getTitle().trim());
            conversationService.renameConversation(project.getConversationId(), "📁 " + project.getTitle());
        }
        if (dto.getDescription() != null) project.setDescription(dto.getDescription());
        if (dto.getCategory() != null) project.setCategory(dto.getCategory());
        if (dto.getPriority() != null) project.setPriority(dto.getPriority());
        if (dto.getStatus() != null) project.setStatus(dto.getStatus());
        if (dto.getDeadline() != null) project.setDeadline(dto.getDeadline());
        if (dto.getTags() != null) project.setTags(String.join(",", dto.getTags()));
        if (dto.getCoverUrl() != null) project.setCoverUrl(dto.getCoverUrl().isBlank() ? null : dto.getCoverUrl());
        if (dto.getVisibility() != null) {
            String next = "public".equalsIgnoreCase(dto.getVisibility()) ? "public" : "private";
            if (!next.equals(project.getVisibility())) {
                project.setVisibility(next);
                addActivity(project, user, "public".equals(next) ? "hizo público el proyecto" : "hizo privado el proyecto",
                        project.getTitle(), "status");
            }
        }
        project.setUpdatedAt(LocalDateTime.now());
        addActivity(project, user, "actualizó el proyecto", project.getTitle(), "status");
        return toResponse(projectRepository.save(project), user, true);
    }

    /** Portada: sube una imagen al servidor */
    @Transactional
    public ProjectResponse uploadCover(Long id, String principal, MultipartFile file) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(id);
        assertOwnership(project, user);
        if (file == null || file.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecciona una imagen");
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La portada debe ser una imagen");
        }
        project.setCoverUrl("/uploads/projects/" + saveResourceFile(file));
        project.setUpdatedAt(LocalDateTime.now());
        return toResponse(projectRepository.save(project), user, true);
    }

    @Transactional
    public void deleteProject(Long id, String principal) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(id);
        assertOwnership(project, user);
        projectMemberRepository.deleteByProjectId(id);
        projectLikeRepository.deleteByProjectId(id);
        chapterRepository.deleteByProjectId(id);
        // La obra del Estudio se conserva (queda solo para su dueño)
        studioProjectRepository.findByProjectId(id).forEach(sp -> { sp.setProjectId(null); studioProjectRepository.save(sp); });
        try {
            conversationService.deleteConversation(project.getConversationId());
        } catch (Exception e) {
            log.warn("No se pudo borrar el chat del proyecto {}: {}", id, e.getMessage());
        }
        projectRepository.delete(project);
    }

    // =====================================================================
    // Publicar en el feed al completar
    // =====================================================================

    @Transactional
    public ProjectResponse publishToFeed(Long id, String principal) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(id);
        assertOwnership(project, user);
        if (project.getPublishedPostId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este proyecto ya está publicado en el feed");
        }

        String type = project.getProjectType() != null ? project.getProjectType() : "general";
        PostRequest.PostRequestBuilder post = PostRequest.builder()
                .title(project.getTitle())
                .visibility("public")
                .tools(project.getTags());

        String credits = creditsLine(project);
        switch (type) {
            case "book" -> {
                List<ProjectChapter> chapters = chapterRepository.findByProjectIdOrderByPositionAsc(id);
                if (chapters.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escribe al menos un capítulo antes de publicar el libro");
                }
                StringBuilder body = new StringBuilder();
                if (project.getDescription() != null && !project.getDescription().isBlank()) {
                    body.append(project.getDescription().trim()).append("\n\n");
                }
                ProjectChapter first = chapters.get(0);
                String excerpt = first.getContent() != null ? first.getContent().trim() : "";
                if (excerpt.length() > 1200) excerpt = excerpt.substring(0, 1200) + "…";
                body.append("📖 ").append(first.getTitle()).append("\n\n").append(excerpt)
                        .append("\n\n— ").append(chapters.size()).append(chapters.size() == 1 ? " capítulo" : " capítulos")
                        .append(". Lee el libro completo en Proyectos.").append(credits);
                post.contentType("text").contenido(body.toString());
                if (project.getCoverUrl() != null) post.imageUrl(project.getCoverUrl()).thumbnailUrl(project.getCoverUrl());
            }
            case "image", "video", "audio" -> {
                StudioProject work = project.getStudioProjectId() != null
                        ? studioProjectRepository.findById(project.getStudioProjectId()).orElse(null) : null;
                String media = work != null && work.getMediaUrl() != null ? work.getMediaUrl() : latestResourceOf(project, type);
                if (media == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Guarda la obra en el editor (o sube un archivo de " + type + " al drive) antes de publicar");
                }
                post.contentType(type).imageUrl(media).thumbnailUrl(media)
                        .contenido((project.getDescription() != null ? project.getDescription() : "") + credits);
            }
            default -> {
                post.contentType(project.getCoverUrl() != null ? "image" : "text")
                        .contenido((project.getDescription() != null ? project.getDescription() : "") + credits);
                if (project.getCoverUrl() != null) post.imageUrl(project.getCoverUrl()).thumbnailUrl(project.getCoverUrl());
            }
        }

        PostResponse created = postService.create(user.getEmail(), post.build(), null, false);
        project.setPublishedPostId(created.getId());
        project.setStatus("completed");
        project.setUpdatedAt(LocalDateTime.now());
        addActivity(project, user, "publicó el proyecto en el feed", project.getTitle(), "status");
        gamificationEventService.recordAchievementProgress(user.getId(), "publish_project", 1);
        Project saved = projectRepository.save(project);

        // Avisar a los colaboradores
        for (ProjectMember m : projectMemberRepository.findByProjectId(id)) {
            if (m.getUsername().equalsIgnoreCase(user.getEmail())) continue;
            userRepository.findByEmail(m.getUsername()).ifPresent(u -> notificationService.notify(
                    u.getId(), "project_message",
                    "🚀 \"" + project.getTitle() + "\" se publicó en el feed. ¡Felicidades por el trabajo en equipo!",
                    user.getHandle(), avatarOf(user.getId()), id.intValue()));
        }
        return toResponse(saved, user, true);
    }

    // =====================================================================
    // Libro (capítulos)
    // =====================================================================

    public List<ProjectChapterResponse> listChapters(Long projectId, String principal) {
        Project project = findProjectOrThrow(projectId);
        assertCanView(project, tryUser(principal));
        return chapterRepository.findByProjectIdOrderByPositionAsc(projectId).stream().map(this::toChapterResponse).toList();
    }

    @Transactional
    public ProjectChapterResponse addChapter(Long projectId, String principal, ProjectChapterRequest request) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(projectId);
        assertMember(project, user);
        int position = (int) chapterRepository.countByProjectId(projectId) + 1;
        ProjectChapter chapter = chapterRepository.save(ProjectChapter.builder()
                .projectId(projectId)
                .title(request.getTitle() != null && !request.getTitle().isBlank() ? request.getTitle().trim() : "Capítulo " + position)
                .content(request.getContent() != null ? request.getContent() : "")
                .position(position)
                .wordCount(wordCount(request.getContent()))
                .authorEmail(user.getEmail())
                .lastEditedBy(user.getHandle())
                .build());
        addActivity(project, user, "escribió el capítulo", chapter.getTitle(), "task");
        gamificationEventService.recordAchievementProgress(user.getId(), "write_chapters", 1);
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
        return toChapterResponse(chapter);
    }

    @Transactional
    public ProjectChapterResponse updateChapter(Long projectId, Long chapterId, String principal, ProjectChapterRequest request) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(projectId);
        assertMember(project, user);
        ProjectChapter chapter = chapterRepository.findById(chapterId)
                .filter(c -> c.getProjectId().equals(projectId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Capítulo no encontrado"));
        if (request.getTitle() != null && !request.getTitle().isBlank()) chapter.setTitle(request.getTitle().trim());
        if (request.getContent() != null) {
            chapter.setContent(request.getContent());
            chapter.setWordCount(wordCount(request.getContent()));
        }
        chapter.setLastEditedBy(user.getHandle());
        ProjectChapter saved = chapterRepository.save(chapter);

        if (request.getPosition() != null && !request.getPosition().equals(chapter.getPosition())) {
            reorder(projectId, chapterId, request.getPosition());
            saved = chapterRepository.findById(chapterId).orElse(saved);
        }
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
        return toChapterResponse(saved);
    }

    @Transactional
    public void deleteChapter(Long projectId, Long chapterId, String principal) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(projectId);
        assertMember(project, user);
        chapterRepository.findById(chapterId).filter(c -> c.getProjectId().equals(projectId)).ifPresent(c -> {
            chapterRepository.delete(c);
            addActivity(project, user, "eliminó el capítulo", c.getTitle(), "task");
            projectRepository.save(project);
        });
        // Recompactar posiciones 1..n
        List<ProjectChapter> rest = chapterRepository.findByProjectIdOrderByPositionAsc(projectId);
        for (int i = 0; i < rest.size(); i++) rest.get(i).setPosition(i + 1);
        chapterRepository.saveAll(rest);
    }

    private void reorder(Long projectId, Long chapterId, int newPosition) {
        List<ProjectChapter> chapters = new ArrayList<>(chapterRepository.findByProjectIdOrderByPositionAsc(projectId));
        ProjectChapter moving = chapters.stream().filter(c -> c.getId().equals(chapterId)).findFirst().orElse(null);
        if (moving == null) return;
        chapters.remove(moving);
        int index = Math.max(0, Math.min(chapters.size(), newPosition - 1));
        chapters.add(index, moving);
        for (int i = 0; i < chapters.size(); i++) chapters.get(i).setPosition(i + 1);
        chapterRepository.saveAll(chapters);
    }

    // =====================================================================
    // Tareas y notas
    // =====================================================================

    @Transactional
    public ProjectTask addTask(Long projectId, String principal, TaskRequestDTO dto) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(projectId);
        assertMember(project, user);

        ProjectTask task = ProjectTask.builder()
                .title(dto.getTitle() != null ? dto.getTitle() : "Nueva Tarea")
                .priority(dto.getPriority() != null ? dto.getPriority() : "media")
                .assignedTo(dto.getAssignedTo() != null ? dto.getAssignedTo() : user.getHandle())
                .dueDate(dto.getDueDate() != null ? dto.getDueDate() : "Pronto")
                .completed(false)
                .project(project)
                .build();
        project.getTasks().add(task);
        project.setUpdatedAt(LocalDateTime.now());
        addActivity(project, user, "creó la tarea", task.getTitle(), "task");
        Project saved = projectRepository.save(project);
        return saved.getTasks().get(saved.getTasks().size() - 1);
    }

    @Transactional
    public ProjectTask toggleTask(Long projectId, Long taskId, String principal) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(projectId);
        assertMember(project, user);

        ProjectTask task = project.getTasks().stream()
                .filter(t -> t.getId() != null && t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada"));
        task.setCompleted(!task.isCompleted());
        project.setUpdatedAt(LocalDateTime.now());
        addActivity(project, user, task.isCompleted() ? "completó la tarea" : "reabrió la tarea", task.getTitle(), "task");
        projectRepository.save(project);
        return task;
    }

    @Transactional
    public void deleteTask(Long projectId, Long taskId, String principal) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(projectId);
        assertMember(project, user);
        project.getTasks().removeIf(t -> t.getId() != null && t.getId().equals(taskId));
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
    }

    @Transactional
    public ProjectNote addNote(Long projectId, String principal, NoteRequestDTO dto) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(projectId);
        assertMember(project, user);
        ProjectNote note = ProjectNote.builder()
                .content(dto.getContent() != null ? dto.getContent() : "")
                .author(user.getHandle() != null ? user.getHandle() : user.getEmail())
                .createdAt(LocalDateTime.now())
                .project(project)
                .build();
        project.getNotes().add(note);
        project.setUpdatedAt(LocalDateTime.now());
        Project saved = projectRepository.save(project);
        return saved.getNotes().get(saved.getNotes().size() - 1);
    }

    // =====================================================================
    // Drive (archivos y carpetas)
    // =====================================================================

    @Transactional
    public ProjectResource addResource(Long projectId, String principal, ResourceRequestDTO dto, MultipartFile file, String folder) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(projectId);
        assertMember(project, user);

        String name = dto.getName();
        String type = dto.getType();
        String size = dto.getSize();
        String url = dto.getUrl();
        Long sizeBytes = null;
        String mime = null;

        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            type = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toUpperCase()
                    : "BIN";
            url = "/uploads/projects/" + saveResourceFile(file);
            size = formatFileSize(file.getSize());
            sizeBytes = file.getSize();
            mime = file.getContentType();
            if (name == null || name.isBlank()) name = originalFilename != null ? originalFilename : "Archivo";
        }

        boolean isFolder = "FOLDER".equalsIgnoreCase(type);
        ProjectResource res = ProjectResource.builder()
                .name(name != null && !name.isBlank() ? name.trim() : (isFolder ? "Nueva carpeta" : "Archivo"))
                .type(type != null ? type.toUpperCase() : "LINK")
                .size(isFolder ? "—" : (size != null ? size : "—"))
                .url(url)
                .sizeBytes(sizeBytes)
                .mimeType(mime)
                .folder(normalizeFolder(folder))
                .uploadedBy(user.getHandle() != null ? user.getHandle() : user.getEmail())
                .uploadedAt(LocalDateTime.now())
                .project(project)
                .build();
        project.getResources().add(res);
        project.setUpdatedAt(LocalDateTime.now());
        addActivity(project, user, isFolder ? "creó la carpeta" : "subió el archivo", res.getName(), "file");
        Project saved = projectRepository.save(project);
        return saved.getResources().get(saved.getResources().size() - 1);
    }

    @Transactional
    public ProjectResource updateResource(Long projectId, Long resourceId, String principal, ProjectResourceUpdateRequest request) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(projectId);
        assertMember(project, user);
        ProjectResource res = findResource(project, resourceId);

        if ("FOLDER".equalsIgnoreCase(res.getType()) && request.getName() != null && !request.getName().isBlank()) {
            // Renombrar carpeta: mover también su contenido
            String oldPath = childPath(res);
            res.setName(request.getName().trim());
            String newPath = childPath(res);
            for (ProjectResource r : project.getResources()) {
                if (r.getFolder() != null && (r.getFolder().equals(oldPath) || r.getFolder().startsWith(oldPath + "/"))) {
                    r.setFolder(newPath + r.getFolder().substring(oldPath.length()));
                }
            }
        } else if (request.getName() != null && !request.getName().isBlank()) {
            res.setName(request.getName().trim());
        }
        if (request.getFolder() != null) res.setFolder(normalizeFolder(request.getFolder()));
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
        return res;
    }

    @Transactional
    public void deleteResource(Long projectId, Long resourceId, String principal) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(projectId);
        assertMember(project, user);
        ProjectResource res = findResource(project, resourceId);

        List<ProjectResource> toRemove = new ArrayList<>();
        toRemove.add(res);
        if ("FOLDER".equalsIgnoreCase(res.getType())) {
            String path = childPath(res);
            project.getResources().stream()
                    .filter(r -> r.getFolder() != null && (r.getFolder().equals(path) || r.getFolder().startsWith(path + "/")))
                    .forEach(toRemove::add);
        }
        for (ProjectResource r : toRemove) {
            deleteStoredFile(r.getUrl());
            project.getResources().remove(r);
        }
        addActivity(project, user, "eliminó", res.getName(), "file");
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
    }

    // =====================================================================
    // Miembros
    // =====================================================================

    public List<ProjectMemberResponse> listMembers(Long projectId, String principal) {
        Project project = findProjectOrThrow(projectId);
        assertCanView(project, tryUser(principal));

        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);
        Map<String, User> users = new HashMap<>();
        for (ProjectMember m : members) userRepository.findByEmail(m.getUsername()).ifPresent(u -> users.put(m.getUsername(), u));
        Set<Integer> userIds = users.values().stream().map(User::getId).collect(Collectors.toSet());
        Map<Integer, Profile> profiles = profileRepository.findByUserIdIn(userIds).stream()
                .collect(Collectors.toMap(Profile::getUserId, p -> p, (a, b) -> a));
        Map<Integer, CosmeticsResponse> cosmetics = cosmeticsService.forUsers(userIds);

        return members.stream().map(m -> {
            ProjectMemberResponse r = mappers.toResponse(m);
            User u = users.get(m.getUsername());
            if (u != null) {
                Profile p = profiles.get(u.getId());
                r.setUserId(u.getId());
                r.setHandle(u.getHandle() != null ? u.getHandle() : u.getEmail().split("@")[0]);
                r.setName(p != null && p.getName() != null ? p.getName() : r.getHandle());
                r.setAvatarUrl(p != null ? p.getAvatarUrl() : null);
                r.setCosmetics(cosmetics.get(u.getId()));
            } else {
                r.setHandle(m.getUsername());
                r.setName(m.getUsername());
            }
            return r;
        }).toList();
    }

    @Transactional
    public ProjectMemberResponse inviteMember(Long projectId, String principal, InviteMemberRequest request) {
        User owner = requireUser(principal);
        Project project = findProjectOrThrow(projectId);
        assertOwnership(project, owner);

        String target = request.getUsername().trim().replaceFirst("^@", "");
        User targetUser = userRepository.findByUsernameOrEmail(target, target)
                .or(() -> userRepository.findByEmail(target))
                .or(() -> userRepository.findByUsername(target))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: @" + target));
        if (projectMemberRepository.existsByProjectIdAndUsernameIn(projectId, identities(targetUser))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ese usuario ya es colaborador del proyecto");
        }

        ProjectMember member = projectMemberRepository.save(ProjectMember.builder()
                .projectId(projectId).username(targetUser.getEmail()).role("COLLABORATOR").joinedAt(LocalDateTime.now()).build());
        conversationService.addParticipant(ensureChat(project, owner), targetUser.getId());

        addActivity(project, owner, "invitó a colaborar a", "@" + targetUser.getHandle(), "member");
        projectRepository.save(project);

        notificationService.notify(targetUser.getId(), "project_invite",
                "@" + owner.getHandle() + " te agregó al proyecto \"" + project.getTitle() + "\"",
                owner.getHandle(), avatarOf(owner.getId()), projectId.intValue());
        gamificationEventService.recordAchievementProgress(targetUser.getId(), "project_collab", 1);
        gamificationEventService.recordAchievementProgress(owner.getId(), "lead_team", 1);

        ProjectMemberResponse r = mappers.toResponse(member);
        r.setUserId(targetUser.getId());
        r.setHandle(targetUser.getHandle());
        r.setName(targetUser.getHandle());
        r.setAvatarUrl(avatarOf(targetUser.getId()));
        return r;
    }

    @Transactional
    public void removeMember(Long projectId, String principal, String targetIdentifier) {
        User requester = requireUser(principal);
        Project project = findProjectOrThrow(projectId);
        User target = userRepository.findByEmail(targetIdentifier)
                .or(() -> userRepository.findByUsername(targetIdentifier))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Colaborador no encontrado"));

        boolean isSelf = requester.getId().equals(target.getId());
        if (isSelf) assertMember(project, requester); else assertOwnership(project, requester);
        if (isOwner(project, target)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El dueño del proyecto no puede ser removido");
        }

        List<ProjectMember> rows = projectMemberRepository.findByProjectId(projectId).stream()
                .filter(m -> identities(target).contains(m.getUsername())).toList();
        if (rows.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Colaborador no encontrado");
        projectMemberRepository.deleteAll(rows);
        conversationService.removeParticipant(project.getConversationId(), target.getId());
        addActivity(project, requester, isSelf ? "salió del proyecto" : "quitó del proyecto a", "@" + target.getHandle(), "member");
        projectRepository.save(project);
    }

    /** Devuelve el id del chat del proyecto, creándolo (con todos los miembros) si aún no existe */
    @Transactional
    public Integer ensureChat(Project project, User requester) {
        if (project.getConversationId() != null) return project.getConversationId();
        User owner = userRepository.findByEmail(project.getCreatedBy()).orElse(requester);
        List<Integer> memberIds = projectMemberRepository.findByProjectId(project.getId()).stream()
                .map(m -> userRepository.findByEmail(m.getUsername()).map(User::getId).orElse(null))
                .filter(Objects::nonNull).toList();
        Integer chatId = conversationService.createProjectChat(owner.getId(), "📁 " + project.getTitle(), memberIds);
        project.setConversationId(chatId);
        projectRepository.save(project);
        return chatId;
    }

    /** Chat del proyecto para el frontend (crea el grupo en proyectos antiguos) */
    @Transactional
    public Map<String, Object> getChat(Long projectId, String principal) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(projectId);
        assertMember(project, user);
        Integer chatId = ensureChat(project, user);
        // Garantiza que el miembro esté en el grupo aunque se haya unido antes de existir el chat
        conversationService.addParticipant(chatId, user.getId());
        return Map.of("conversationId", chatId);
    }

    // =====================================================================
    // Likes
    // =====================================================================

    @Transactional
    public ProjectLikeResponse toggleLike(Long projectId, String principal) {
        User user = requireUser(principal);
        Project project = findProjectOrThrow(projectId);
        assertCanView(project, user);

        boolean liked;
        var existing = projectLikeRepository.findByProjectIdAndUsername(projectId, user.getEmail());
        if (existing.isPresent()) {
            projectLikeRepository.delete(existing.get());
            liked = false;
        } else {
            projectLikeRepository.save(ProjectLike.builder()
                    .projectId(projectId).username(user.getEmail()).createdAt(LocalDateTime.now()).build());
            liked = true;
            gamificationEventService.recordMissionProgress(user.getId(), "react_projects", 1);
        }
        return ProjectLikeResponse.builder().liked(liked).likesCount(projectLikeRepository.countByProjectId(projectId)).build();
    }

    public ProjectLikeResponse getLikeStatus(Long projectId, String principal) {
        User user = requireUser(principal);
        findProjectOrThrow(projectId);
        boolean liked = projectLikeRepository.findByProjectIdAndUsername(projectId, user.getEmail()).isPresent();
        return ProjectLikeResponse.builder().liked(liked).likesCount(projectLikeRepository.countByProjectId(projectId)).build();
    }

    // =====================================================================
    // Permisos (usados también por el canal de voz y el editor compartido)
    // =====================================================================

    public boolean isMember(Long projectId, String principal) {
        User user = tryUser(principal);
        if (user == null) return false;
        return projectRepository.findById(projectId).map(p -> isMemberOf(p, user)).orElse(false);
    }

    private boolean isOwner(Project project, User user) {
        return user != null && project.getCreatedBy() != null && identities(user).contains(project.getCreatedBy());
    }

    private boolean isMemberOf(Project project, User user) {
        return user != null && (isOwner(project, user)
                || projectMemberRepository.existsByProjectIdAndUsernameIn(project.getId(), identities(user)));
    }

    private void assertOwnership(Project project, User user) {
        if (!isOwner(project, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el dueño del proyecto puede hacer esto");
        }
    }

    private void assertMember(Project project, User user) {
        if (!isMemberOf(project, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo los miembros del proyecto pueden hacer esto");
        }
    }

    /** Público: cualquiera puede verlo (solo lectura). Privado: solo miembros. */
    private void assertCanView(Project project, User user) {
        if ("public".equals(project.getVisibility())) return;
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión");
        if (!isMemberOf(project, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este proyecto es privado");
        }
    }

    /** Todas las formas en que un usuario pudo quedar guardado (email, @usuario, prefijo del email) */
    private Set<String> identities(User user) {
        Set<String> ids = new LinkedHashSet<>();
        if (user.getEmail() != null) {
            ids.add(user.getEmail());
            ids.add(user.getEmail().split("@")[0]);
        }
        if (user.getHandle() != null && !user.getHandle().isBlank()) ids.add(user.getHandle());
        return ids;
    }

    private User requireUser(String principal) {
        User user = tryUser(principal);
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        return user;
    }

    private User tryUser(String principal) {
        if (principal == null || principal.isBlank() || "anonimo".equalsIgnoreCase(principal)) return null;
        return userRepository.findByEmail(principal)
                .or(() -> userRepository.findByUsername(principal))
                .orElse(null);
    }

    // =====================================================================
    // Utilidades
    // =====================================================================

    private ProjectResponse toResponse(Project p, User viewer, boolean full) {
        boolean owner = isOwner(p, viewer);
        boolean member = owner || (viewer != null && projectMemberRepository.existsByProjectIdAndUsernameIn(p.getId(), identities(viewer)));
        User ownerUser = p.getCreatedBy() != null ? userRepository.findByEmail(p.getCreatedBy()).orElse(null) : null;

        return ProjectResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .category(p.getCategory())
                .priority(p.getPriority())
                .status(p.getStatus())
                .deadline(p.getDeadline())
                .createdBy(ownerUser != null && ownerUser.getHandle() != null ? ownerUser.getHandle() : p.getCreatedBy())
                .ownerUsername(ownerUser != null ? ownerUser.getHandle() : null)
                .ownerAvatarUrl(ownerUser != null ? avatarOf(ownerUser.getId()) : null)
                .tags(p.getTags())
                .visibility(p.getVisibility() != null ? p.getVisibility() : "private")
                .projectType(p.getProjectType() != null ? p.getProjectType() : "general")
                .coverUrl(p.getCoverUrl())
                .conversationId(member ? p.getConversationId() : null)
                .studioProjectId(p.getStudioProjectId())
                .publishedPostId(p.getPublishedPostId())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .tasks(full || member ? p.getTasks() : List.of())
                .resources(full ? p.getResources() : List.of())
                .activities(full ? p.getActivities() : List.of())
                .notes(full && member ? p.getNotes() : List.of())
                .membersCount(projectMemberRepository.countByProjectId(p.getId()))
                .chaptersCount("book".equals(p.getProjectType()) ? chapterRepository.countByProjectId(p.getId()) : 0)
                .likesCount(projectLikeRepository.countByProjectId(p.getId()))
                .liked(viewer != null && projectLikeRepository.findByProjectIdAndUsername(p.getId(), viewer.getEmail()).isPresent())
                .owner(owner)
                .member(member)
                .build();
    }

    private ProjectChapterResponse toChapterResponse(ProjectChapter c) {
        String author = c.getAuthorEmail() != null
                ? userRepository.findByEmail(c.getAuthorEmail()).map(User::getHandle).orElse(null) : null;
        return ProjectChapterResponse.builder()
                .id(c.getId())
                .projectId(c.getProjectId())
                .title(c.getTitle())
                .content(c.getContent())
                .position(c.getPosition())
                .wordCount(c.getWordCount())
                .authorUsername(author)
                .lastEditedBy(c.getLastEditedBy())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private void addActivity(Project project, User user, String action, String target, String iconType) {
        String handle = user.getHandle() != null ? user.getHandle() : user.getEmail();
        project.getActivities().add(ProjectActivity.builder()
                .user(handle)
                .avatar(handle.length() >= 2 ? handle.substring(0, 2).toUpperCase() : "ZN")
                .action(action)
                .target(target)
                .iconType(iconType)
                .timestamp(LocalDateTime.now())
                .project(project)
                .build());
    }

    private String creditsLine(Project project) {
        List<String> handles = projectMemberRepository.findByProjectId(project.getId()).stream()
                .map(m -> userRepository.findByEmail(m.getUsername()).map(User::getHandle).orElse(null))
                .filter(Objects::nonNull).map(h -> "@" + h).toList();
        return handles.size() > 1 ? "\n\n👥 Creado en equipo por " + String.join(", ", handles) : "";
    }

    private String latestResourceOf(Project project, String type) {
        List<ProjectResource> list = new ArrayList<>(project.getResources());
        Collections.reverse(list);
        return list.stream()
                .filter(r -> r.getUrl() != null && r.getMimeType() != null && r.getMimeType().startsWith(type + "/"))
                .map(ProjectResource::getUrl).findFirst().orElse(null);
    }

    private String avatarOf(Integer userId) {
        return profileRepository.findByUserId(userId).map(Profile::getAvatarUrl).orElse(null);
    }

    private static String defaultCategory(String type) {
        return switch (type) {
            case "book" -> "Literatura";
            case "image" -> "Arte Digital";
            case "video" -> "Video";
            case "audio" -> "Música";
            default -> "General";
        };
    }

    private static int wordCount(String text) {
        if (text == null || text.isBlank()) return 0;
        return text.trim().split("\\s+").length;
    }

    private static String normalizeFolder(String folder) {
        if (folder == null || folder.isBlank()) return "/";
        String f = folder.trim().replace("\\", "/").replaceAll("/+", "/");
        if (f.contains("..")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ruta de carpeta no válida");
        if (!f.startsWith("/")) f = "/" + f;
        if (f.length() > 1 && f.endsWith("/")) f = f.substring(0, f.length() - 1);
        return f;
    }

    /** Ruta de los archivos que viven dentro de una carpeta */
    private static String childPath(ProjectResource folder) {
        String parent = folder.getFolder() == null ? "/" : folder.getFolder();
        return ("/".equals(parent) ? "" : parent) + "/" + folder.getName();
    }

    private ProjectResource findResource(Project project, Long resourceId) {
        return project.getResources().stream()
                .filter(r -> r.getId() != null && r.getId().equals(resourceId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no encontrado"));
    }

    private String saveResourceFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get("uploads/projects");
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase() : ".bin";
            if (!ext.matches("\\.[a-z0-9]{1,6}")) ext = ".bin";
            String newFilename = "resource_" + UUID.randomUUID().toString().substring(0, 12) + ext;
            Files.copy(file.getInputStream(), uploadPath.resolve(newFilename), StandardCopyOption.REPLACE_EXISTING);
            return newFilename;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el archivo", e);
        }
    }

    private void deleteStoredFile(String url) {
        if (url == null || !url.startsWith("/uploads/projects/")) return;
        try {
            Path path = Paths.get("uploads/projects").resolve(url.substring("/uploads/projects/".length())).normalize();
            if (path.startsWith(Paths.get("uploads/projects"))) Files.deleteIfExists(path);
        } catch (Exception e) {
            log.warn("No se pudo borrar el archivo {}: {}", url, e.getMessage());
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    private Project findProjectOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));
    }
}
