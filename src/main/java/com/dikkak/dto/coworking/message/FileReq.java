package com.dikkak.dto.coworking.message;

import lombok.Data;

@Data
public class FileReq {
    private String email;
    private Long coworkingId;
}
