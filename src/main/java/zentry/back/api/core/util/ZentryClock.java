package zentry.back.api.core.util;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * "Día" de la plataforma para rachas y misiones diarias. Usa la zona horaria de la comunidad
 * (APP_TIMEZONE, por defecto America/Mexico_City) para que el día cambie a medianoche local
 * —como TikTok— y no a medianoche UTC (las 6 p. m. en México).
 */
public final class ZentryClock {

    private static final ZoneId ZONE = resolveZone();

    private ZentryClock() {}

    private static ZoneId resolveZone() {
        String configured = System.getenv("APP_TIMEZONE");
        try {
            return ZoneId.of(configured != null && !configured.isBlank() ? configured : "America/Mexico_City");
        } catch (Exception e) {
            return ZoneId.of("America/Mexico_City");
        }
    }

    public static ZoneId zone() {
        return ZONE;
    }

    public static LocalDate today() {
        return LocalDate.now(ZONE);
    }
}
