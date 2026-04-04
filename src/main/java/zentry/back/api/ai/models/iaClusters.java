package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;


@Entity
@Table(name ="ia_clusters")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor


public class iaClusters {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @Column(name="description", length = 100, nullable = false)
    private String description;
    
}
