package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "tag_embeddings")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor

public class tagEmbeddings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "tag_id", nullable = false)
    private Integer tagId;

    @Column(name = "vetor", nullable = false)
    private String vetor;
    
}
