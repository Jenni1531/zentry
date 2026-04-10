package zentry.back.api.core.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "forum_replies", schema = "zentry_core")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ForumReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "thread_id")
    private Integer threadId;
}
