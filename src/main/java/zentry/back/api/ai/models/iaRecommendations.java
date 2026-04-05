package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ai_recommendations", schema = "zentry_ai")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class iaRecommendations {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "contenido_id", nullable = false)
    private Integer contenidoId;

    @Column(name = "score", nullable = false, precision = 5, scale = 4)
    private BigDecimal score;
}
