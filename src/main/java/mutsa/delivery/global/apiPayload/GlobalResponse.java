package mutsa.delivery.global.apiPayload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mutsa.delivery.global.apiPayload.code.BaseErrorCode;
import mutsa.delivery.global.apiPayload.code.GeneralSuccessCode;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"success", "code", "message", "data", "error"})
public class GlobalResponse<T> {

    @JsonProperty("success")
    private final Boolean success;

    @JsonProperty("code")
    private final String code;

    @JsonProperty("message")
    private final String message;

    @JsonProperty("data")
    private final T data;

    @JsonProperty("error")
    private final Object error;

    public static <T> GlobalResponse<T> onSuccess(T data) {
        return new GlobalResponse<>(true, GeneralSuccessCode.OK.getCode(), GeneralSuccessCode.OK.getMessage(), data, null);
    }

    public static <T> GlobalResponse<T> onSuccess(){
        return new GlobalResponse<>(true, GeneralSuccessCode.OK.getCode(), GeneralSuccessCode.OK.getMessage(), null, null);
    }

    public static <T> GlobalResponse<T> onSuccessCreate(T data) {
        return new GlobalResponse<>(true, GeneralSuccessCode.CREATED.getCode(), GeneralSuccessCode.CREATED.getMessage(), data, null);
    }


    public static <T> GlobalResponse<T> onFailure(BaseErrorCode errorCode, Object error) {
        return new GlobalResponse<>(false, errorCode.getCode(), errorCode.getMessage(),null, error);
    }
}
