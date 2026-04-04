package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name ="ia_similarity_users")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor    

public class iaPrompts {
        @Id
        @GeneratedValue(strategy= GenerationType.AUTO)
        @Column(name="id")
        private UUID id;

        @Column(name="user_id", nullable = false)
        private Integer userId;

        @Column (name = "prompt", nullable = false)
        private String prompt;
}
