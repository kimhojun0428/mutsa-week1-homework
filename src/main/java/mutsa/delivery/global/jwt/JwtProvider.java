package mutsa.delivery.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import mutsa.delivery.global.apiPayload.code.AuthErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Access Token의 발급과 검증을 담당한다.
 *
 * <p>인증 필터(JwtAuthenticationFilter)는 이 클래스의 {@link #getUserId(String)}를 호출해
 * 토큰에서 사용자 식별자를 얻는다. 토큰이 만료됐거나 위조된 경우
 * {@link ProjectException}(EXPIRED_TOKEN / INVALID_TOKEN)이 발생한다.
 */
@Component
public class JwtProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /** 로그인 성공 시 Access Token을 발급한다. subject에 사용자 ID가 담긴다. */
    public String createToken(Long userId, String email) {

        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiredAt)
                .signWith(key)
                .compact();
    }

    /** 토큰의 서명·만료를 검증하고 사용자 ID를 추출한다. */
    public Long getUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new ProjectException(AuthErrorCode.EXPIRED_TOKEN);

        } catch (JwtException | IllegalArgumentException e) {
            // 서명 불일치, 구조 오류, 빈 토큰 등
            throw new ProjectException(AuthErrorCode.INVALID_TOKEN);
        }
    }
}
