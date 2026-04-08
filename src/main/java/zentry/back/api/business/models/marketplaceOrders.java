package zentry.back.api.business.models;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "marketplace_orders", schema = "zentry_business")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor

public class marketplaceOrders {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="buyer_id", nullable = false)
    private Integer buyerId;

    @Column(name="total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
}
