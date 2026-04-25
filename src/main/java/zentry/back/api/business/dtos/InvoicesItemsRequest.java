package zentry.back.api.business.dtos;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class InvoicesItemsRequest {
    private Integer invoiceId;
    private String descripcion;
    private BigDecimal precio;
}
