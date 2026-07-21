package mutsa.delivery.global.oauth2;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 공급자별 원본 attributes와 정규화된 사용자 정보를 함께 보관한다.
 */
public record OAuth2Attributes(
        Map<String, Object> attributes,
        String nameAttributeKey,
        OAuth2UserInfo userInfo
) {

    public static OAuth2Attributes of(
            String registrationId,
            String nameAttributeKey,
            Map<String, Object> attributes
    ) {
        if (!"kakao".equalsIgnoreCase(registrationId)) {
            throw oauth2Error("unsupported_provider", "지원하지 않는 소셜 로그인 제공자입니다: " + registrationId);
        }

        KakaoUserInfo userInfo = KakaoUserInfo.from(attributes);
        validate(userInfo);
        Map<String, Object> copiedAttributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
        return new OAuth2Attributes(copiedAttributes, nameAttributeKey, userInfo);
    }

    private static void validate(KakaoUserInfo userInfo) {
        if (isBlank(userInfo.providerId())) {
            throw oauth2Error("invalid_user_info", "카카오 사용자 식별자를 확인할 수 없습니다.");
        }
        if (isBlank(userInfo.email())) {
            throw oauth2Error("email_not_provided", "카카오 계정 이메일 제공 동의가 필요합니다.");
        }
        if (isBlank(userInfo.nickname())) {
            throw oauth2Error("nickname_not_provided", "카카오 프로필 닉네임 제공 동의가 필요합니다.");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static OAuth2AuthenticationException oauth2Error(String code, String description) {
        return new OAuth2AuthenticationException(new OAuth2Error(code), description);
    }
}
