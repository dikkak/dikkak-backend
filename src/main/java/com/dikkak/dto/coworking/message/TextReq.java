package com.dikkak.dto.coworking.message;

import lombok.Data;

@Data
public class TextReq {
    private String email;
    private String content;
    private Long coworkingId;
}
