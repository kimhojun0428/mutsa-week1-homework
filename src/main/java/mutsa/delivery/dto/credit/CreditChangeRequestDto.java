package mutsa.delivery.dto.credit;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import mutsa.delivery.domain.CreditHistoryType;

public record CreditChangeRequestDto(

        @NotNull(message = "금액을 입력해주세요.")
        @Min(value = 1, message = "금액은 1 이상이어야 합니다.")
        Long amount,

        @NotNull(message = "변동 유형(CHARGE/USE/REFUND)을 입력해주세요.")
        CreditHistoryType type

) {}
