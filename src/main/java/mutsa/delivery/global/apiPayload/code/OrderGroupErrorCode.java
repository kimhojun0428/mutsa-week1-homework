package mutsa.delivery.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderGroupErrorCode implements BaseErrorCode{
    ORDER_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_404", "해당 주문을 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
