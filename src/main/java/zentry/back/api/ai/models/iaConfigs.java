package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;


@Entity 
@Table(name="ia_configs")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor
 

public class iaConfigs{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

   @Column(name= "model_id", nullable = false )
   private Integer modelid;

   @Column(name="parametros", nullable = false, precision = 5, scale = 4)
    private String parametros ;
}