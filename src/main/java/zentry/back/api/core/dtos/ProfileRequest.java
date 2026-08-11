package zentry.back.api.core.dtos;

import org.springframework.web.multipart.MultipartFile;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ProfileRequest {

    private String name;
    private String discipline;
    private String location;
    private String bio;
    private MultipartFile avatar;
    private MultipartFile banner;
    
}
