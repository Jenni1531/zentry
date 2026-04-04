package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;


@Entity 
@Table(name ="ia_content_generation")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor

public class iaContentGeneration {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id")
    private UUID id;


    @Column (name= "prompt_id", nullable = false)
    private int promptId;

    @Column (name="resultado", nullable = false)
    private String resultado;
    
}
