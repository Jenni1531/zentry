package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;


@Entity 
@Table (name ="ia_scores")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor

public class iaScores {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name="post_id", nullable = false)
    private int postId;

    @Column(name="score", nullable = false, precision = 5, scale = 4)
    private BigDecimal score;

    
}
