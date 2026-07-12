package mutsa.delivery.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

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
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // TODO(보안 담당): 아래를 실제 인가 규칙으로 교체
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
