package zentry.back.api.core.dtos;

import java.time.LocalDateTime;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ProfileResponse {

    private String username;
    private String name;
    private String discipline;
    private String location;
    private String bio;
    private String avatarUrl;
    private String bannerUrl;
    private Integer followersCount;
    private Integer followingCount;
    private LocalDateTime createdAt;
}
