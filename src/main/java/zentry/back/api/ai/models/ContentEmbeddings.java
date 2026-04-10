package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "content_embeddings", schema = "zentry_ai")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ContentEmbeddings {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "post_id", nullable = false)
    private Integer postId;

    @Column(name = "vector", nullable = false)
    private String vector;
}
