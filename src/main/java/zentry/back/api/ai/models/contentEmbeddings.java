package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "content_embeddings") //revisar los argsconstructor y no argsconstructor
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor
 
public class contentEmbeddings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "post_id", nullable = false)
    private int  postId;

    @Column(name = "vetor", nullable = false)
    private String vetor;
}
