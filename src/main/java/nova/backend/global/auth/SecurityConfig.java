package nova.backend.global.auth;

import lombok.RequiredArgsConstructor;
import nova.backend.global.auth.jwt.JwtAuthenticationEntryPoint;
import nova.backend.global.auth.jwt.JwtAuthenticationFilter;
import nova.backend.global.auth.jwt.JwtProvider;
import nova.backend.global.config.CorsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CorsConfig corsConfig;
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService; // ✅ 추가

    private static final String[] whiteList = {
            "/", "/swagger/**", "/swagger-ui/**", "/v3/api-docs/**",
            "/api/auth/**", "/auth/callback/**"
    };

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/swagger/**", "/swagger-ui/**", "/v3/api-docs/**"
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(config -> config.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whiteList).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilter(corsConfig.corsFilter())
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider, customUserDetailsService), // ✅ 수정된 부분
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(new ExceptionHandlerFilter(), JwtAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
