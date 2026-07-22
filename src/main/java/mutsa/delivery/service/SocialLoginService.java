package mutsa.delivery.service;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.SocialType;
import mutsa.delivery.domain.User;
import mutsa.delivery.global.oauth2.OAuth2UserInfo;
import mutsa.delivery.repository.UserRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 소셜 로그인 회원의 저장·조회를 담당한다. (개발자 B → 개발자 C 진입점)
 *
 * <p>개발자 B의 {@code CustomOAuth2UserService}가 카카오 프로필을 파싱해
 * {@link OAuth2UserInfo}로 넘겨주면, provider + providerId 로 기존 회원을 찾아
 * 있으면 로그인 처리(반환), 없으면 자동 가입시킨다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SocialLoginService {

    private final UserRepository userRepository;

    @Transactional
    public User processSocialLogin(OAuth2UserInfo userInfo) {
        return userRepository
                .findByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId())
                .orElseGet(() -> register(userInfo));
    }

    private User register(OAuth2UserInfo userInfo) {
        // 소셜 회원 매칭은 (provider, providerId)를 유일 키로 한다.
        // 다만 User.email 은 unique 제약이 있으므로, 같은 이메일이 이미 다른 방식(일반 가입 등)으로
        // 등록돼 있으면 그대로 저장 시 DB 제약 위반(500)이 난다. 정책상 자동 계정 연동은 하지 않고,
        // "차단 + 안내"로 처리해 사용자가 기존 로그인 방식을 쓰도록 유도한다.
        userRepository.findByEmail(userInfo.getEmail())
                .ifPresent(existing -> {
                    throw emailAlreadyRegistered(existing);
                });

        User user = User.createSocial(
                userInfo.getEmail(),
                userInfo.getNickname(),
                userInfo.getProfileImageUrl(),
                userInfo.getProvider(),
                userInfo.getProviderId()
        );
        return userRepository.save(user);
    }

    /**
     * OAuth2 인증 흐름에서 발생한 실패는 {@link OAuth2AuthenticationException}(AuthenticationException 계열)로
     * 던져야 OAuth2FailureHandler를 거쳐 프론트 실패 페이지로 리다이렉트된다.
     * 일반 RuntimeException은 이 흐름을 벗어나 500으로 노출된다.
     */
    private static OAuth2AuthenticationException emailAlreadyRegistered(User existing) {
        String description = existing.getProvider() == SocialType.LOCAL
                ? "이미 일반 회원가입에 사용된 이메일입니다. 이메일·비밀번호로 로그인해주세요."
                : "이미 사용 중인 이메일입니다.";
        return new OAuth2AuthenticationException(
                new OAuth2Error("email_already_registered"), description);
    }
}
