package zentry.back.api.core.dtos;

import java.time.LocalDateTime;
import java.util.List;
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
    private List<String> mediaUrls;
    private List<String> tags;
    
    // Estadísticas
    private Integer likesCount;
    private Integer commentsCount;
    
    private LocalDateTime createdAt;

}
