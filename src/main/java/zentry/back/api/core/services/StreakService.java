package zentry.back.api.core.services;

import zentry.back.api.core.util.ZentryClock;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.StreakResponse;
import zentry.back.api.core.models.User;
import zentry.back.api.core.models.UserStreak;
import zentry.back.api.core.repositories.UserRepository;
import zentry.back.api.core.repositories.UserStreakRepository;

import java.time.LocalDate;

@Service
@SuppressWarnings("null")
public class StreakService {

    private final UserStreakRepository streakRepo;
    private final UserRepository userRepo;

    public StreakService(UserStreakRepository streakRepo, UserRepository userRepo) {
        this.streakRepo = streakRepo;
        this.userRepo = userRepo;
    }

    private User resolveUser(String identifier) {
        if (identifier == null || identifier.isBlank() || "anonimo".equalsIgnoreCase(identifier)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
        var byEmail = userRepo.findByEmail(identifier);
        if (byEmail.isPresent()) return byEmail.get();

        var byUsername = userRepo.findByUsername(identifier);
        if (byUsername.isPresent()) return byUsername.get();

        var byBoth = userRepo.findByUsernameOrEmail(identifier, identifier);
        if (byBoth.isPresent()) return byBoth.get();

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
    }

    @Transactional
    public StreakResponse recordActivity(Integer userId) {
        if (userId == null) return null;

        LocalDate today = ZentryClock.today();
        UserStreak streak = streakRepo.findByUserId(userId)
                .orElse(UserStreak.builder()
                        .userId(userId)
                        .currentStreak(0)
                        .longestStreak(0)
                        .build());

        LocalDate last = streak.getLastActivityDate();
        boolean alreadyToday = today.equals(last);
        if (last == null) {
            streak.setCurrentStreak(1);
        } else if (last.equals(today)) {
            // Ya registró actividad hoy, mantener la racha actual
        } else if (last.equals(today.minusDays(1))) {
            // Actividad consecutiva ayer y hoy -> Incrementar racha
            streak.setCurrentStreak(streak.getCurrentStreak() + 1);
        } else {
            // Perdió la racha -> Reiniciar a 1
            streak.setCurrentStreak(1);
        }

        streak.setLastActivityDate(today);
        if (streak.getCurrentStreak() > streak.getLongestStreak()) {
            streak.setLongestStreak(streak.getCurrentStreak());
        }

        UserStreak saved = streakRepo.save(streak);
        StreakResponse response = mapToResponse(saved, true);
        response.setJustIncreased(!alreadyToday);
        return response;
    }

    @Transactional(readOnly = true)
    public StreakResponse getStreak(String identifier) {
        User user = resolveUser(identifier);
        LocalDate today = ZentryClock.today();

        UserStreak streak = streakRepo.findByUserId(user.getId())
                .orElse(UserStreak.builder()
                        .userId(user.getId())
                        .currentStreak(0)
                        .longestStreak(0)
                        .build());

        int effectiveCurrentStreak = streak.getCurrentStreak();
        LocalDate last = streak.getLastActivityDate();
        boolean activeToday = last != null && last.equals(today);

        if (last != null && !last.equals(today) && !last.equals(today.minusDays(1))) {
            // La racha ha caducado
            effectiveCurrentStreak = 0;
        }

        String state;
        if (activeToday) state = "active";
        else if (last != null && last.equals(today.minusDays(1)) && effectiveCurrentStreak > 0) state = "at_risk";
        else if (last != null) state = "lost";
        else state = "none";

        return StreakResponse.builder()
                .userId(user.getId())
                .currentStreak(effectiveCurrentStreak)
                .longestStreak(streak.getLongestStreak())
                .lastActivityDate(last)
                .activeToday(activeToday)
                .state(state)
                .hoursLeftToday(hoursLeftToday())
                .build();
    }

    private long hoursLeftToday() {
        java.time.ZonedDateTime now = java.time.ZonedDateTime.now(ZentryClock.zone());
        java.time.ZonedDateTime midnight = ZentryClock.today().plusDays(1).atStartOfDay(ZentryClock.zone());
        return java.time.Duration.between(now, midnight).toHours();
    }

    private StreakResponse mapToResponse(UserStreak streak, boolean activeToday) {
        return StreakResponse.builder()
                .state(activeToday ? "active" : "none")
                .hoursLeftToday(hoursLeftToday())
                .userId(streak.getUserId())
                .currentStreak(streak.getCurrentStreak())
                .longestStreak(streak.getLongestStreak())
                .lastActivityDate(streak.getLastActivityDate())
                .activeToday(activeToday)
                .build();
    }
}
