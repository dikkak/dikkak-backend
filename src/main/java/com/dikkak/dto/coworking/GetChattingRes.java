package com.dikkak.dto.coworking;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드 제외
public class GetChattingRes {
    private Long messageId;
    private Long userId;
    private String content;
    private String fileUrl;
    private LocalDateTime createdAt;

    @QueryProjection
    public GetChattingRes(Long id, Long userId, String content, String fileUrl, LocalDateTime createdAt) {
        this.messageId = id;
        this.userId = userId;
        this.content = content;
        this.fileUrl = fileUrl;
        this.createdAt = createdAt;
    }
}
