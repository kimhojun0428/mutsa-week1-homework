package mutsa.delivery.service;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.User;
import mutsa.delivery.global.oauth2.OAuth2UserInfo;
import mutsa.delivery.repository.UserRepository;
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
        // providerId 를 유일 키로 취급한다. 동일 이메일의 기존 LOCAL 계정과의 병합은
        // 과제 범위상 다루지 않는다(필요 시 후속 확장).
        User user = User.createSocial(
                userInfo.getEmail(),
                userInfo.getNickname(),
                userInfo.getProfileImageUrl(),
                userInfo.getProvider(),
                userInfo.getProviderId()
        );
        return userRepository.save(user);
    }
}
