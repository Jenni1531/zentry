package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity 
@Table(name="ia_recommendations")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor

public class iaRecommendations {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

   @Column(name= "recommendation_id", nullable = false )
   private int recommendationId;

   @Column(name="score", nullable = false, precision = 5, scale = 4)
    private BigDecimal score;
}

