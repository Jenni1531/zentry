package zentry.back.api.core.dtos;

import java.time.LocalDateTime;
import java.util.List;
<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonProperty;
=======
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class PostResponse {

    private Integer id;

    private String authorUsername;
    private String authorAvatar;
    private String authorName;

    // Datos de la obra
    private String title;
    private String contenido;
<<<<<<< HEAD

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;

    @JsonProperty("image_url")
    private String imageUrl;

    private String visibility;
    private List<String> tools;
=======
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    private List<String> mediaUrls;
    private List<String> tags;
    
    // Estadísticas
    private Integer likesCount;
    private Integer commentsCount;
    
    private LocalDateTime createdAt;
<<<<<<< HEAD
    private LocalDateTime updatedAt;

    @JsonProperty("content")
    public String getContent() {
        return this.contenido;
    }

    @JsonProperty("type")
    public String getType() {
        return this.contentType != null ? this.contentType : "canvas";
    }
=======

>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
}
