package zentry.back.api.business.dtos;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AffiliatePayoutsRequest {
    private Integer affiliateId;
    private BigDecimal amount;
}
