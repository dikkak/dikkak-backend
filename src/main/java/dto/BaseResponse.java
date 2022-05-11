package dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import static dto.ResponseCode.SUCCESS;

@Getter
@JsonPropertyOrder({"success", "code", "message", "result"})
public class BaseResponse<T> {

    private final boolean isSuccess;
    private final int code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL) // NULL 이 아닐 경우에만 포함
    private T result;

    // 요청 성공한 경우
    public BaseResponse(T result) {
        this.isSuccess = SUCCESS.isSuccess();
        this.code = SUCCESS.getCode();
        this.message = SUCCESS.getMessage();
        this.result = result;
    }

    // 요청 실패한 경우
    public BaseResponse(ResponseCode status) {
        this.isSuccess = status.isSuccess();
        this.code = status.getCode();
        this.message = status.getMessage();
    }
}
