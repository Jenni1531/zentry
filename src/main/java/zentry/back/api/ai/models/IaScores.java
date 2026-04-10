package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.math.BigDecimal;



@Entity
@Table(name = "ai_scores", schema = "zentry_ai")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor

public class IaScores {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="post_id", nullable = false)
    private int postId;

    @Column(name="score", nullable = false, precision = 5, scale = 4)
    private BigDecimal score;

    
}
