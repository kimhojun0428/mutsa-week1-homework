package mutsa.delivery.global.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import mutsa.delivery.global.jwt.JwtProvider;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson 2 고정

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);

            try {
                Long userId = jwtProvider.getUserId(token);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ProjectException e) {
                // 필터 내부에서 예외를 처리하여 EXPIRED_TOKEN / INVALID_TOKEN 유지 및 반환
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                Map<String, Object> body = new HashMap<>();
                body.put("success", false);
                body.put("code", e.getErrorCode().getCode());
                body.put("message", e.getErrorCode().getMessage());
                body.put("data", null);
                body.put("error", null);

                objectMapper.writeValue(response.getWriter(), body);
                return; // 에러 발생 시 필터 체인 통과를 중단하고 즉시 리턴
            }
        }

        filterChain.doFilter(request, response);
    }
}