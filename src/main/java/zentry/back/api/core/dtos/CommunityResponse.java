package zentry.back.api.core.dtos;

import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class CommunityResponse {

    private Integer id;
    private String slug;
    private String nombre;
    private String descripcion;
    private String imageUrl;
    private String avatarUrl;
    private String bannerUrl;
    private String categoria;
    private Integer creatorId;
    private String ownerUsername;
    private List<String> rules;
    private Integer membersCount;
    private Boolean isJoined;
    private String privacy;
    /** Rol del usuario que consulta: ADMIN | MODERATOR | MEMBER | PENDING | null */
    private String myRole;
    private Boolean isAdmin;
    private Boolean isOwner;
    private Boolean hasPendingRequest;
    /** Solicitudes pendientes (solo se envía a administradores) */
    private Integer pendingCount;
    private LocalDateTime createdAt;
}
