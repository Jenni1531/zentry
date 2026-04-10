package zentry.back.api.core.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "communities", schema = "zentry_core")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", length = 100)
    private String nombre;
}
