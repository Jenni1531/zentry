package zentry.back.api.core.dtos;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ForumReplyResponse {

    private Integer id;
    private Integer threadId;
}
