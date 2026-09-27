package zentry.back.api.core.dtos;

import jakarta.validation.constraints.Size;
import lombok.*;

/** Renombrar o mover un archivo del drive del proyecto */
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ProjectResourceUpdateRequest {
    @Size(max = 255)
    private String name;
    @Size(max = 255)
    private String folder;
}
