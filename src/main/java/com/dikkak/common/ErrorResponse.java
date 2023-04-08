package com.dikkak.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(BaseException e) {
        this.message = e.getResponseMessage().getMessage();
    }

}
