package zentry.back.api.core.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class CommunityRequest {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    private String slug;

    private String categoria;

    private String descripcion;

    private String avatarUrl;

    private String bannerUrl;

    private List<String> rules;
}
