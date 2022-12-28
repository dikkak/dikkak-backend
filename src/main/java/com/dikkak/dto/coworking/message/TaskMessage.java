package com.dikkak.dto.coworking.message;

import com.dikkak.entity.coworking.CoworkingTask;
import lombok.Data;

@Data
public class TaskMessage {
    private Long taskId;
    private String content;
    private Boolean isChecked;

    public TaskMessage(CoworkingTask task) {
        this.taskId = task.getId();
        this.content = task.getContent();
        this.isChecked = task.isComplete();
    }
}
