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

/**
 * [임시] 인증 인프라 담당자가 교체할 파일입니다.
 *
 * <p>spring-boot-starter-security를 추가하면 기본 설정이 모든 요청을 막기 때문에,
 * 회원가입·로그인 API를 개발·테스트할 수 있도록 최소한의 설정만 넣어 둔 자리표시자다.
 * 현재는 <b>모든 요청을 permitAll</b> 하므로 보호 기능이 없다.
 *
 * <p>인증 인프라 담당자가 아래를 이 파일에 채워 넣으면 된다.
 * <ul>
 *   <li>JwtAuthenticationFilter 등록 (UsernamePasswordAuthenticationFilter 앞)
 *       — {@code JwtProvider.getUserId(token)}으로 사용자 ID를 얻어
 *       SecurityContext에 principal = {@code Long userId}로 저장</li>
 *   <li>인가 규칙: /api/auth/**, /api/health, swagger, GET /api/shops/** 는 permitAll,
 *       그 외(/api/users/**, /api/cart/**, /api/addresses/**, /api/credits/**)는 authenticated</li>
 *   <li>예외 처리: AuthenticationEntryPoint(401), AccessDeniedHandler(403) — GlobalResponse 봉투로 응답</li>
 *   <li>CORS 설정 (프론트 로컬·배포 주소만 허용, Authorization 헤더 허용)</li>
 * </ul>
 */
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
                // TODO(보안 담당): 아래를 실제 인가 규칙으로 교체
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
}
