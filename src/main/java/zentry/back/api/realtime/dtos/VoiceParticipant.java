package zentry.back.api.realtime.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class VoiceParticipant {
    private Integer userId;
    private String username;
    private String avatarUrl;
    private boolean muted;
    private long joinedAt;
    @JsonIgnore
    private String principalName;
    @JsonIgnore
    private String sessionId;
}
