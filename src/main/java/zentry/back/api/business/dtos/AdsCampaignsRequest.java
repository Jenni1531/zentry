package zentry.back.api.business.dtos;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AdsCampaignsRequest {
    private Integer userId;
    private String nombre;
}
