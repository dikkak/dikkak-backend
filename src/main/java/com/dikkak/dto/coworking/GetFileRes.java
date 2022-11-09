package com.dikkak.dto.coworking;

import com.dikkak.entity.coworking.StepType;
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
    private StepType step;


    @QueryProjection
    public GetFileRes(Long fileId, String fileName, String fileUrl, boolean isImageFile, StepType step) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.isImageFile = isImageFile;
        this.step = step;
    }
}
