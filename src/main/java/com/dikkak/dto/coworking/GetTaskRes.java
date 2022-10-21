package com.dikkak.dto.coworking;

import com.dikkak.entity.coworking.StepType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class GetTaskRes {

    private Long taskId;
    private String content;
    private boolean isChecked;
    private StepType step;

    @QueryProjection
    public GetTaskRes(Long taskId, String content, boolean isChecked, StepType step) {
        this.taskId = taskId;
        this.content = content;
        this.isChecked = isChecked;
        this.step = step;
    }
}
