package com._p1m.portfolio.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Platform implements BaseEnum<Integer> {
    Github(1),
    LinkedIn(2),
    Behance(3);

    private final int value;

    @Override
    public Integer getValue() {
        return value;
    }
}
