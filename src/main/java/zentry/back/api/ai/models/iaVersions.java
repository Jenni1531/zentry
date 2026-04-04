package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "ia_versions") //revisar los argsconstructor y no argsconstructor
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor

public class iaVersions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name= "model_id", nullable = false )
    private int modelId;

    @Column(name= "version", nullable = false )
    private char version;
}
