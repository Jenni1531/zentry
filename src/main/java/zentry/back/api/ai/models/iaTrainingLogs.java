package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.sql.Timestamp;

@Entity 
@Table (name="ia_training_logs")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor


public class iaTrainingLogs {
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "model_id")
    private Integer modelId;

    @Column(name = "estado", length = 50, nullable = false)
    private String estado;

    @Column(name = "fecha", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp fecha;
}
