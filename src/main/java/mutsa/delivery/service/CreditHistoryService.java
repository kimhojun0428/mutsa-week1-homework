package mutsa.delivery.service;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.CreditHistory;
import mutsa.delivery.domain.User;
import mutsa.delivery.dto.credit.CreditChangeRequestDto;
import mutsa.delivery.dto.credit.CreditHistoryResponseDto;
import mutsa.delivery.global.apiPayload.code.CreditErrorCode;
import mutsa.delivery.global.apiPayload.code.UserErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import mutsa.delivery.repository.CreditHistoryRepository;
import mutsa.delivery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreditHistoryService {

    private final CreditHistoryRepository creditHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreditHistoryResponseDto changeCredit(Long userId, CreditChangeRequestDto requestDto) {

        if (requestDto.amount() < 1) {
            throw new ProjectException(CreditErrorCode.INVALID_CHARGE_AMOUNT);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ProjectException(UserErrorCode.USER_NOT_FOUND));

        if (requestDto.type().isIncrease()) {
            user.chargeCredit(requestDto.amount());
        } else {
            user.useCredit(requestDto.amount());
        }

        CreditHistory history = creditHistoryRepository.save(
                CreditHistory.of(user, requestDto.amount(), requestDto.type())
        );

        return CreditHistoryResponseDto.from(history);
    }

    public List<CreditHistoryResponseDto> getHistories(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new ProjectException(UserErrorCode.USER_NOT_FOUND));

        return creditHistoryRepository.findAllByUser_IdOrderByIdDesc(userId).stream()
                .map(CreditHistoryResponseDto::from)
                .toList();
    }
}
