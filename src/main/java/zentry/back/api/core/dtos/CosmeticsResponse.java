package zentry.back.api.core.dtos;

import lombok.*;

/** Cosméticos equipados de un usuario, para mostrarlos junto a su avatar en toda la plataforma. */
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class CosmeticsResponse {
    /** COMMON | RARE | EPIC | LEGENDARY (define el anillo del marco) */
    private String frameRarity;
    private String frameName;
    private String petIcon;
    private String petName;
    private String titleIcon;
    private String titleName;
}
