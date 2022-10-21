package com.dikkak.aop;

import com.dikkak.common.BaseException;
import com.dikkak.common.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.dikkak.common.ResponseMessage.INVALID_ACCESS_TOKEN;

@RestControllerAdvice
public class ControllerAdviceCustom {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<?>> handleException(BaseException e) {
        ResponseEntity.BodyBuilder res;
        if(e.getResponseMessage().equals(INVALID_ACCESS_TOKEN))
             res = ResponseEntity.status(HttpStatus.UNAUTHORIZED);
        else res = ResponseEntity.badRequest();
        return res.body(new BaseResponse<>(e));
    }
}
