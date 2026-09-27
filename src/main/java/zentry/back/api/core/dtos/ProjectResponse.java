package zentry.back.api.core.dtos;

import lombok.*;
import zentry.back.api.core.models.ProjectActivity;
import zentry.back.api.core.models.ProjectNote;
import zentry.back.api.core.models.ProjectResource;
import zentry.back.api.core.models.ProjectTask;

import java.time.LocalDateTime;
import java.util.List;

/** Proyecto + permisos del usuario que lo consulta (el frontend decide qué acciones mostrar). */
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ProjectResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private String status;
    private String deadline;
    private String createdBy;
    private String ownerUsername;
    private String ownerAvatarUrl;
    private String tags;
    private String visibility;
    private String projectType;
    private String coverUrl;
    private Integer conversationId;
    private Integer studioProjectId;
    private Integer publishedPostId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ProjectTask> tasks;
    private List<ProjectResource> resources;
    private List<ProjectActivity> activities;
    private List<ProjectNote> notes;

    private long membersCount;
    private long chaptersCount;
    private long likesCount;
    private boolean liked;

    /** Permisos del usuario autenticado */
    private boolean owner;
    private boolean member;
}
