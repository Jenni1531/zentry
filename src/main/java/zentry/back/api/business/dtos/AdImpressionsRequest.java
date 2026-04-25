package zentry.back.api.business.dtos;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AdImpressionsRequest {
    private Integer campaignId;
    private Integer vistas;
}
