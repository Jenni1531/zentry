package zentry.back.api.business.dtos;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OrderItemsResponse {
    private Integer orderId;
    private Integer productId;
    private Integer cantidad;
}
