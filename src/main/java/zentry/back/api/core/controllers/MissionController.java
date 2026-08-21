package zentry.back.api.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zentry.back.api.core.dtos.WalletResponse;
import zentry.back.api.core.services.WalletService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping({"/api/core/missions", "/api/v1/missions"})
@Tag(name = "Misiones y Recompensas", description = "Endpoints para reclamar recompensas de misiones diarias y logros")
public class MissionController {

    private final WalletService walletService;

    public MissionController(WalletService walletService) {
        this.walletService = walletService;
    }

    @Operation(summary = "Reclamar recompensa de misión diaria",
               description = "Incrementa el saldo de Zentry Coins del usuario al completar una misión diaria.")
    @PostMapping("/{id}/claim")
    public ResponseEntity<Map<String, Object>> claimMission(
            @Parameter(description = "ID de la misión") @PathVariable String id,
            Principal principal) {
        
        String username = principal != null ? principal.getName() : "admin@zentry.com";
        // Por defecto recompensamos con 15 ZC por misión diaria
        WalletResponse wallet = walletService.topup(username, 
                zentry.back.api.core.dtos.TopupRequest.builder().amount(java.math.BigDecimal.valueOf(15.0)).build());

        return ResponseEntity.ok(Map.of(
            "success", true,
            "missionId", id,
            "rewardCoins", 15,
            "newBalance", wallet.getBalance()
        ));
    }
}
