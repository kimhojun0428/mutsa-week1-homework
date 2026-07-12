package mutsa.delivery.global.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mutsa.delivery.global.jwt.JwtProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 헤더에서 Authorization 값을 꺼낸다.
        String bearerToken = request.getHeader("Authorization");

        // 2. 토큰이 존재하고 "Bearer "로 시작하는지 확인한다.
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7); // "Bearer " 접두사를 제외한 순수 JWT 문자열만 추출

            // 3. JwtProvider를 사용하여 토큰 내부의 userId를 추출한다.
            // 위조되었거나 만료된 토큰이라면 현준이가 만든 parseClaims() 내부에서 ProjectException을 던진다.
            Long userId = jwtProvider.getUserId(token);

            // 4. 스프링 시큐리티 전용 인증 객체(Authentication)를 생성한다.
            // 현준이의 가이드에 맞춰 principal 자리에 userId를 넣고, 비밀번호(credentials)는 null, 권한은 빈 리스트로 설정한다.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

            // 5. 시큐리티 전용 저장소(SecurityContext)에 인증 객체를 보관한다.
            // 여기에 객체가 존재해야 시큐리티 벽을 통과해 컨트롤러까지 요청이 도달한다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 6. 다음 필터 거름망으로 요청을 넘긴다.
        filterChain.doFilter(request, response);
    }
}