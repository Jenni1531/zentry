package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ia_behaivor_analitycs") //revisar los argsconstructor y no argsconstructor
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor
public class iaBehaivorAnalitycs {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "score", nullable = false, precision = 5, scale = 4)
    private BigDecimal score;

}
