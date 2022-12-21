package com.dikkak.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextMessage {
    private String email;
    private String content;

    private LocalDateTime createdAt;


    @Builder
    public TextMessage(String email, String content, LocalDateTime createdAt) {
        this.email = email;
        this.content = content;
        this.createdAt = createdAt;
    }
}
