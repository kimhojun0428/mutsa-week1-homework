package mutsa.delivery.service;

import mutsa.delivery.domain.CreditHistoryType;
import mutsa.delivery.dto.credit.CreditChangeRequestDto;
import mutsa.delivery.global.apiPayload.code.CreditErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import mutsa.delivery.repository.CreditHistoryRepository;
import mutsa.delivery.repository.UserRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class CreditHistoryServiceTest {

    private final CreditHistoryRepository creditHistoryRepository = mock(CreditHistoryRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CreditHistoryService creditHistoryService =
            new CreditHistoryService(creditHistoryRepository, userRepository);

    @Test
    void nonPositiveAmountReturnsCreditError() {
        CreditChangeRequestDto request = new CreditChangeRequestDto(0L, CreditHistoryType.CHARGE);

        assertThatThrownBy(() -> creditHistoryService.changeCredit(1L, request))
                .isInstanceOf(ProjectException.class)
                .extracting("errorCode")
                .isEqualTo(CreditErrorCode.INVALID_CHARGE_AMOUNT);

        verifyNoInteractions(userRepository, creditHistoryRepository);
    }
}
