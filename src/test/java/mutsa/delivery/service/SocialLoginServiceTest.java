package mutsa.delivery.service;

import mutsa.delivery.domain.SocialType;
import mutsa.delivery.domain.User;
import mutsa.delivery.global.oauth2.OAuth2UserInfo;
import mutsa.delivery.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 소셜 로그인 진입점(processSocialLogin)의 "없으면 가입, 있으면 조회" 동작과
 * 이메일 충돌 시 "차단 + 안내" 정책(#34/#35 후속)을 고정한다.
 */
class SocialLoginServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final SocialLoginService socialLoginService = new SocialLoginService(userRepository);

    @Test
    @DisplayName("기존 소셜 회원이면 저장 없이 그대로 반환한다")
    void existingUserReturnedWithoutSave() {
        OAuth2UserInfo userInfo = new KakaoUserInfoStub(
                "12345", "kakao@example.com", "카카오유저", "http://img/profile.png");
        User existing = User.createSocial(
                "kakao@example.com", "카카오유저", "http://img/profile.png", SocialType.KAKAO, "12345");
        when(userRepository.findByProviderAndProviderId(SocialType.KAKAO, "12345"))
                .thenReturn(Optional.of(existing));

        User result = socialLoginService.processSocialLogin(userInfo);

        assertThat(result).isSameAs(existing);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("신규 소셜 회원이고 이메일 충돌이 없으면 자동 가입시켜 저장 후 반환한다")
    void newUserRegistered() {
        OAuth2UserInfo userInfo = new KakaoUserInfoStub(
                "99999", "new@example.com", "새유저", "http://img/new.png");
        when(userRepository.findByProviderAndProviderId(SocialType.KAKAO, "99999"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = socialLoginService.processSocialLogin(userInfo);

        assertThat(result.getProvider()).isEqualTo(SocialType.KAKAO);
        assertThat(result.getProviderId()).isEqualTo("99999");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getName()).isEqualTo("새유저");
        assertThat(result.getPassword()).isNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("같은 이메일의 일반(LOCAL) 계정이 있으면 가입을 차단하고 OAuth2 예외를 던진다")
    void blocksWhenEmailAlreadyUsedByLocalAccount() {
        OAuth2UserInfo userInfo = new KakaoUserInfoStub(
                "77777", "dup@example.com", "카카오유저", "http://img/x.png");
        User localUser = User.create("dup@example.com", "encodedPw", "일반유저");
        when(userRepository.findByProviderAndProviderId(SocialType.KAKAO, "77777"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("dup@example.com")).thenReturn(Optional.of(localUser));

        assertThatThrownBy(() -> socialLoginService.processSocialLogin(userInfo))
                .isInstanceOf(OAuth2AuthenticationException.class)
                .hasMessageContaining("일반 회원가입에 사용된 이메일");

        verify(userRepository, never()).save(any());
    }

    private record KakaoUserInfoStub(
            String providerId, String email, String nickname, String profileImageUrl
    ) implements OAuth2UserInfo {
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
    }
}
