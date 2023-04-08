package com.dikkak.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class BaseException extends RuntimeException {

    private final ResponseMessage responseMessage;

}
