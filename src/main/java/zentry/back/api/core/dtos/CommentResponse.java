package zentry.back.api.core.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class CommentResponse {

    private Integer id;
    private Integer postId;
    private Integer userId;
    private String authorUsername;
    private String authorAvatarUrl;
    private CosmeticsResponse authorCosmetics;
    private String content;
    private LocalDateTime createdAt;

    @Builder.Default
    private Integer likesCount = 0;

    @Builder.Default
    private Boolean liked = false;

    @Builder.Default
    private Boolean canEdit = false;

    @Builder.Default
    private Boolean canDelete = false;
}
