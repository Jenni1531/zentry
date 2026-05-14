package zentry.back.api.core.dtos;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class CommunityResponse {

    private Integer id;
    private String nombre;
}
