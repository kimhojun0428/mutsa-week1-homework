package mutsa.delivery.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderErrorCode implements BaseErrorCode {

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_404", "해당 주문을 찾을 수 없습니다."),
    ORDER_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "ORDER_400_1", "조리중/배달중/배달완료인 주문은 취소할 수 없습니다."),
    ORDER_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "ORDER_400_2", "이미 취소된 주문이 존재합니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
