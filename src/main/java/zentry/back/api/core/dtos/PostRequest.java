package zentry.back.api.core.dtos;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;
=======
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class PostRequest {
<<<<<<< HEAD

    @NotBlank(message = "El título es obligatorio")
    private String title;

    @JsonAlias({"content", "contenido"})
    private String contenido;

    @JsonAlias({"content_type", "type", "contentType"})
    private String contentType;

    @JsonAlias({"thumbnail_url", "imageBlob", "thumbnailUrl"})
    private String thumbnailUrl;

    @JsonAlias({"image_url", "imageUrl"})
    private String imageUrl;

    private String visibility;

    private List<String> mediaUrls;
=======
@NotBlank(message = "El título es obligatorio")
    private String title;

    private String contenido;

    private List<String> mediaUrls;

>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    private List<Integer> tagIds;
    private MultipartFile image;
    private String tools;
}
