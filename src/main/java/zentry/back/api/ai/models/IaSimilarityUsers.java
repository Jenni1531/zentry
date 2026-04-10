package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ai_similarity_users", schema = "zentry_ai")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class IaSimilarityUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user1", nullable = false)
    private Integer user1;

    @Column(name = "user2", nullable = false)
    private Integer user2;

    @Column(name = "score", nullable = false, precision = 5, scale = 4)
    private BigDecimal score;
}
