package mutsa.delivery.global.oauth2;

import mutsa.delivery.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.io.Serial;
import java.util.Collection;
import java.util.Map;

/**
 * 카카오 원본 attributes와 DB 회원을 함께 담아 성공 핸들러로 전달하는 principal이다.
 */
public class CustomOAuth2User extends DefaultOAuth2User {

    @Serial
    private static final long serialVersionUID = 1L;

    private final User user;

    public CustomOAuth2User(
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes,
            String nameAttributeKey,
            User user
    ) {
        super(authorities, attributes, nameAttributeKey);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
