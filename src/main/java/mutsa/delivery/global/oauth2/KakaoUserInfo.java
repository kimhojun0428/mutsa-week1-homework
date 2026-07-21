package mutsa.delivery.global.oauth2;

import mutsa.delivery.domain.SocialType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 카카오 사용자 정보 응답을 애플리케이션에서 사용하는 형태로 변환한 DTO다.
 */
public record KakaoUserInfo(
        String providerId,
        String email,
        String nickname,
        String profileImageUrl
) implements OAuth2UserInfo {

    public static KakaoUserInfo from(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = nestedMap(attributes, "kakao_account");
        Map<String, Object> profile = nestedMap(kakaoAccount, "profile");
        Map<String, Object> properties = nestedMap(attributes, "properties");
        String providerId = stringValue(attributes.get("id"));
        String email = stringValue(kakaoAccount.get("email"));

        return new KakaoUserInfo(
                providerId,
                firstNonBlank(email, fallbackEmail(providerId)),
                firstNonBlank(
                        stringValue(profile.get("nickname")),
                        stringValue(properties.get("nickname"))
                ),
                firstNonBlank(
                        stringValue(profile.get("profile_image_url")),
                        stringValue(properties.get("profile_image"))
                )
        );
    }

    @Override
    public SocialType getProvider() {
        return SocialType.KAKAO;
    }

    @Override
    public String getProviderId() {
        return providerId;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    private static Map<String, Object> nestedMap(Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (!(value instanceof Map<?, ?> map)) {
            return Map.of();
        }

        Map<String, Object> copied = new LinkedHashMap<>();
        map.forEach((mapKey, mapValue) -> {
            if (mapKey instanceof String stringKey) {
                copied.put(stringKey, mapValue);
            }
        });
        return Collections.unmodifiableMap(copied);
    }

    private static String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static String firstNonBlank(String first, String second) {
        return first != null && !first.isBlank() ? first : second;
    }

    private static String fallbackEmail(String providerId) {
        if (providerId == null || providerId.isBlank()) {
            return null;
        }
        // 카카오는 사용자 동의 상태에 따라 이메일을 주지 않을 수 있다.
        // DB의 필수·고유 이메일 제약을 지키면서 실제 메일로 오인되지 않도록 예약 도메인을 사용한다.
        return "kakao-" + providerId + "@oauth.invalid";
    }
}
