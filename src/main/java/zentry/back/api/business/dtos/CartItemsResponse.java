package zentry.back.api.business.dtos;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class CartItemsResponse {
    private Integer cartId;
    private Integer productId;
    private Integer cantidad;
}
