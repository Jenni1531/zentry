package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ai_models", schema = "zentry_ai")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor

public class aiModels {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", length = 100)
    private String nombre;
}
