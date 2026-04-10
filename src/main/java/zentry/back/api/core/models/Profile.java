package zentry.back.api.core.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profiles", schema = "zentry_core")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", unique = true)
    private Integer userId;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
}
