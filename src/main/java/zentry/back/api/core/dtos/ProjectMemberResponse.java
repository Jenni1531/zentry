package zentry.back.api.core.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ProjectMemberResponse {
    private Long projectId;
    private String username;
    /** @usuario visible (username guarda el email, que es la clave única) */
    private String handle;
    private Integer userId;
    private String name;
    private String avatarUrl;
    private CosmeticsResponse cosmetics;
    private String role;
    private LocalDateTime joinedAt;
}
