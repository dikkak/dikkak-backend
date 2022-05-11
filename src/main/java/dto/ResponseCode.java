package dto;

import lombok.Getter;

/**
 * 응답 코드 관리
 */
@Getter
public enum ResponseCode {

    /***
     * 100: 성공
     */
    SUCCESS(true, 100, "요청에 성공하였습니다."),

    /**
     * 200: Request 오류
     */


    /***
     * 300: Response 오류
     */
    DUPLICATED_USER_EMAIL(false, 301, "중복된 이메일입니다."),

    /**
     * 400 : Database, Server 오류
     */
    DATABASE_ERROR(false, 400, "데이터베이스 연결에 실패하였습니다.");



    private final boolean isSuccess;
    private final int code;
    private final String message;

    ResponseCode(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
