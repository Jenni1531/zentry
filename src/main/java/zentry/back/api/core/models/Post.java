package zentry.back.api.core.models;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;;

@Entity
@Table(name = "posts", schema = "zentry_core")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "contenido", columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "image_url")
    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "post_tools", schema = "zentry_core", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tool")
    private List<String> tools;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

    /*
    @Column(name = "is_published")
    private Boolean isPublished = false;
    */
   
    }
    

}
