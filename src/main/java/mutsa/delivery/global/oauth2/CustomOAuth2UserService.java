package mutsa.delivery.global.oauth2;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.User;
import mutsa.delivery.service.SocialLoginService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * 카카오 프로필을 파싱하고 정규화해 회원 저장·조회 로직으로 전달한다.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final SocialLoginService socialLoginService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = loadOAuth2User(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        OAuth2Attributes oAuth2Attributes = OAuth2Attributes.of(
                registrationId,
                nameAttributeKey,
                oAuth2User.getAttributes()
        );
        User user = socialLoginService.processSocialLogin(oAuth2Attributes.userInfo());

        return new CustomOAuth2User(
                oAuth2User.getAuthorities(),
                oAuth2Attributes.attributes(),
                oAuth2Attributes.nameAttributeKey(),
                user
        );
    }

    protected OAuth2User loadOAuth2User(OAuth2UserRequest userRequest) {
        return super.loadUser(userRequest);
    }
}
