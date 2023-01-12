package com.dikkak.dto.coworking;

import lombok.Data;

@Data
public class AddTaskReq {
    private Long coworkingId;
    private String content;
    private Long fileId;
}
