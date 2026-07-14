package mutsa.delivery.service;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.User;
import mutsa.delivery.dto.user.UpdateUserRequestDto;
import mutsa.delivery.dto.user.UserResponseDto;
import mutsa.delivery.global.apiPayload.code.UserErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import mutsa.delivery.repository.AddressRepository;
import mutsa.delivery.repository.CartRepository;
import mutsa.delivery.repository.CreditHistoryRepository;
import mutsa.delivery.repository.OrderGroupRepository;
import mutsa.delivery.repository.OrderRepository;
import mutsa.delivery.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final OrderRepository orderRepository;
    private final OrderGroupRepository orderGroupRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto getMyInfo(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ProjectException(UserErrorCode.USER_NOT_FOUND));

        return UserResponseDto.from(user);
    }

    @Transactional
    public UserResponseDto updateMyInfo(Long userId, UpdateUserRequestDto requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ProjectException(UserErrorCode.USER_NOT_FOUND));

        String password = requestDto.password();
        String encodedPassword = (password != null && !password.isBlank())
                ? passwordEncoder.encode(password)
                : null;

        user.updateInfo(requestDto.name(), encodedPassword);

        return UserResponseDto.from(user);
    }

    @Transactional
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ProjectException(UserErrorCode.USER_NOT_FOUND));

        cartRepository.findByUserId(userId).ifPresent(cartRepository::delete);
        cartRepository.flush();

        orderRepository.deleteAll(orderRepository.findAllByUser_Id(userId));
        orderRepository.flush();

        orderGroupRepository.deleteAllByUser_Id(userId);
        creditHistoryRepository.deleteAllByUser_Id(userId);
        addressRepository.deleteAllByUser_Id(userId);
        userRepository.delete(user);
    }
}
