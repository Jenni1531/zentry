package zentry.back.api.realtime.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class MessageRequest {

    @NotNull
    private Integer conversationId;

    @NotNull
    private Integer senderId;

    private String content;

    @Size(max = 10)
    private String type;

    private Boolean read;
}
