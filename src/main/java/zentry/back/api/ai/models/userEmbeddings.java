package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "user_embeddings", schema = "zentry_ai")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class userEmbeddings {
    @Id
    private Integer userId;

    @Column(name = "vector", nullable = false)
    private String vector;
}
