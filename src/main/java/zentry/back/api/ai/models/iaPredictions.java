package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;  

@Entity 
@Table(name = "ia_predictions")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor

public class iaPredictions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

   @Column(name="model_id", nullable = false)
   private Integer modelId;

   @Column (name="resultado", nullable = false)
    private String resultado;
}
 