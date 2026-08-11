package zentry.back.api.core.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class PostRequest {
@NotBlank(message = "El título es obligatorio")
    private String title;

    private String contenido;

    private List<String> mediaUrls;

    private List<Integer> tagIds;
    private MultipartFile image;
    private String tools;
}
