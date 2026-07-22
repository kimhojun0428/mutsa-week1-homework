package mutsa.delivery.global.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void corsConfigurationUsesConfiguredAllowedOrigins() {
        SecurityConfig securityConfig = new SecurityConfig(
                null,
                null,
                null,
                null,
                null,
                null
        );

        CorsConfigurationSource source = securityConfig.corsConfigurationSource(List.of(
                " https://week7-homework.vercel.app ",
                "https://mutsa.dev.me.kr",
                " "
        ));
        CorsConfiguration configuration = source.getCorsConfiguration(
                new MockHttpServletRequest("OPTIONS", "/api/auth/login")
        );

        assertThat(configuration).isNotNull();
        assertThat(configuration.getAllowedOrigins()).containsExactly(
                "https://week7-homework.vercel.app",
                "https://mutsa.dev.me.kr"
        );
        assertThat(configuration.getAllowedMethods()).contains("POST", "OPTIONS");
        assertThat(configuration.getAllowCredentials()).isTrue();
    }
}
