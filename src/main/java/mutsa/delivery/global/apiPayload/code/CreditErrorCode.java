package mutsa.delivery.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CreditErrorCode implements BaseErrorCode {

    INSUFFICIENT_CREDIT(HttpStatus.BAD_REQUEST, "CREDIT_400_1", "크레딧 잔액이 부족합니다."),
    INVALID_CHARGE_AMOUNT(HttpStatus.BAD_REQUEST, "CREDIT_400_2", "충전 금액은 1 이상이어야 합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
