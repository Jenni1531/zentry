package zentry.back.api.business.models;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "invoice_items", schema = "zentry_business")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor

public class invoicesItems {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column (name = "invoice_id", nullable = false)
    private Integer invoiceId;

    @Column (name = "description",  length = 255, nullable = false)
    private String description;

    @Column (name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

}
