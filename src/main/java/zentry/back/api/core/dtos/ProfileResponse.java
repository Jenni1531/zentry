package zentry.back.api.core.dtos;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ProfileResponse {

    private String username;
    private String name;
    private String artisticName;
    private String discipline;
    private String experienceLevel;
    private String rank;
    private String location;
    private String bio;
    private String avatarUrl;
    private String bannerUrl;
    private Boolean isPrivate;
    private Boolean showSavedPosts;
    private Boolean showLikedPosts;
    private Integer followersCount;
    private Integer followingCount;

    @Builder.Default
    @JsonProperty("isFollowing")
    private Boolean isFollowing = false;

    @JsonProperty("following")
    public Boolean getFollowing() {
        return Boolean.TRUE.equals(this.isFollowing);
    }

    public void setFollowing(boolean following) {
        this.isFollowing = following;
    }

    private LocalDateTime createdAt;
}

