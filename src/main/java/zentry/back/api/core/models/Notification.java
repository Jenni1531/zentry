package zentry.back.api.core.models;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "CoreNotification")
@Table(name = "notifications", schema = "zentry_core")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;
}
