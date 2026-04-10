package zentry.back.api.business.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items", schema = "zentry_business")
@IdClass(OrderItems.OrderItemsId.class)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor

public class OrderItems {

    @Id
    @Column(name = "order_id")
    private Integer orderId;

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "cantidad")
    private Integer cantidad;

    // Clase interna para la clave primaria compuesta
    @lombok.EqualsAndHashCode
    public static class OrderItemsId implements java.io.Serializable {
        private Integer orderId;
        private Integer productId;
    }
}
