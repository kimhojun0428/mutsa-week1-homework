package mutsa.delivery.global.oauth2;

import mutsa.delivery.domain.SocialType;

/**
 * 소셜 공급자(카카오 등)에서 받아온 사용자 정보를 정규화한 추상 타입이다.
 *
 * <p>개발자 B의 {@code CustomOAuth2UserService}가 공급자별 원본 attributes를 파싱해
 * 이 인터페이스의 구현체(예: {@code KakaoUserInfo})로 만들고,
 * 개발자 C의 {@link mutsa.delivery.service.SocialLoginService#processSocialLogin(OAuth2UserInfo)}에
 * 전달한다. 도메인 로직은 공급자 종류에 관계없이 이 인터페이스에만 의존한다.
 */
public interface OAuth2UserInfo {

    /** 가입 경로(예: KAKAO). */
    SocialType getProvider();

    /** 소셜 공급자가 부여한 고유 식별자. 회원 매칭의 유일 키다. */
    String getProviderId();

    /** 이메일. 공급자 동의 항목에 따라 null 일 수 있다. */
    String getEmail();

    /** 닉네임. 신규 소셜 회원의 이름으로 사용한다. */
    String getNickname();

    /** 프로필 이미지 URL. 없으면 null. */
    String getProfileImageUrl();
}
