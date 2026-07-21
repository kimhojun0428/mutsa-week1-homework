package mutsa.delivery.global.jwt;

import mutsa.delivery.global.apiPayload.code.AuthErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JwtProvider는 인증 필터(보안 담당)가 사용하는 계약이므로,
 * 발급·검증·만료·위조 동작을 고정해 둔다.
 */
class JwtProviderTest {

    private static final String SECRET = "test-only-secret-key-do-not-use-in-production-1234567890";
    private static final long ONE_HOUR = 3_600_000L;

    @Test
    @DisplayName("발급한 토큰에서 사용자 ID를 다시 추출할 수 있다")
    void createToken_thenGetUserId() {
        JwtProvider jwtProvider = new JwtProvider(SECRET, ONE_HOUR);

        String token = jwtProvider.createToken(1L, "test@example.com");

        assertThat(jwtProvider.getUserId(token)).isEqualTo(1L);
    }

    @Test
    @DisplayName("userId만으로 발급한 Access Token에서도 사용자 ID를 추출할 수 있다")
    void createAccessToken_thenGetUserId() {
        JwtProvider jwtProvider = new JwtProvider(SECRET, ONE_HOUR);

        String token = jwtProvider.createAccessToken(42L);

        assertThat(jwtProvider.getUserId(token)).isEqualTo(42L);
    }

    @Test
    @DisplayName("만료된 토큰은 EXPIRED_TOKEN 예외를 던진다")
    void expiredToken() {
        JwtProvider expiredProvider = new JwtProvider(SECRET, -1000L); // 이미 만료된 토큰 발급
        String token = expiredProvider.createToken(1L, "test@example.com");

        assertThatThrownBy(() -> expiredProvider.getUserId(token))
                .isInstanceOf(ProjectException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.EXPIRED_TOKEN);
    }

    @Test
    @DisplayName("다른 키로 서명된 토큰은 INVALID_TOKEN 예외를 던진다")
    void invalidSignature() {
        JwtProvider issuer = new JwtProvider(SECRET, ONE_HOUR);
        JwtProvider verifier = new JwtProvider("another-completely-different-secret-key-0987654321", ONE_HOUR);

        String token = issuer.createToken(1L, "test@example.com");

        assertThatThrownBy(() -> verifier.getUserId(token))
                .isInstanceOf(ProjectException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("형식이 깨진 토큰은 INVALID_TOKEN 예외를 던진다")
    void malformedToken() {
        JwtProvider jwtProvider = new JwtProvider(SECRET, ONE_HOUR);

        assertThatThrownBy(() -> jwtProvider.getUserId("this-is-not-a-jwt"))
                .isInstanceOf(ProjectException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_TOKEN);
    }
}
