package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity 
@Table(name="ia_training_data")
    @Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor

    public class iaTrainingData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name=" data_input", nullable = false)
    private String dataInput;

    @Column(name="data_output", nullable = false)
    private String dataOutput;

    }