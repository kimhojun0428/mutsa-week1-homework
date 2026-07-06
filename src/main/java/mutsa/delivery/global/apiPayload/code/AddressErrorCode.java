package mutsa.delivery.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AddressErrorCode implements BaseErrorCode {

    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "ADDRESS_404_1", "해당 주소를 찾을 수 없습니다."),
    ADDRESS_ALREADY_EXISTS(HttpStatus.CONFLICT, "ADDRESS_409_1", "해당 주소가 이미 존재합니다."),
    ADDRESS_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "ADDRESS_409_2", "해당 주소지명이 이미 존재합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
