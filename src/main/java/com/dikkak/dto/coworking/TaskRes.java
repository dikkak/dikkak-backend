package com.dikkak.dto.coworking;

import com.dikkak.entity.coworking.CoworkingTask;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class TaskRes {

    private Long taskId;
    private String content;
    private boolean isChecked;

    @QueryProjection
    public TaskRes(Long taskId, String content, boolean isChecked) {
        this.taskId = taskId;
        this.content = content;
        this.isChecked = isChecked;
    }

    public static TaskRes fromEntity(CoworkingTask task) {
        return new TaskRes(task.getId(), task.getContent(), task.isComplete());
    }
}
