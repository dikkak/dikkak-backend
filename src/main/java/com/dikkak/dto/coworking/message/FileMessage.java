package com.dikkak.dto.coworking.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileMessage {
    private String email;
    private LocalDateTime createdAt;

    // 파일 메시지
    private String fileName;
    private String fileUrl;

    // 이미지 파일 여부
    @JsonProperty("isImageFile")
    private boolean isImageFile;

    @Builder
    public FileMessage(String email, LocalDateTime createdAt, String fileName, String fileUrl, boolean isImageFile) {
        this.email = email;
        this.createdAt = createdAt;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.isImageFile = isImageFile;
    }
}
