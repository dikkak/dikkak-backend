package com.dikkak.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SortType {
    LATEST("latest"),
    OLD("old");

    private final String lowerCase;
}
