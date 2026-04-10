package zentry.back.api.business.models;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "marketplace_products", schema = "zentry_business")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class MarketplaceProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "seller_id", nullable = false)
    private Integer sellerId;

    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
}
