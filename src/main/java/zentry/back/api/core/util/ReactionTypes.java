package zentry.back.api.core.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Catálogo único de reacciones para obras e historias.
 * "like" es el valor por defecto y equivale al me gusta clásico (❤️).
 */
public final class ReactionTypes {

    public static final String DEFAULT = "like";

    private static final Map<String, String> EMOJIS = new LinkedHashMap<>();
    static {
        EMOJIS.put("like", "❤️");
        EMOJIS.put("fire", "🔥");
        EMOJIS.put("clap", "👏");
        EMOJIS.put("wow", "😮");
        EMOJIS.put("laugh", "😂");
        EMOJIS.put("idea", "💡");
    }

    private ReactionTypes() {}

    public static String normalize(String type) {
        if (type == null || type.isBlank()) return DEFAULT;
        String key = type.trim().toLowerCase();
        if (!EMOJIS.containsKey(key)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reacción no válida: " + type);
        }
        return key;
    }

    /** Filas antiguas (antes de existir reaction_type) cuentan como "like". */
    public static String orDefault(String type) {
        return type == null || type.isBlank() ? DEFAULT : type;
    }

    public static String emoji(String type) {
        return EMOJIS.getOrDefault(orDefault(type), "❤️");
    }
}
