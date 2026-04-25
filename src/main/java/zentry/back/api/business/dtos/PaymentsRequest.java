package zentry.back.api.business.dtos;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class PaymentsRequest {
    private Integer userId;
    private Integer paymentMethodId;
    private BigDecimal amount;
}
