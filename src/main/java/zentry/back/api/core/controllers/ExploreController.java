package zentry.back.api.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import zentry.back.api.core.dtos.PostResponse;
import zentry.back.api.core.dtos.ProfileResponse;
import zentry.back.api.core.dtos.TrendingTopicResponse;
import zentry.back.api.core.dtos.UnifiedSearchResponse;
import zentry.back.api.core.services.ExploreService;

import java.util.List;

@RestController
@RequestMapping({"/api/core", "/api/v1"})
@Tag(name = "Explorar y Búsqueda", description = "Endpoints de tendencias estilo X y búsqueda unificada multimodal")
public class ExploreController {

    private final ExploreService exploreService;

    public ExploreController(ExploreService exploreService) {
        this.exploreService = exploreService;
    }

    // ─── GET /api/core/explore/trending ───────────────────────────────────────
    @Operation(summary = "Obtener tendencias actuales estilo X",
               description = "Devuelve una lista de hashtags y categorías en tendencia ordenados por cantidad de publicaciones.")
    @GetMapping("/explore/trending")
    public ResponseEntity<List<TrendingTopicResponse>> getTrending() {
        return ResponseEntity.ok(exploreService.getTrendingTopics());
    }

    // ─── GET /api/core/explore/history ────────────────────────────────────────
    @Operation(summary = "Obtener archivo histórico de tendencias",
               description = "Devuelve el resumen de las mejores tendencias ordenadas por año.")
    @GetMapping("/explore/history")
    public ResponseEntity<List<TrendingTopicResponse>> getHistory(
            @Parameter(description = "Año a consultar (ej: 2026, 2025)") @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(exploreService.getTrendingHistory(year));
    }

    // ─── GET /api/core/explore/popular ────────────────────────────────────────
    @Operation(summary = "Obras populares", description = "Las publicaciones con más reacciones de los últimos 30 días.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Obras obtenidas") })
    @GetMapping("/explore/popular")
    public ResponseEntity<List<PostResponse>> getPopular(
            @RequestParam(defaultValue = "0") int page, java.security.Principal principal) {
        return ResponseEntity.ok(exploreService.getPopularPosts(page, principal != null ? principal.getName() : null));
    }

    // ─── GET /api/core/explore/creators ───────────────────────────────────────
    @Operation(summary = "Creadores sugeridos", description = "Los creadores más seguidos que el usuario aún no sigue.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Creadores obtenidos") })
    @GetMapping("/explore/creators")
    public ResponseEntity<List<ProfileResponse>> getCreators(java.security.Principal principal) {
        return ResponseEntity.ok(exploreService.getSuggestedCreators(principal != null ? principal.getName() : null));
    }

    // ─── GET /api/core/search ──────────────────────────────────────────────────
    @Operation(summary = "Búsqueda unificada",
               description = "Creadores (por @usuario o nombre), obras, proyectos públicos, comunidades y tendencias.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Resultados") })
    @GetMapping("/search")
    public ResponseEntity<UnifiedSearchResponse> search(
            @Parameter(description = "Término o palabra clave de búsqueda") @RequestParam(required = false, defaultValue = "") String query,
            java.security.Principal principal) {
        return ResponseEntity.ok(exploreService.unifiedSearch(query, principal != null ? principal.getName() : null));
    }
}
