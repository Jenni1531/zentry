package zentry.back.api.business.models;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;



@Entity 
@Table(name = "payments", schema = "zentry_business")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor

public class Payments {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;        

    @Column(name ="user_id", nullable = false)
    private Integer userId;

    @Column(name ="payment_method_id", nullable = false)
    private Integer paymentMethodId;

    @Column(name ="amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
}
