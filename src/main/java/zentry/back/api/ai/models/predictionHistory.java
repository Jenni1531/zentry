package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;
import java.sql.Timestamp;//importaciones para timestamp

@Entity
@Table(name = "prediction_history")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor

public class predictionHistory {
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name ="prediction_id", nullable = false)
    private int predictionId;

    @Column(name = "fecha", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp fecha;
}
