package zentry.back.api.core.dtos;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ProjectChapterRequest {
    @Size(max = 200)
    private String title;
    private String content;
    /** Nueva posición (1 = primero) para reordenar */
    private Integer position;
}
