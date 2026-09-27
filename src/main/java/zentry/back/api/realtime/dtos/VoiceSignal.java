package zentry.back.api.realtime.dtos;

import lombok.*;

/**
 * Mensaje del canal de voz de un proyecto.
 * type: join | leave | mute | offer | answer | ice
 * to/from: userId de los participantes (nunca se exponen emails)
 */
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class VoiceSignal {
    private String type;
    private Integer to;
    private Integer from;
    private Boolean muted;
    /** SDP u ICE candidate (se reenvía tal cual) */
    private Object data;
}
