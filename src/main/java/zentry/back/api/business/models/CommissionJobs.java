package zentry.back.api.business.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "commission_jobs", schema = "zentry_business")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommissionJobs {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;
}
