package mutsa.delivery.global.apiPayload.handler;

import mutsa.delivery.global.apiPayload.GlobalResponse;
import mutsa.delivery.global.apiPayload.code.GeneralErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void jsonTypeMismatchReturnsInvalidType() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(
                "Cannot deserialize value", new InvalidFormatException(), mock(HttpInputMessage.class));

        ResponseEntity<GlobalResponse<Void>> response =
                handler.handleHttpMessageNotReadableException(exception);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(GeneralErrorCode.INVALID_TYPE.getCode());
    }

    @Test
    void malformedJsonReturnsInvalidRequest() {
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException(
                        "Malformed JSON", new RuntimeException(), mock(HttpInputMessage.class));

        ResponseEntity<GlobalResponse<Void>> response =
                handler.handleHttpMessageNotReadableException(exception);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(GeneralErrorCode.INVALID_REQUEST.getCode());
    }

    private static class InvalidFormatException extends RuntimeException {
    }
}
