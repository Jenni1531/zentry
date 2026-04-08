package zentry.back.api.global;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;



import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("jwtService")
class jwtServiceTest {

    private jwtService service;
    private UserDetails userDetails;
    private String base64Secret;

    @BeforeEach
    void setUp() throws Exception {
        service = new jwtService();

        // Generate a fresh HS256-safe key and encode it as Base64
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        base64Secret = Encoders.BASE64.encode(key.getEncoded());

        // Inject values normally set by @Value
        ReflectionTestUtils.setField(service, "secretKey", base64Secret);
        ReflectionTestUtils.setField(service, "expiration", 3_600_000L); // 1 hour

        // Trigger @PostConstruct manually
        ReflectionTestUtils.invokeMethod(service, "initSigningKey");

        userDetails = User.withUsername("test@zentry.com")
                .password("irrelevant")
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // generateToken
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("generateToken()")
    class GenerateTokenTests {

        @Test
        @DisplayName("returns a non-blank JWT string")
        void returnsNonBlankToken() {
            String token = service.generateToken(userDetails);
            assertThat(token).isNotBlank();
        }

        @Test
        @DisplayName("token subject equals the user's username")
        void subjectMatchesUsername() {
            String token = service.generateToken(userDetails);
            assertThat(service.extractUsername(token)).isEqualTo("test@zentry.com");
        }

        @Test
        @DisplayName("token roles claim contains user authorities")
        void rolesClaimContainsAuthorities() {
            String token = service.generateToken(userDetails);
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) service.extractClaim(token, claims -> claims.get("roles", List.class));
            assertThat(roles).contains("ROLE_USER");
        }

        @Test
        @DisplayName("overload with extra claims embeds those claims in the token")
        void extraClaimsAreEmbedded() {
            Map<String, Object> extra = Map.of("customKey", "customValue");
            String token = service.generateToken(extra, userDetails);
            String value = service.extractClaim(token, claims -> claims.get("customKey", String.class));
            assertThat(value).isEqualTo("customValue");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // validateToken
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("validateToken()")
    class ValidateTokenTests {

        @Test
        @DisplayName("returns true for a valid, non-expired token")
        void returnsTrueForValidToken() {
            String token = service.generateToken(userDetails);
            assertThat(service.validateToken(token, userDetails)).isTrue();
        }

        @Test
        @DisplayName("returns false when token subject does not match the user")
        void returnsFalseForWrongUser() {
            String token = service.generateToken(userDetails);
            UserDetails otherUser = User.withUsername("other@zentry.com")
                    .password("irrelevant")
                    .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                    .build();
            assertThat(service.validateToken(token, otherUser)).isFalse();
        }

        @Test
        @DisplayName("throws 401 for an expired token")
        void throws401ForExpiredToken() {
            // Build a token that expired 1 ms ago
            Key key = Keys.hmacShaKeyFor(
                    io.jsonwebtoken.io.Decoders.BASE64.decode(base64Secret));
            String expiredToken = Jwts.builder()
                    .setSubject("test@zentry.com")
                    .setIssuedAt(new Date(System.currentTimeMillis() - 5000))
                    .setExpiration(new Date(System.currentTimeMillis() - 1))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            assertThatThrownBy(() -> service.validateToken(expiredToken, userDetails))
                    .isInstanceOf(ResponseStatusException.class)
                    .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.UNAUTHORIZED));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // extractUsername / extractExpiration
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("claim extraction")
    class ExtractionTests {

        @Test
        @DisplayName("extractUsername returns correct subject")
        void extractsUsername() {
            String token = service.generateToken(userDetails);
            assertThat(service.extractUsername(token)).isEqualTo("test@zentry.com");
        }

        @Test
        @DisplayName("extractExpiration returns a future date")
        void extractsFutureExpiration() {
            String token = service.generateToken(userDetails);
            assertThat(service.extractExpiration(token)).isAfter(new Date());
        }

        @Test
        @DisplayName("throws 401 for a tampered / invalid token")
        void throws401ForInvalidToken() {
            assertThatThrownBy(() -> service.extractUsername("this.is.not.a.jwt"))
                    .isInstanceOf(ResponseStatusException.class)
                    .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.UNAUTHORIZED));
        }
    }
}
