package com.dikkak.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class BaseResponse<T> {

    private String message;

    // null인 경우는 제외한다.
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;


    public BaseResponse(ResponseMessage message) {
        this.message = message.getMessage();
    }

    public BaseResponse(T data) {
        this.message = ResponseMessage.SUCCESS.getMessage();
        this.data = data;
    }



}
