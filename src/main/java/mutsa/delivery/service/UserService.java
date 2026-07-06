package mutsa.delivery.service;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.User;
import mutsa.delivery.dto.user.SignUpRequestDto;
import mutsa.delivery.dto.user.UpdateUserRequestDto;
import mutsa.delivery.dto.user.UserResponseDto;
import mutsa.delivery.global.apiPayload.code.UserErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import mutsa.delivery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto signUp(SignUpRequestDto requestDto) {

        if (userRepository.existsByEmail(requestDto.email())) {
            throw new ProjectException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.create(
                requestDto.email(),
                requestDto.password(),
                requestDto.name()
        );
        userRepository.save(user);

        return UserResponseDto.from(user);
    }

    public UserResponseDto getMyInfo(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ProjectException(UserErrorCode.USER_NOT_FOUND));

        return UserResponseDto.from(user);
    }

    @Transactional
    public UserResponseDto updateMyInfo(Long userId, UpdateUserRequestDto requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ProjectException(UserErrorCode.USER_NOT_FOUND));

        user.updateInfo(requestDto.name(), requestDto.password());

        return UserResponseDto.from(user);
    }

    @Transactional
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ProjectException(UserErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }
}
