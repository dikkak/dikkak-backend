package com.dikkak.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class BaseResponse {

    private final String message;

    public BaseResponse(ResponseMessage message) {
        this.message = message.getMessage();
    }

    public BaseResponse(BaseException e) {
        this.message = e.getResponseMessage().getMessage();
    }

}
