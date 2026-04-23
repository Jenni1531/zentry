package zentry.back.api.ai.dtos;

import lombok.*;
import java.util.UUID;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class FeedbackLabelsRequest {
    private Integer feedbackId;
    private String etiqueta;
}
