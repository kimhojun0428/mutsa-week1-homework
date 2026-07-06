package mutsa.delivery.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CartErrorCode implements BaseErrorCode {

    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CART_404_1", "해당 장바구니를 찾을 수 없습니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "CART_404_2", "장바구니 내 해당 항목을 찾을 수 없습니다."),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "CART_400_1", "재고가 부족합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
