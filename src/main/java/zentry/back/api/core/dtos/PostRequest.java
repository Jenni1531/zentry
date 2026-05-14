package zentry.back.api.core.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class PostRequest {

    @NotNull
    private Integer userId;

    private String contenido;
}
