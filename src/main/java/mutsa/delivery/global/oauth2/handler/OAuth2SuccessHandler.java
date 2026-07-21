package mutsa.delivery.global.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.User;
import mutsa.delivery.global.jwt.JwtProvider;
import mutsa.delivery.global.oauth2.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider; // C가 제공하는 JwtProvider

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. B가 CustomOAuth2UserService에서 반환한 CustomOAuth2User 객체 획득
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        // 2. C가 작성한 JwtProvider의 createAccessToken(Long userId) 호출
        String accessToken = jwtProvider.createAccessToken(user.getId());

        // 3. 프론트엔드로 토큰을 쿼리 스트링에 실어 리다이렉트
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/oauth/success")
                .queryParam("accessToken", accessToken)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
