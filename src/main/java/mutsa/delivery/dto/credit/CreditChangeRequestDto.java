package mutsa.delivery.dto.credit;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import mutsa.delivery.domain.CreditHistoryType;

public record CreditChangeRequestDto(

        @NotNull(message = "금액을 입력해주세요.")
        @Schema(description = "변동할 크레딧 금액", example = "10000", minimum = "1")
        Long amount,

        @NotNull(message = "변동 유형(CHARGE/USE/REFUND)을 입력해주세요.")
        @Schema(description = "크레딧 변동 유형", example = "CHARGE")
        CreditHistoryType type

) {}
