package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="ia_similarity_content")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor

public class iaSimilarityContent {
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @Column(name="post_id_1", nullable = false)
    private int postId1;

    @Column (name ="post_id_2", nullable = false)
    private int postId2;

    @Column(name="score", nullable = false, precision = 5, scale = 4)
    private BigDecimal score;
}
