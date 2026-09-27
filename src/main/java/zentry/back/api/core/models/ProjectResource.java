package zentry.back.api.core.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_resources", schema = "zentry_core")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String type; // "PDF", "FIGMA", "ZIP", "PNG"
    private String size;
    private String url;
    private String uploadedBy;

    /** Carpeta dentro del drive del proyecto, ej. "/", "/Referencias", "/Audio/Stems" */
    @Column(name = "folder", length = 255)
    @Builder.Default
    private String folder = "/";

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "mime_type", length = 120)
    private String mimeType;

    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;

    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) uploadedAt = LocalDateTime.now();
    }
}
