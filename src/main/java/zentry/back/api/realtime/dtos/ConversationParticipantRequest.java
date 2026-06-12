package zentry.back.api.realtime.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ConversationParticipantRequest {

    @NotNull
    private Integer conversationId;

    @NotNull
    private Integer userId;

    @Size(max = 20)
    private String role;
}
