package zentry.back.api.core.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.StoreItemResponse;
import zentry.back.api.core.dtos.StorePurchaseResponse;
import zentry.back.api.core.models.StoreItem;
import zentry.back.api.core.models.StorePurchase;
import zentry.back.api.core.models.TransactionType;
import zentry.back.api.core.models.User;
import zentry.back.api.core.models.Wallet;
import zentry.back.api.core.models.WalletTransaction;
import zentry.back.api.core.models.UserEquippedItem;
import zentry.back.api.core.repositories.StoreItemRepository;
import zentry.back.api.core.repositories.StorePurchaseRepository;
import zentry.back.api.core.repositories.UserEquippedItemRepository;
import zentry.back.api.core.repositories.UserRepository;
import zentry.back.api.core.repositories.WalletRepository;
import zentry.back.api.core.repositories.WalletTransactionRepository;
import zentry.back.api.global.mappers;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StoreService {

    private final StoreItemRepository storeItemRepo;
    private final StorePurchaseRepository storePurchaseRepo;
    private final WalletRepository walletRepo;
    private final WalletTransactionRepository txRepo;
    private final UserRepository userRepo;
    private final UserEquippedItemRepository equippedRepo;
    private final WalletService walletService;
    private final GamificationEventService gamificationEventService;

    public StoreService(StoreItemRepository storeItemRepo, StorePurchaseRepository storePurchaseRepo,
                        WalletRepository walletRepo, WalletTransactionRepository txRepo, UserRepository userRepo,
                        UserEquippedItemRepository equippedRepo, WalletService walletService,
                        @org.springframework.context.annotation.Lazy GamificationEventService gamificationEventService) {
        this.gamificationEventService = gamificationEventService;
        this.storeItemRepo = storeItemRepo;
        this.storePurchaseRepo = storePurchaseRepo;
        this.walletRepo = walletRepo;
        this.txRepo = txRepo;
        this.userRepo = userRepo;
        this.equippedRepo = equippedRepo;
        this.walletService = walletService;
    }

    // El campo "type" guarda la categoría (frames/pets/banners/themes/titles) y
    // "imageUrl" guarda el emoji del ítem: la tienda es 100% visual con iconos, sin imágenes reales.
    // Nombres anteriores -> nuevos: se renombra la fila existente para no perder compras ni equipados
    private static final Map<String, String> RENAMED_ITEMS = Map.of(
            "Tema Galaxia Violeta Profunda", "Tema Galaxia Violeta",
            "Zorro Kitsune Astral", "Zorro Kitsune",
            "Panda Samurái", "Panda de Bambú",
            "Cascada Esmeralda Zen", "Cascada de Jade",
            "Tema Carmesí Bloodmoon", "Tema Luna de Sangre"
    );

    private static final List<String> CATEGORY_ORDER = List.of("frames", "pets", "banners", "themes", "titles");
    private static final List<String> RARITY_ORDER = List.of("COMMON", "RARE", "EPIC", "LEGENDARY");

    @PostConstruct
    public void seedStoreItems() {
        Map<String, StoreItem> existingByName = storeItemRepo.findAll().stream()
                .collect(Collectors.toMap(StoreItem::getName, i -> i, (a, b) -> a));

        RENAMED_ITEMS.forEach((oldName, newName) -> {
            StoreItem legacy = existingByName.remove(oldName);
            if (legacy != null && !existingByName.containsKey(newName)) {
                legacy.setName(newName);
                existingByName.put(newName, legacy);
            }
        });

        List<StoreItem> catalog = List.of(
            // Marcos de avatar
            storeItem("Anillo de Plata Minimal", "Contorno plateado sutil y elegante para empezar tu colección.", "frames", "COMMON", 100, "⚪"),
            storeItem("Aura Dorada Radiante", "Aura resplandeciente en oro de 24k para tu avatar en toda la plataforma.", "frames", "RARE", 150, "✨"),
            storeItem("Fuego Carmesí", "Llamas ardientes de energía para creadores apasionados.", "frames", "EPIC", 300, "🔥"),
            storeItem("Neón Synthwave", "Borde cian y magenta neón futurista con efecto retro.", "frames", "RARE", 220, "⚡"),
            storeItem("Dragón Astral", "Marco legendario forjado con aliento de dragón cósmico.", "frames", "LEGENDARY", 550, "🐲"),
            storeItem("Corona Imperial", "Distintivo de la realeza de Zentry para los creadores más influyentes.", "frames", "EPIC", 450, "👑"),
            storeItem("Halo Angelical Cósmico", "Resplandor divino y etéreo para creadores legendarios.", "frames", "LEGENDARY", 600, "😇"),
            storeItem("Vórtice Galáctico", "Agujero negro y espirales gravitatorias de plasma.", "frames", "EPIC", 380, "🌀"),
            storeItem("Cristal Glacial Diamante", "Facetas geométricas heladas reflectantes de luz prismática.", "frames", "RARE", 260, "💎"),
            storeItem("Circuitos Cuánticos", "Trazas electrónicas que pulsan con energía cibernética.", "frames", "EPIC", 340, "💠"),
            // Mascotas
            storeItem("Colibrí Prisma", "Pequeño colibrí que deja estelas de color al volar.", "pets", "COMMON", 140, "🐦"),
            storeItem("Pixel Cat", "Un gatito pixel art que te acompaña en tus publicaciones y mensajes.", "pets", "COMMON", 120, "🐱"),
            storeItem("Cyber Bot", "Droide de asistencia con IA para inspirar tus mejores proyectos.", "pets", "RARE", 240, "🤖"),
            storeItem("Baby Dragon", "Cría de dragón elemental que escupe chispas doradas.", "pets", "EPIC", 480, "🐉"),
            storeItem("Fénix Astral", "Ave inmortal de luz cósmica. Concede aura de prestigio permanente.", "pets", "LEGENDARY", 600, "🦅"),
            storeItem("Zentry Ghost", "Espíritu travieso de código y diseño.", "pets", "COMMON", 180, "👻"),
            storeItem("Zorro Kitsune", "Zorro espiritual milenario con nueve colas de fuego místico.", "pets", "LEGENDARY", 650, "🦊"),
            storeItem("Panda de Bambú", "Guardián sereno del bosque de bambú que protege tu creatividad.", "pets", "EPIC", 420, "🐼"),
            storeItem("Lobo de Éter", "Lobo alfa fantasmal cazador de inspiración nocturna.", "pets", "RARE", 310, "🐺"),
            storeItem("Búho Arcano", "Búho hechicero con gafas que lee pergaminos de sabiduría.", "pets", "EPIC", 390, "🦉"),
            // Fondos de perfil / banners
            storeItem("Metrópolis Cyberpunk HD", "Ilustración panorámica nocturna de una megaciudad futurista.", "banners", "RARE", 250, "🏙️"),
            storeItem("Nebulosa Cósmica", "Espacio profundo con polvo estelar y auroras boreales violetas.", "banners", "EPIC", 320, "🌌"),
            storeItem("Templo Samurái & Cerezos", "Paz y honor tradicional con caída de pétalos de sakura.", "banners", "RARE", 280, "⛩️"),
            storeItem("Matrix Code Rain", "Lluvia de caracteres binarios verdes para hackers del diseño.", "banners", "COMMON", 200, "💻"),
            storeItem("Atardecer Synthwave", "Sol gigante ocre en el horizonte con rejilla de neón 80s.", "banners", "EPIC", 290, "🌅"),
            storeItem("Castillo Flotante Neocielo", "Fortaleza celestial flotando entre nubes al amanecer.", "banners", "EPIC", 360, "🏰"),
            storeItem("Estación Espacial Orbital", "Puerto espacial con naves hiperlumínicas cruzando la órbita.", "banners", "LEGENDARY", 480, "🛰️"),
            storeItem("Cascada de Jade", "Torrente de agua cristalina entre rocas de jade y musgo sereno.", "banners", "RARE", 270, "🏞️"),
            // Temas de interfaz
            storeItem("Tema Cyberpunk Neón", "Paleta electrizante cian y magenta de alto impacto.", "themes", "RARE", 250, "🕶️"),
            storeItem("Tema Eclipse Solar", "Negro ónix profundo con detalles en oro puro.", "themes", "EPIC", 350, "🌑"),
            storeItem("Tema Bosque Esmeralda", "Verdes profundos y tonos tierra relajantes.", "themes", "COMMON", 200, "🌲"),
            storeItem("Tema Oro Imperial", "Lujo absoluto en dorado champagne y platino.", "themes", "LEGENDARY", 500, "🪙"),
            storeItem("Tema Galaxia Violeta", "Atmósfera mística violeta e índigo con destellos de estrellas.", "themes", "LEGENDARY", 520, "🔮"),
            storeItem("Tema Luna de Sangre", "Contraste dramático de rojo rubí intenso y obsidiana.", "themes", "EPIC", 380, "🩸"),
            storeItem("Tema Aurora Boreal", "Gradientes ondulantes verde esmeralda y turquesa ártico.", "themes", "EPIC", 400, "❄️"),
            storeItem("Tema Blanco Puro Minimal", "Estética nórdica ultralimpia, serena y diáfana.", "themes", "COMMON", 210, "🕊️"),
            // Títulos de reputación
            storeItem("Aprendiz Creativo", "Para quienes dan sus primeros pasos en la comunidad.", "titles", "COMMON", 120, "🌱"),
            storeItem("Explorador de Píxeles", "Curioso incansable del arte digital y los videojuegos.", "titles", "COMMON", 160, "👾"),
            storeItem("Maestro del Código", "Muestra tu maestría en ingeniería y desarrollo.", "titles", "RARE", 300, "⌨️"),
            storeItem("Diseñador Legendario", "Corona tu perfil con el máximo título visual.", "titles", "RARE", 300, "🎨"),
            storeItem("Creador Cósmico", "El título más prestigioso de toda la red Zentry.", "titles", "LEGENDARY", 450, "☄️"),
            storeItem("Alquimista Digital", "Transforma ideas abstractas en oro creativo.", "titles", "EPIC", 350, "🧪"),
            storeItem("Heraldo de la Vanguardia", "Pionero que marca las tendencias visuales y tecnológicas.", "titles", "LEGENDARY", 500, "⚔️"),
            storeItem("Voz de la Comunidad", "Líder carismático respetado por todos los miembros de Zentry.", "titles", "RARE", 280, "📢")
        );

        for (StoreItem item : catalog) {
            StoreItem existing = existingByName.get(item.getName());
            if (existing == null) {
                storeItemRepo.save(item);
            } else {
                existing.setDescription(item.getDescription());
                existing.setType(item.getType());
                existing.setRarity(item.getRarity());
                existing.setPrice(item.getPrice());
                existing.setImageUrl(item.getImageUrl());
                storeItemRepo.save(existing);
            }
        }
    }

    private static StoreItem storeItem(String name, String description, String type, String rarity, int price, String icon) {
        return StoreItem.builder()
                .name(name)
                .description(description)
                .type(type)
                .rarity(rarity)
                .price(price)
                .imageUrl(icon)
                .build();
    }

    public List<StoreItemResponse> getAllItems(Integer userId) {
        Set<Integer> ownedItemIds = storePurchaseRepo.findByUserId(userId).stream()
                .map(StorePurchase::getStoreItemId)
                .collect(Collectors.toSet());

        return storeItemRepo.findAll().stream()
                .sorted(java.util.Comparator
                        .comparingInt((StoreItem i) -> orderIndex(CATEGORY_ORDER, i.getType()))
                        .thenComparingInt(i -> orderIndex(RARITY_ORDER, i.getRarity() != null ? i.getRarity().toUpperCase() : null))
                        .thenComparingInt(i -> i.getPrice() != null ? i.getPrice() : 0)
                        .thenComparing(StoreItem::getName))
                .map(item -> {
                    StoreItemResponse response = mappers.toResponse(item);
                    response.setOwned(ownedItemIds.contains(item.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    private static int orderIndex(List<String> order, String value) {
        int idx = value != null ? order.indexOf(value) : -1;
        return idx >= 0 ? idx : order.size();
    }

    public List<StorePurchaseResponse> getPurchasesForUser(Integer userId) {
        return storePurchaseRepo.findByUserId(userId).stream()
                .map(mappers::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public StorePurchaseResponse buyItem(String username, Integer itemId) {
        User user = userRepo.findByUsername(username)
                .orElseGet(() -> userRepo.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")));

        StoreItem item = storeItemRepo.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artículo de tienda no encontrado"));

        if (storePurchaseRepo.existsByUserIdAndStoreItemId(user.getId(), item.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya posees este artículo");
        }

        Wallet wallet = walletService.getOrCreateWallet(user.getHandle() != null ? user.getHandle() : user.getEmail());

        BigDecimal price = BigDecimal.valueOf(item.getPrice());

        if (wallet.getBalance().compareTo(price) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente en Zentry Coins (" + item.getPrice() + " ZC)");
        }

        // Descontar saldo
        wallet.setBalance(wallet.getBalance().subtract(price));
        walletRepo.save(wallet);

        // Registrar transacción de salida
        txRepo.save(WalletTransaction.builder()
                .username(wallet.getUsername())
                .type(TransactionType.EGRESO)
                .amount(price)
                .description("Compra en tienda: " + item.getName())
                .createdAt(LocalDateTime.now())
                .build());

        // Guardar compra
        StorePurchase purchase = storePurchaseRepo.save(StorePurchase.builder()
                .userId(user.getId())
                .storeItemId(item.getId())
                .purchaseDate(LocalDateTime.now())
                .build());
        gamificationEventService.recordAchievementProgress(user.getId(), "store_purchases", 1);
        return mappers.toResponse(purchase);
    }

    public Map<String, Integer> getEquippedItems(Integer userId) {
        return equippedRepo.findByUserId(userId).stream()
                .collect(Collectors.toMap(UserEquippedItem::getCategory, UserEquippedItem::getStoreItemId));
    }

    @Transactional
    public Map<String, Integer> toggleEquip(Integer userId, Integer itemId) {
        StoreItem item = storeItemRepo.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artículo de tienda no encontrado"));

        if (!storePurchaseRepo.existsByUserIdAndStoreItemId(userId, itemId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes comprar este artículo antes de equiparlo");
        }

        String category = item.getType();
        UserEquippedItem current = equippedRepo.findByUserIdAndCategory(userId, category).orElse(null);

        if (current != null && current.getStoreItemId().equals(itemId)) {
            equippedRepo.delete(current);
        } else if (current != null) {
            current.setStoreItemId(itemId);
            current.setEquippedAt(LocalDateTime.now());
            equippedRepo.save(current);
        } else {
            equippedRepo.save(UserEquippedItem.builder()
                    .userId(userId)
                    .category(category)
                    .storeItemId(itemId)
                    .equippedAt(LocalDateTime.now())
                    .build());
        }

        return getEquippedItems(userId);
    }
}
