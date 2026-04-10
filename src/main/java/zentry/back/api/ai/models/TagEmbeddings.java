package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "tag_embeddings", schema = "zentry_ai")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class TagEmbeddings {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tag_id", nullable = false)
    private Integer tagId;

    @Column(name = "vector", nullable = false)
    private String vector;
}
