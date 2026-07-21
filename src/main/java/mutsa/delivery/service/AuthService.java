package mutsa.delivery.service;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.User;
import mutsa.delivery.dto.auth.LoginRequestDto;
import mutsa.delivery.dto.auth.TokenResponseDto;
import mutsa.delivery.dto.user.SignUpRequestDto;
import mutsa.delivery.dto.user.UserResponseDto;
import mutsa.delivery.global.apiPayload.code.AuthErrorCode;
import mutsa.delivery.global.apiPayload.code.UserErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import mutsa.delivery.global.jwt.JwtProvider;
import mutsa.delivery.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public UserResponseDto signUp(SignUpRequestDto requestDto) {

        if (userRepository.existsByEmail(requestDto.email())) {
            throw new ProjectException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.create(
                requestDto.email(),
                passwordEncoder.encode(requestDto.password()),
                requestDto.name()
        );
        userRepository.save(user);

        return UserResponseDto.from(user);
    }

    public TokenResponseDto login(LoginRequestDto requestDto) {

        // 존재하지 않는 이메일과 비밀번호 불일치를 같은 에러로 응답해 계정 존재 여부가 노출되지 않도록 한다.
        User user = userRepository.findByEmail(requestDto.email())
                .orElseThrow(() -> new ProjectException(AuthErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(requestDto.password(), user.getPassword())) {
            throw new ProjectException(AuthErrorCode.LOGIN_FAILED);
        }

        String accessToken = jwtProvider.createToken(user.getId(), user.getEmail());

        return TokenResponseDto.of(accessToken);
    }

    /**
     * 로그아웃. 현재는 무상태(stateless) JWT 방식이라 서버가 파기할 세션·토큰 저장소가 없으므로,
     * 실제 토큰 삭제는 클라이언트가 담당한다(저장한 Access Token 제거). 인증 여부와 무관하게 멱등하다.
     *
     * <p>향후 RefreshToken 저장소나 토큰 블랙리스트를 도입하면 이 지점에서 파기 로직을 추가한다.
     */
    public void logout(Long userId) {
        // no-op
    }
}
