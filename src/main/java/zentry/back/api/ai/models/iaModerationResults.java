package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;


@Entity
@Table (name="ia_moderation_results")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor

public class iaModerationResults {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name="post_id", nullable = false)
    private int postId;

    @Column (name="Resultado", length =50 )
    private String resultado;
    
}
