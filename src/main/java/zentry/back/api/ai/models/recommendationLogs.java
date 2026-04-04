package zentry.back.api.ai.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.sql.Timestamp;  // Importaciones para timestamp


@Entity 
@Table (name= "recommendation_logs")
@Getter @Setter  @Builder @NoArgsConstructor @AllArgsConstructor

public class recommendationLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "recommendation_id", nullable = false)
     private  int recommendationId;

     @Column(name = "timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")//solucion a timestamp
      private Timestamp timestamp;  


      //otra opcion por correcion
      //@Column(name = "timestamp", nullable = false)
      // private string timestamp; // con las importaciones establecidas anteriores 
  
}
