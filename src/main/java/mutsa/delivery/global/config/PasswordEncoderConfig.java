package mutsa.delivery.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 비밀번호 암호화에 사용하는 인코더.
 *
 * <p>SecurityConfig와 분리해 둔다. SecurityConfig는 인증 인프라(필터·인가 규칙) 담당자가
 * 작성하므로, 두 사람이 같은 파일을 고치다 충돌하는 것을 막기 위함이다.
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
