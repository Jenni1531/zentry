package zentry.back.api.core.services;

import org.springframework.stereotype.Service;
import zentry.back.api.core.dtos.CosmeticsResponse;
import zentry.back.api.core.models.StoreItem;
import zentry.back.api.core.models.UserEquippedItem;
import zentry.back.api.core.repositories.StoreItemRepository;
import zentry.back.api.core.repositories.UserEquippedItemRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Resuelve en lote los cosméticos equipados (marco, mascota, título) de varios usuarios.
 * El catálogo de la tienda es pequeño y casi estático: se cachea en memoria unos minutos
 * para no consultarlo en cada publicación/comentario/conversación.
 */
@Service
public class CosmeticsService {

    private static final long CATALOG_TTL_MS = 5 * 60 * 1000;

    private final UserEquippedItemRepository equippedRepo;
    private final StoreItemRepository storeItemRepo;

    private volatile Map<Integer, StoreItem> catalog = Map.of();
    private volatile long catalogLoadedAt = 0;

    public CosmeticsService(UserEquippedItemRepository equippedRepo, StoreItemRepository storeItemRepo) {
        this.equippedRepo = equippedRepo;
        this.storeItemRepo = storeItemRepo;
    }

    private Map<Integer, StoreItem> catalog() {
        if (System.currentTimeMillis() - catalogLoadedAt > CATALOG_TTL_MS) {
            catalog = storeItemRepo.findAll().stream().collect(Collectors.toMap(StoreItem::getId, i -> i, (a, b) -> a));
            catalogLoadedAt = System.currentTimeMillis();
        }
        return catalog;
    }

    public Map<Integer, CosmeticsResponse> forUsers(Collection<Integer> userIds) {
        Set<Integer> ids = userIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) return Map.of();

        Map<Integer, StoreItem> items = catalog();
        Map<Integer, CosmeticsResponse> result = new HashMap<>();
        for (UserEquippedItem eq : equippedRepo.findByUserIdIn(ids)) {
            StoreItem item = items.get(eq.getStoreItemId());
            if (item == null || item.getType() == null) continue;
            CosmeticsResponse c = result.computeIfAbsent(eq.getUserId(), k -> new CosmeticsResponse());
            switch (item.getType().toLowerCase()) {
                case "frames" -> { c.setFrameRarity(item.getRarity()); c.setFrameName(item.getName()); }
                case "pets" -> { c.setPetIcon(item.getImageUrl()); c.setPetName(item.getName()); }
                case "titles" -> { c.setTitleIcon(item.getImageUrl()); c.setTitleName(item.getName()); }
                default -> { /* temas y banners no se muestran junto al avatar */ }
            }
        }
        return result;
    }

    public CosmeticsResponse forUser(Integer userId) {
        return forUsers(userId == null ? List.of() : List.of(userId)).get(userId);
    }
}
