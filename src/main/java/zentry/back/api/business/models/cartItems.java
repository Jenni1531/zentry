package zentry.back.api.business.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items", schema = "zentry_business")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor 

public class cartItems {
   @Id
    @Column(name = "cart_id")
    private Integer cartId;

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "cantidad")
    private Integer cantidad;
}
