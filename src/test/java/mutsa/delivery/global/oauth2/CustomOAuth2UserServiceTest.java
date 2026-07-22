package mutsa.delivery.global.oauth2;

import mutsa.delivery.domain.SocialType;
import mutsa.delivery.domain.User;
import mutsa.delivery.service.SocialLoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomOAuth2UserServiceTest {

    @Test
    @DisplayName("카카오 프로필을 정규화해 회원 서비스에 넘기고 CustomOAuth2User를 반환한다")
    void delegatesNormalizedProfileToSocialLoginService() {
        SocialLoginService socialLoginService = mock(SocialLoginService.class);
        Map<String, Object> attributes = Map.of(
                "id", 777L,
                "kakao_account", Map.of(
                        "email", "oauth@example.com",
                        "profile", Map.of(
                                "nickname", "OAuth유저",
                                "profile_image_url", "https://example.com/oauth.png"
                        )
                )
        );
        OAuth2User rawOAuth2User = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")), attributes, "id");
        User savedUser = User.createSocial(
                "oauth@example.com", "OAuth유저", "https://example.com/oauth.png",
                SocialType.KAKAO, "777");
        when(socialLoginService.processSocialLogin(org.mockito.ArgumentMatchers.any()))
                .thenReturn(savedUser);

        CustomOAuth2UserService service = new CustomOAuth2UserService(socialLoginService) {
            @Override
            protected OAuth2User loadOAuth2User(OAuth2UserRequest userRequest) {
                return rawOAuth2User;
            }
        };

        OAuth2User result = service.loadUser(kakaoUserRequest());

        ArgumentCaptor<OAuth2UserInfo> captor = ArgumentCaptor.forClass(OAuth2UserInfo.class);
        verify(socialLoginService).processSocialLogin(captor.capture());
        assertThat(captor.getValue().getProvider()).isEqualTo(SocialType.KAKAO);
        assertThat(captor.getValue().getProviderId()).isEqualTo("777");
        assertThat(captor.getValue().getEmail()).isEqualTo("oauth@example.com");
        assertThat(result).isInstanceOf(CustomOAuth2User.class);
        assertThat(((CustomOAuth2User) result).getUser()).isSameAs(savedUser);
        assertThat(result.getAttributes()).isEqualTo(attributes);
    }

    private OAuth2UserRequest kakaoUserRequest() {
        ClientRegistration registration = ClientRegistration.withRegistrationId("kakao")
                .clientId("client-id")
                .clientSecret("client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("id")
                .clientName("Kakao")
                .build();
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "access-token",
                Instant.now(),
                Instant.now().plusSeconds(300)
        );
        return new OAuth2UserRequest(registration, accessToken);
    }
}
