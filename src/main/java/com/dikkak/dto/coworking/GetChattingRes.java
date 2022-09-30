package com.dikkak.dto.coworking;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드 제외
public class GetChattingRes {
    private String email;
    private LocalDateTime createdAt;

    // 텍스트 메시지
    private String content;

    // 파일 메시지
    private String fileName;
    private String fileUrl;

    @QueryProjection
    public GetChattingRes(String email, String content,
                          String fileName, String fileUrl, LocalDateTime createdAt) {
        this.email = email;
        this.content = content;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.createdAt = createdAt;
    }
}
