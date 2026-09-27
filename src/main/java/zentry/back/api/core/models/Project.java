package zentry.back.api.core.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects", schema = "zentry_core")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category; // "UI/UX", "Arte Digital", "Desarrollo", etc.
    private String priority; // "baja", "media", "alta", "urgente"
    private String status;   // "active", "completed", "paused"
    private String deadline; // "28 Feb 2026"

    @Column(name = "created_by", nullable = false)
    private String createdBy; // username o email del creador

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Relación con Tareas
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectTask> tasks = new ArrayList<>();

    // Relación con Recursos / Archivos
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectResource> resources = new ArrayList<>();

    // Relación con Historial de Actividades
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectActivity> activities = new ArrayList<>();

    // Relación con Notas del Equipo
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectNote> notes = new ArrayList<>();

    // Etiquetas separadas por comas (ej. "Next.js,Tailwind")
    private String tags;

    /** private = solo miembros · public = cualquiera puede verlo (solo lectura) y aparece en Explorar */
    @Column(name = "visibility", length = 20)
    @Builder.Default
    private String visibility = "private";

    /** general · book (libro por capítulos) · image · video · audio (obra compartida en el editor) */
    @Column(name = "project_type", length = 20)
    @Builder.Default
    private String projectType = "general";

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    /** Chat de grupo del proyecto (solo miembros) */
    @Column(name = "conversation_id")
    private Integer conversationId;

    /** Obra compartida en el editor del Estudio (proyectos de imagen, video o audio) */
    @Column(name = "studio_project_id")
    private Integer studioProjectId;

    /** Publicación del feed creada al completar el proyecto */
    @Column(name = "published_post_id")
    private Integer publishedPostId;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
