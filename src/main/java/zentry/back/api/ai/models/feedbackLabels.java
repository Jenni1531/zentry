package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;


@Entity
@Table(name="feedback_labels")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor

public class feedbackLabels {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name=" feedback_id", nullable = false)
    private int feedbackId;   

    @Column(name="etiqueta", length = 50, nullable = false)
    private String etiqueta;
    
}
