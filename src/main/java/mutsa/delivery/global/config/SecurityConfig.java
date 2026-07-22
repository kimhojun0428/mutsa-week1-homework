package mutsa.delivery.global.config;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.global.config.security.CustomAccessDeniedHandler;
import mutsa.delivery.global.config.security.CustomAuthenticationEntryPoint;
import mutsa.delivery.global.config.security.JwtAuthenticationFilter;
import mutsa.delivery.global.jwt.JwtProvider;
import mutsa.delivery.global.oauth2.CustomOAuth2UserService;
import mutsa.delivery.global.oauth2.handler.OAuth2FailureHandler;
import mutsa.delivery.global.oauth2.handler.OAuth2SuccessHandler;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            @Value("${app.security.swagger-public:true}") boolean swaggerPublic
    ) throws Exception {

        List<String> publicPaths = new ArrayList<>(List.of(
                "/api/auth/**",
                "/api/health",
                "/oauth2/authorization/**",
                "/login/oauth2/code/**"
        ));
        if (swaggerPublic) {
            publicPaths.add("/swagger-ui/**");
            publicPaths.add("/v3/api-docs/**");
        }

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

                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization")
                        )
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/*")
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // B의 CustomOAuth2UserService 연결
                        )
                        .successHandler(oAuth2SuccessHandler) // A의 SuccessHandler 연결
                        .failureHandler(oAuth2FailureHandler) // A의 FailureHandler 연결
                )

                .authorizeHttpRequests(auth -> auth
                        // 1. 토큰 없이 무조건 허용할 경로들 (로그인, 회원가입, 헬스체크, 스웨거)
                        .requestMatchers(publicPaths.toArray(String[]::new)).permitAll()

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
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") List<String> allowedOrigins
    ) {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> normalizedAllowedOrigins = allowedOrigins.stream()
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();

        configuration.setAllowedOrigins(normalizedAllowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization")); // Authorization 헤더 추출 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
