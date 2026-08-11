package zentry.back.api.core.models;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profiles", schema = "zentry_core")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", unique = true, nullable = false)
    private Integer userId;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "name")
    private String name;

    @Column(name = "discipline")
    private String discipline;

    @Column(name = "location")
    private String location;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
