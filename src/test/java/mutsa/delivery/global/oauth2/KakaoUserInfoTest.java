package mutsa.delivery.global.oauth2;

import mutsa.delivery.domain.SocialType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KakaoUserInfoTest {

    @Test
    @DisplayName("카카오의 중첩된 사용자 응답에서 필요한 프로필을 추출한다")
    void parsesNestedKakaoAttributes() {
        Map<String, Object> attributes = Map.of(
                "id", 123456789L,
                "kakao_account", Map.of(
                        "email", "kakao@example.com",
                        "profile", Map.of(
                                "nickname", "카카오유저",
                                "profile_image_url", "https://example.com/profile.png"
                        )
                )
        );

        KakaoUserInfo result = KakaoUserInfo.from(attributes);

        assertThat(result.getProvider()).isEqualTo(SocialType.KAKAO);
        assertThat(result.getProviderId()).isEqualTo("123456789");
        assertThat(result.getEmail()).isEqualTo("kakao@example.com");
        assertThat(result.getNickname()).isEqualTo("카카오유저");
        assertThat(result.getProfileImageUrl()).isEqualTo("https://example.com/profile.png");
    }

    @Test
    @DisplayName("profile 객체가 없으면 properties의 프로필 값을 사용한다")
    void fallsBackToLegacyProperties() {
        Map<String, Object> attributes = Map.of(
                "id", "98765",
                "kakao_account", Map.of("email", "legacy@example.com"),
                "properties", Map.of(
                        "nickname", "레거시유저",
                        "profile_image", "https://example.com/legacy.png"
                )
        );

        KakaoUserInfo result = KakaoUserInfo.from(attributes);

        assertThat(result.getNickname()).isEqualTo("레거시유저");
        assertThat(result.getProfileImageUrl()).isEqualTo("https://example.com/legacy.png");
    }
}
