package zentry.back.api.business.dtos;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class SubscriptionsRequest {
    private Integer userId;
    private Integer planId;
}
