package com.dikkak.dto.coworking;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class GetFileRes {

    private Long fileId;
    private String fileName;
    private String fileUrl;

    // 이미지 파일 여부
    @JsonProperty("isImageFile")
    private boolean isImageFile;


    @QueryProjection
    public GetFileRes(Long fileId, String fileName, String fileUrl, boolean isImageFile) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.isImageFile = isImageFile;
    }
}
