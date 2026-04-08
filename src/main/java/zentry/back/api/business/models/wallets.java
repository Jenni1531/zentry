package zentry.back.api.business.models;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "wallets", schema = "zentry_business")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor

public class wallets {
    
    @Id
    @Column(name = "user_id", nullable = false) 
    private Integer userId;

    @Column(name = "balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;
}

