package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_embeddings") //revisar los argsconstructor y no argsconstructor
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor

public class userEmbeddings {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @Column(name="user_id", nullable = false)
    private Integer userId;

    @Column(name="vetor", nullable = false)
    private String vetor;
}

