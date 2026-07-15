package mutsa.delivery.global.apiPayload.handler;

import lombok.extern.slf4j.Slf4j;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import mutsa.delivery.global.apiPayload.code.BaseErrorCode;
import mutsa.delivery.global.apiPayload.code.GeneralErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 프로젝트 정의 예외 (도메인 에러코드)
    @ExceptionHandler(ProjectException.class)
    public ResponseEntity<GlobalResponse<Void>> handleProjectException(
            ProjectException e
    ) {
        BaseErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(GlobalResponse.onFailure(errorCode, null));
    }

    // @Valid 검증 실패 (필수값 누락 등) -> INVALID_REQUEST
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        BaseErrorCode errorCode = GeneralErrorCode.INVALID_REQUEST;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(GlobalResponse.onFailure(errorCode, errors));
    }

    // JSON 값의 타입 불일치는 INVALID_TYPE, 문법 오류와 본문 누락은 INVALID_REQUEST
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GlobalResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e
    ) {
        BaseErrorCode errorCode = isJsonTypeMismatch(e)
                ? GeneralErrorCode.INVALID_TYPE
                : GeneralErrorCode.INVALID_REQUEST;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(GlobalResponse.onFailure(errorCode, null));
    }

    private boolean isJsonTypeMismatch(Throwable throwable) {
        Throwable cause = throwable;
        while (cause != null) {
            String exceptionName = cause.getClass().getSimpleName();
            if ("InvalidFormatException".equals(exceptionName)
                    || "MismatchedInputException".equals(exceptionName)) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    // 타입 불일치 (예: 숫자 자리에 문자) -> INVALID_TYPE
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GlobalResponse<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e
    ) {
        BaseErrorCode errorCode = GeneralErrorCode.INVALID_TYPE;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(GlobalResponse.onFailure(errorCode, null));
    }

    // 그 외 예기치 못한 예외 -> INTERNAL_SERVER_ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalResponse<String>> handleUndefinedException(
            Exception e
    ) {
        log.error("예기치 않은 오류 발생: ", e);

        BaseErrorCode errorCode = GeneralErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(GlobalResponse.onFailure(errorCode, errorCode.getMessage()));
    }
}
