package com.dikkak.aop;

import com.dikkak.common.BaseException;
import com.dikkak.common.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.constraints.Null;

@RestControllerAdvice
public class ControllerAdviceCustom {

    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Null> handleException(BaseException e) {
        return new BaseResponse<>(e);
    }
}
