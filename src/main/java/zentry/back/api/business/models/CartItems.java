package zentry.back.api.business.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items", schema = "zentry_business")
@IdClass(CartItems.CartItemsId.class)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CartItems {

    @Id
    @Column(name = "cart_id")
    private Integer cartId;

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "cantidad")
    private Integer cantidad;

    // Clase interna para la clave primaria compuesta
    @lombok.EqualsAndHashCode
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CartItemsId implements java.io.Serializable {
        private Integer cartId;
        private Integer productId;
    }
}
