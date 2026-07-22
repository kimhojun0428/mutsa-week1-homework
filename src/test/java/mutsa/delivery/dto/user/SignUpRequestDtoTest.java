package mutsa.delivery.dto.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 소셜 회원을 위해 User.password 를 nullable 로 바꾼 뒤에도(#35),
 * 일반 회원가입은 여전히 비밀번호가 필수(NotBlank)이고 8자 이상이어야 함을 고정한다.
 * DB 컬럼 제약이 아니라 요청 DTO의 Bean Validation 이 이 규칙을 보장한다.
 */
class SignUpRequestDtoTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    private boolean hasPasswordViolation(SignUpRequestDto dto) {
        Set<ConstraintViolation<SignUpRequestDto>> violations = validator.validate(dto);
        return violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    @DisplayName("비밀번호가 null 이면 검증 위반이다")
    void nullPasswordIsInvalid() {
        SignUpRequestDto dto = new SignUpRequestDto("user@example.com", null, "홍길동");

        assertThat(hasPasswordViolation(dto)).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 빈 문자열이면 검증 위반이다")
    void blankPasswordIsInvalid() {
        SignUpRequestDto dto = new SignUpRequestDto("user@example.com", "   ", "홍길동");

        assertThat(hasPasswordViolation(dto)).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 8자 미만이면 검증 위반이다")
    void tooShortPasswordIsInvalid() {
        SignUpRequestDto dto = new SignUpRequestDto("user@example.com", "1234567", "홍길동");

        assertThat(hasPasswordViolation(dto)).isTrue();
    }

    @Test
    @DisplayName("8자 이상 비밀번호는 검증을 통과한다")
    void validPasswordPasses() {
        SignUpRequestDto dto = new SignUpRequestDto("user@example.com", "password123", "홍길동");

        assertThat(hasPasswordViolation(dto)).isFalse();
    }
}
