package com.dikkak.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class BaseException extends Exception{

    private ResponseMessage responseMessage;

}
