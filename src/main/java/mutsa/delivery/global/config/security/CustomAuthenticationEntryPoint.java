package mutsa.delivery.global.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // 브라우저에 JSON 형태로 응답을 보내기 위한 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태코드 설정

        // 💡 우리 프로젝트의 GlobalResponse 포맷에 맞게 맵이나 DTO를 구성한다.
        Map<String, Object> body = new HashMap<>();
        body.put("isSuccess", false);
        body.put("code", "AUTH401");
        body.put("message", "인증이 필요하거나 유효하지 않은 토큰입니다.");

        // JSON 문자열로 변환하여 리스폰스 바디에 직접 밀어넣는다.
        objectMapper.writeValue(response.getWriter(), body);
    }
}
