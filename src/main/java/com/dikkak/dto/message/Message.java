package com.dikkak.dto.message;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message<T> {
    private MessageType type;
    private Long coworkingId;
    private T data;

    @Builder
    public Message(MessageType type, Long coworkingId, T data) {
        this.type = type;
        this.coworkingId = coworkingId;
        this.data = data;
    }
}
