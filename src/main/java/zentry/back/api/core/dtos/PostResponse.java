package zentry.back.api.core.dtos;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class PostResponse {

    private Integer id;
    private Integer userId;
    private String contenido;
}
