package zentry.back.api.business.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;


@Entity
@Table(name = "ads_campaigns", schema = "zentry_business")              
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor     

public class AdsCampaigns {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @Column(name = "user_id", nullable = false)
        private Integer userId;

        @Column(name="nombre", length = 100, nullable = false)
        private String nombre;
}
