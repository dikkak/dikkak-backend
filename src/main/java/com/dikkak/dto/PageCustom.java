package com.dikkak.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PageCustom<T> {

    List<T> content;
    boolean hasNext;
    boolean hasPrev;
    int next;
    int prev;

    @Builder
    public PageCustom(List<T> content, boolean hasNext, boolean hasPrev, int next, int prev) {
        this.content = content;
        this.hasNext = hasNext;
        this.hasPrev = hasPrev;
        this.next = next;
        this.prev = prev;
    }
}
