package mutsa.delivery.global.config;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.global.config.security.CustomAccessDeniedHandler;
import mutsa.delivery.global.config.security.CustomAuthenticationEntryPoint;
import mutsa.delivery.global.config.security.JwtAuthenticationFilter;
import mutsa.delivery.global.jwt.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeHttpRequests(auth -> auth
                        // 1. 토큰 없이 무조건 허용할 경로들 (로그인, 회원가입, 헬스체크, 스웨거)
                        .requestMatchers("/api/auth/**", "/api/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 2. 상점 조회는 GET 요청만 토큰 없이 허용 (POST, PATCH 등 상점 수정/생성은 차단)
                        .requestMatchers(HttpMethod.GET, "/api/shops/**").permitAll()

                        // 3. 유저 정보, 장바구니, 주소, 크레딧 관련 API는 반드시 인증(토큰) 필요
                        .requestMatchers("/api/users/**", "/api/cart/**", "/api/addresses/**", "/api/credits/**").authenticated()

                        // 4. 나머지 정의되지 않은 모든 요청도 안전하게 인증된 사용자만 허용
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization")); // Authorization 헤더 추출 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
