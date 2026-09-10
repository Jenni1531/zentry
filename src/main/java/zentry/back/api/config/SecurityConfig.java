package zentry.back.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Value("${ALLOWED_ORIGINS:http://localhost:3000,http://localhost:5173}")
    private List<String> allowedOrigins;

    @Bean
    public HttpFirewall allowUrlEncodedPercentHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowUrlEncodedPeriod(true);
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**", "/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/core/users/login", "/api/core/users").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        // El handshake HTTP/SockJS de "/ws" no puede llevar el JWT como header
                        // (el token viaja dentro del frame STOMP CONNECT, ya sobre la conexión
                        // establecida). La autenticación real ocurre en StompAuthChannelInterceptor;
                        // aquí solo se permite abrir la conexión.
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/core/profiles/**", "/api/v1/profiles/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/core/communities", "/api/core/communities/**",
                                "/api/v1/communities", "/api/v1/communities/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/core/explore/**", "/api/v1/explore/**",
                                "/api/core/search", "/api/v1/search")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/core/posts", "/api/core/posts/**", "/api/v1/posts",
                                "/api/v1/posts/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/core/categories", "/api/core/categories/**",
                                "/api/v1/categories", "/api/v1/categories/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/core/tags", "/api/core/tags/**", "/api/v1/tags",
                                "/api/v1/tags/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .httpFirewall(allowUrlEncodedPercentHttpFirewall())
                .ignoring().requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/api-docs/**",
                        "/swagger-resources/**",
                        "/swagger-ui.html",
                        "/webjars/**");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}