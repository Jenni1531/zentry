package zentry.back.api.global;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Global JWT utility service.
 * <p>
 * Single source of truth for all JWT operations across the application:
 * token generation, claim extraction, and validation.
 * The signing key is decoded once at startup and cached for performance.
 * </p>
 */
@Service
public class jwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    /** Signing key cached at startup — avoids repeated Base64 decoding per request. */
    private Key signingKey;

    @PostConstruct
    private void initSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Token generation
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Generates a signed JWT for the given user.
     * Embeds the user's granted roles as a {@code "roles"} claim.
     */
    public String generateToken(UserDetails user) {
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);

        return buildToken(claims, user.getUsername());
    }

    /**
     * Generates a JWT with arbitrary extra claims (e.g., for refresh token flows).
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails user) {
        return buildToken(extraClaims, user.getUsername());
    }

    private String buildToken(Map<String, Object> claims, String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Claim extraction
    // ═══════════════════════════════════════════════════════════════════════

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    /**
     * Parses and verifies the token signature.
     *
     * @throws ResponseStatusException 401 if the token is expired or invalid.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token has expired", ex);
        } catch (JwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token", ex);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Validation
    // ═══════════════════════════════════════════════════════════════════════

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Returns {@code true} only when the token subject matches the given user
     * and the token has not yet expired.
     */
    public boolean validateToken(String token, UserDetails user) {
        return extractUsername(token).equals(user.getUsername()) && !isTokenExpired(token);
    }
}
