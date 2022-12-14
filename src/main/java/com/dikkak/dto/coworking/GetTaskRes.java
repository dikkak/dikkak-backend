package com.dikkak.dto.coworking;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class GetTaskRes {

    private Long taskId;
    private String content;
    private boolean isChecked;

    @QueryProjection
    public GetTaskRes(Long taskId, String content, boolean isChecked) {
        this.taskId = taskId;
        this.content = content;
        this.isChecked = isChecked;
    }
}
