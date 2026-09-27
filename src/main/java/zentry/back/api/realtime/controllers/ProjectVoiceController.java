package zentry.back.api.realtime.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import zentry.back.api.core.models.Profile;
import zentry.back.api.core.models.User;
import zentry.back.api.core.repositories.ProfileRepository;
import zentry.back.api.core.repositories.UserRepository;
import zentry.back.api.core.services.ProjectService;
import zentry.back.api.realtime.dtos.VoiceParticipant;
import zentry.back.api.realtime.dtos.VoiceSignal;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Canal de voz de cada proyecto (tipo Discord). El audio viaja directo entre navegadores (WebRTC);
 * el servidor solo coordina: valida que el usuario sea miembro, mantiene quién está en la sala y
 * reenvía las señales (offer/answer/ice) únicamente al participante destinatario.
 *
 * Suscripciones del cliente:
 *  - /topic/projects/{id}/voice  -> lista de participantes
 *  - /user/queue/voice           -> señales WebRTC dirigidas a mí
 * Envío: /app/projects/{id}/voice
 */
@Controller
@Tag(name = "Project Voice", description = "Canal de voz de proyectos (señalización WebRTC)")
public class ProjectVoiceController {

    private final ProjectService projectService;
    private final SimpMessagingTemplate messaging;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final zentry.back.api.core.services.GamificationEventService gamificationEventService;

    /** projectId -> (userId -> participante) */
    private final Map<Long, Map<Integer, VoiceParticipant>> rooms = new ConcurrentHashMap<>();

    public ProjectVoiceController(ProjectService projectService, SimpMessagingTemplate messaging,
                                  UserRepository userRepository, ProfileRepository profileRepository,
                                  zentry.back.api.core.services.GamificationEventService gamificationEventService) {
        this.gamificationEventService = gamificationEventService;
        this.projectService = projectService;
        this.messaging = messaging;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @MessageMapping("/projects/{projectId}/voice")
    public void handle(@DestinationVariable Long projectId, @Payload VoiceSignal signal,
                       Principal principal, SimpMessageHeaderAccessor headers) {
        if (principal == null || signal == null || signal.getType() == null) return;
        if (!projectService.isMember(projectId, principal.getName())) return;

        User me = userRepository.findByEmail(principal.getName()).orElse(null);
        if (me == null) return;
        Map<Integer, VoiceParticipant> room = rooms.computeIfAbsent(projectId, k -> new ConcurrentHashMap<>());

        switch (signal.getType()) {
            case "join" -> {
                room.put(me.getId(), VoiceParticipant.builder()
                        .userId(me.getId())
                        .username(me.getHandle() != null ? me.getHandle() : me.getEmail().split("@")[0])
                        .avatarUrl(profileRepository.findByUserId(me.getId()).map(Profile::getAvatarUrl).orElse(null))
                        .muted(Boolean.TRUE.equals(signal.getMuted()))
                        .joinedAt(System.currentTimeMillis())
                        .principalName(principal.getName())
                        .sessionId(headers.getSessionId())
                        .build());
                broadcast(projectId);
                gamificationEventService.recordAchievementProgress(me.getId(), "join_voice", 1);
            }
            case "leave" -> {
                room.remove(me.getId());
                broadcast(projectId);
            }
            case "mute" -> {
                VoiceParticipant p = room.get(me.getId());
                if (p != null) {
                    p.setMuted(Boolean.TRUE.equals(signal.getMuted()));
                    broadcast(projectId);
                }
            }
            case "offer", "answer", "ice" -> {
                VoiceParticipant target = signal.getTo() != null ? room.get(signal.getTo()) : null;
                if (target == null || !room.containsKey(me.getId())) return;
                signal.setFrom(me.getId());
                messaging.convertAndSendToUser(target.getPrincipalName(), "/queue/voice", signal);
            }
            default -> { /* tipo desconocido: se ignora */ }
        }
    }

    @GetMapping("/api/core/projects/{projectId}/voice")
    @ResponseBody
    @Operation(summary = "Participantes conectados al canal de voz del proyecto (solo miembros)")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Participantes"),
                    @ApiResponse(responseCode = "403", description = "No es miembro", content = @Content) })
    public ResponseEntity<List<VoiceParticipant>> participants(@PathVariable Long projectId, Principal principal) {
        if (principal == null || !projectService.isMember(projectId, principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo los miembros del proyecto pueden ver el canal de voz");
        }
        return ResponseEntity.ok(sorted(projectId));
    }

    /** Si alguien cierra la pestaña o pierde la conexión, sale de todas las salas */
    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        rooms.forEach((projectId, room) -> {
            boolean removed = room.values().removeIf(p -> sessionId.equals(p.getSessionId()));
            if (removed) broadcast(projectId);
        });
    }

    private void broadcast(Long projectId) {
        messaging.convertAndSend("/topic/projects/" + projectId + "/voice", sorted(projectId));
    }

    private List<VoiceParticipant> sorted(Long projectId) {
        List<VoiceParticipant> list = new ArrayList<>(rooms.getOrDefault(projectId, Map.of()).values());
        list.sort(Comparator.comparingLong(VoiceParticipant::getJoinedAt));
        return list;
    }
}
