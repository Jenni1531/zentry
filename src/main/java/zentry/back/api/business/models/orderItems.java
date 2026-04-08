package zentry.back.api.business.models;
import jakarta.persistence.*;
import lombok.*;

@Entity 
@Table(name = "order_items", schema = "zentry_business")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor

public class orderItems {
    
   @Id
    @Column(name = "order_id")
    private Integer orderId;

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "cantidad")
    private Integer cantidad;
}
