package zentry.back.api.core.dtos;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ProfileResponse {

    private Integer userId;
    private String username;
    /** Presencia real (ping de los últimos 90 s) */
    private Boolean isOnline;
    private LocalDateTime lastSeen;
    private String name;
    private String artisticName;
    private String discipline;
    private String experienceLevel;
    private String rank;
    private String location;
    private String bio;
    private String specialties;
    private String birthDate;
    private Boolean onboardingCompleted;

    @JsonProperty("avatar_url")
    @JsonAlias({"avatar_url", "avatarUrl"})
    private String avatarUrl;

    @JsonProperty("banner_url")
    @JsonAlias({"banner_url", "bannerUrl"})
    private String bannerUrl;

    @JsonProperty("avatarUrl")
    public String getAvatarUrlCamelCase() {
        return this.avatarUrl;
    }

    @JsonProperty("bannerUrl")
    public String getBannerUrlCamelCase() {
        return this.bannerUrl;
    }

    private Boolean isPrivate;
    private Boolean showSavedPosts;
    private Boolean showLikedPosts;
    private Boolean notifyReactions;
    private Boolean notifyComments;
    private Boolean notifyMessages;
    private Boolean notifyStories;
    private Integer followersCount;
    /** Marco, mascota y título equipados */
    private CosmeticsResponse cosmetics;
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
