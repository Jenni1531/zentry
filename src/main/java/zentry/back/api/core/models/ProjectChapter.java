package zentry.back.api.core.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Capítulo de un proyecto tipo libro (estilo Wattpad). */
@Entity
@Table(name = "project_chapters", schema = "zentry_core")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProjectChapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "word_count")
    private Integer wordCount;

    @Column(name = "author_email", length = 100)
    private String authorEmail;

    @Column(name = "last_edited_by", length = 100)
    private String lastEditedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
