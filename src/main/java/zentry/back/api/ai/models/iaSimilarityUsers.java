package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;


@Entity 
@Table(name ="ia_similarity_users")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor

public class iaSimilarityUsers {
        @Id
        @GeneratedValue(strategy= GenerationType.AUTO)
        @Column(name="id")
        private UUID id;

        @Column(name="user1", nullable = false)
        private int user1;

        @Column(name="user2", nullable = false)
        private int user2;

        @Column(name="score", nullable = false, precision = 5, scale = 4)
        private BigDecimal score;   

}
