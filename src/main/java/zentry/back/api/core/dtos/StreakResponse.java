package zentry.back.api.core.dtos;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class StreakResponse {
    private Integer userId;
    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDate lastActivityDate;
    private Boolean activeToday;
    /** active = encendida hoy · at_risk = viene de ayer y hoy aún no se enciende · lost = se perdió · none = nunca */
    private String state;
    /** Horas que quedan del día local para encender la racha (útil para "¡quedan 3 h!") */
    private Long hoursLeftToday;
    /** true solo en la respuesta de recordActivity cuando este evento sumó un día nuevo */
    private Boolean justIncreased;
}
