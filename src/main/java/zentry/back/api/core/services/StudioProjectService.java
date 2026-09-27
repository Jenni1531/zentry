package zentry.back.api.core.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import zentry.back.api.core.dtos.PostRequest;
import zentry.back.api.core.dtos.StudioProjectRequest;
import zentry.back.api.core.dtos.StudioProjectResponse;
import zentry.back.api.core.models.ContentType;
import zentry.back.api.core.models.StudioProject;
import zentry.back.api.core.models.User;
import zentry.back.api.core.repositories.StudioProjectRepository;
import zentry.back.api.core.repositories.UserRepository;
import zentry.back.api.core.mappers.CoreMappers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class StudioProjectService {

    private final StudioProjectRepository repo;
    private final UserRepository userRepo;
    private final PostService postService;
    private final ProjectService projectService;

    public StudioProjectService(StudioProjectRepository repo, UserRepository userRepo, PostService postService,
                                ProjectService projectService) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.postService = postService;
        this.projectService = projectService;
    }

    private boolean isOwner(StudioProject project, User user, String userEmail) {
        String owner = project.getOwnerUsername();
        return owner != null && (owner.equalsIgnoreCase(user.getHandle() != null ? user.getHandle() : userEmail)
                || owner.equalsIgnoreCase(userEmail));
    }

    /** Dueño, o miembro del proyecto colaborativo al que pertenece la obra */
    private boolean canEdit(StudioProject project, User user, String userEmail) {
        return isOwner(project, user, userEmail)
                || (project.getProjectId() != null && projectService.isMember(project.getProjectId(), userEmail));
    }

    public List<StudioProjectResponse> getUserProjects(String userEmail, ContentType type) {
        User user = findUserByEmail(userEmail);
        String username = user.getUsername() != null ? user.getUsername() : userEmail;

        // Incluye proyectos guardados con el email como dueño (antes de elegir @username en el onboarding)
        List<String> owners = List.of(username, user.getEmail());
        List<StudioProject> projects;
        if (type != null) {
            projects = repo.findByOwnerUsernameInAndTypeOrderByLastEditedAtDesc(owners, type);
        } else {
            projects = repo.findByOwnerUsernameInOrderByLastEditedAtDesc(owners);
        }

        return projects.stream()
                .map(CoreMappers::toResponse)
                .collect(Collectors.toList());
    }

    public StudioProjectResponse createProject(String userEmail, StudioProjectRequest request, MultipartFile file) {
        User user = findUserByEmail(userEmail);
        String username = user.getUsername() != null ? user.getUsername() : userEmail;

        String mediaUrl = request.getMediaUrl();
        if (file != null && !file.isEmpty()) {
            mediaUrl = "/uploads/studio/" + saveFile(file);
        }

        Integer rewardCoins = request.getRewardCoins() != null ? request.getRewardCoins() : 50;

        StudioProject project = StudioProject.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .contentData(request.getContentData())
                .mediaUrl(mediaUrl)
                .tools(request.getTools() != null ? request.getTools() : new ArrayList<>())
                .rewardCoins(rewardCoins)
                .ownerUsername(username)
                .build();

        StudioProject saved = repo.save(project);
        return CoreMappers.toResponse(saved);
    }

    public StudioProjectResponse getProjectDetail(Integer id, String userEmail) {
        StudioProject project = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));
        User user = findUserByEmail(userEmail);
        // Antes cualquiera podía leer cualquier obra por id: solo dueño, colaboradores o si ya está publicada
        if (!canEdit(project, user, userEmail) && !Boolean.TRUE.equals(project.getPublished())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes acceso a esta obra");
        }

        return CoreMappers.toResponse(project);
    }

    public StudioProjectResponse updateProject(Integer id, String userEmail, StudioProjectRequest request, MultipartFile file) {
        User user = findUserByEmail(userEmail);
        String username = user.getUsername() != null ? user.getUsername() : userEmail;

        StudioProject project = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));

        if (!canEdit(project, user, userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para modificar este proyecto");
        }
        project.setLastEditedBy(user.getHandle() != null ? user.getHandle() : username);

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            project.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getContentData() != null) {
            project.setContentData(request.getContentData());
        }
        if (request.getType() != null) {
            project.setType(request.getType());
        }
        if (file != null && !file.isEmpty()) {
            project.setMediaUrl("/uploads/studio/" + saveFile(file));
        } else if (request.getMediaUrl() != null) {
            project.setMediaUrl(request.getMediaUrl());
        }
        if (request.getTools() != null) {
            project.setTools(request.getTools());
        }

        StudioProject updated = repo.save(project);
        return CoreMappers.toResponse(updated);
    }

    public StudioProjectResponse publishProject(Integer id, String userEmail) {
        User user = findUserByEmail(userEmail);
        String username = user.getUsername() != null ? user.getUsername() : userEmail;

        StudioProject project = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));

        if (!project.getOwnerUsername().equalsIgnoreCase(username) && !project.getOwnerUsername().equalsIgnoreCase(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para publicar este proyecto");
        }

        if (Boolean.TRUE.equals(project.getPublished())) {
            return CoreMappers.toResponse(project);
        }

        String contentType = switch (project.getType()) {
            case CANVAS -> "image";
            case DOCUMENT -> "text";
            case IMAGE -> "image";
            case VIDEO -> "video";
            case AUDIO -> "audio";
        };

        PostRequest postRequest = PostRequest.builder()
                .title(project.getTitle())
                .contenido(project.getDescription())
                .contentType(contentType)
                .visibility("public")
                .imageUrl(project.getMediaUrl())
                .thumbnailUrl(project.getMediaUrl())
                .tools(project.getTools() != null ? String.join(",", project.getTools()) : null)
                .build();

        // syncToStudio=false: este proyecto ya vive en el Estudio, no debe duplicarse
        var createdPost = postService.create(userEmail, postRequest, null, false);

        project.setPublished(true);
        project.setPostId(createdPost.getId());
        StudioProject saved = repo.save(project);
        return CoreMappers.toResponse(saved);
    }

    public void deleteProject(Integer id, String userEmail) {
        User user = findUserByEmail(userEmail);
        String username = user.getUsername() != null ? user.getUsername() : userEmail;

        StudioProject project = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));

        if (!project.getOwnerUsername().equalsIgnoreCase(username) && !project.getOwnerUsername().equalsIgnoreCase(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar este proyecto");
        }

        repo.delete(project);
    }

    private User findUserByEmail(String userEmail) {
        return userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get("uploads/studio");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".bin";
            String newFilename = "studio_" + UUID.randomUUID().toString().substring(0, 8) + extension;

            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return newFilename;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el archivo multimedia", e);
        }
    }
}
