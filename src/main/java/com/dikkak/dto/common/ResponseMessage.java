package com.dikkak.dto.common;

import lombok.Getter;

/**
 * 응답 메시지
 */
@Getter
public enum ResponseMessage {

    SUCCESS("요청에 성공하였습니다."),

    DUPLICATED_USER_EMAIL("중복된 이메일입니다."),
    NON_EXISTENT_EMAIL("존재하지 않는 이메일입니다."),
    INCORRECT_PASSWORD("올바르지 않은 비밀번호입니다."),

    INVALID_FORMAT_EMAIL("잘못된 형식의 이메일입니다."),
    INVALID_FORMAT_PASSWORD("잘못된 형식의 비밀번호입니다."),
    INVALID_FORMAT_PHONENUMBER("잘못된 형식의 전화번호입니다."),

    REQUIRED_ITEM_DISAGREE("필수 항목에 동의해야 합니다."),

    INVALID_ACCESS_TOKEN("올바르지 않은 ACCESS TOKEN 입니다."),
    INVALID_REFRESH_TOKEN("올바르지 않은 REFRESH TOKEN 입니다."),
    EXPIRED_TOKEN("만료된 토큰입니다."),

    DATABASE_ERROR("데이터베이스 오류입니다.");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }
}
