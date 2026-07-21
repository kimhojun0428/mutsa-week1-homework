package mutsa.delivery.global.oauth2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OAuth2AttributesTest {

    @Test
    @DisplayName("카카오 attributes를 정규화된 사용자 정보로 만든다")
    void normalizesKakaoAttributes() {
        Map<String, Object> attributes = kakaoAttributes("user@example.com", "사용자");

        OAuth2Attributes result = OAuth2Attributes.of("kakao", "id", attributes);

        assertThat(result.nameAttributeKey()).isEqualTo("id");
        assertThat(result.userInfo()).isInstanceOf(KakaoUserInfo.class);
        assertThat(result.userInfo().getProviderId()).isEqualTo("1234");
    }

    @Test
    @DisplayName("이메일 제공 동의가 없으면 카카오 ID 기반 대체 이메일을 사용한다")
    void usesFallbackEmailWhenEmailIsMissing() {
        Map<String, Object> attributes = Map.of(
                "id", 1234L,
                "kakao_account", Map.of("profile", Map.of("nickname", "사용자"))
        );

        OAuth2Attributes result = OAuth2Attributes.of("kakao", "id", attributes);

        assertThat(result.userInfo().getEmail()).isEqualTo("kakao-1234@oauth.invalid");
    }

    @Test
    @DisplayName("지원하지 않는 공급자는 명확하게 거절한다")
    void rejectsUnsupportedProvider() {
        assertThatThrownBy(() -> OAuth2Attributes.of(
                "google", "sub", kakaoAttributes("user@example.com", "사용자")))
                .isInstanceOf(OAuth2AuthenticationException.class)
                .hasMessageContaining("지원하지 않는 소셜 로그인 제공자");
    }

    private Map<String, Object> kakaoAttributes(String email, String nickname) {
        return Map.of(
                "id", 1234L,
                "kakao_account", Map.of(
                        "email", email,
                        "profile", Map.of("nickname", nickname)
                )
        );
    }
}
