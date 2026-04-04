package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity 
@Table(name="community_embeddings")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor

public class communityEmbeddings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "community_id", nullable = false)
    private Integer communityId;

    @Column(name = "vetor", nullable = false)
    private String vetor;
}