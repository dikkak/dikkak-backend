package com.dikkak.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    private String message;
    private T data;

    public BaseResponse(ResponseMessage message) {
        this.message = message.getMessage();
    }
    public BaseResponse(T data) {
        this.data = data;
    }

    public BaseResponse(String message) {
        this.message = message;
    }

    public BaseResponse(BaseException e) {
        this.message = e.getResponseMessage().getMessage();
    }

}
