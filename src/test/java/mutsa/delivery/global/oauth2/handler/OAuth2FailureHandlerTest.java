package mutsa.delivery.global.oauth2.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2FailureHandlerTest {

    @Test
    @DisplayName("한글 오류 메시지를 URL 인코딩해 프론트엔드로 리다이렉트한다")
    void encodesKoreanErrorMessage() throws Exception {
        String message = "카카오 계정 이메일 제공 동의가 필요합니다.";
        OAuth2AuthenticationException exception = new OAuth2AuthenticationException(
                new OAuth2Error("email_not_provided"), message);
        MockHttpServletResponse response = new MockHttpServletResponse();

        new OAuth2FailureHandler("http://localhost:5173").onAuthenticationFailure(
                new MockHttpServletRequest(), response, exception);

        String redirectedUrl = response.getRedirectedUrl();
        assertThat(redirectedUrl).startsWith("http://localhost:5173/oauth/failure?error=");
        assertThat(URI.create(redirectedUrl).getRawQuery()).contains("%EC");

        String encodedMessage = URI.create(redirectedUrl).getRawQuery().substring("error=".length());
        assertThat(URLDecoder.decode(encodedMessage, StandardCharsets.UTF_8)).isEqualTo(message);
    }
}
