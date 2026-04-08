package zentry.back.api.business.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "affiliate_program", schema = "zentry_business")      
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor 

public class affiliateProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

  @Column (name ="user_id", nullable = false)
  private Integer userId;
    
}
