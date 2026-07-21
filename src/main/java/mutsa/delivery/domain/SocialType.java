package mutsa.delivery.domain;

/**
 * 회원의 로그인 방식(가입 경로)을 구분한다.
 *
 * <p>{@link #LOCAL}은 이메일·비밀번호로 직접 가입한 회원,
 * {@link #KAKAO}는 카카오 OAuth2로 가입한 소셜 회원이다.
 */
public enum SocialType {
    LOCAL,
    KAKAO
}
