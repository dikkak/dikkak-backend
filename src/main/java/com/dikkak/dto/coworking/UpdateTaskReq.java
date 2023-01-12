package com.dikkak.dto.coworking;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateTaskReq {
    @NotNull
    private Long taskId;
    @NotNull
    private Boolean checked;
}
