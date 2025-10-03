package com._p1m.portfolio.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ROLE implements BaseEnum<Integer>{
    USER(1),
    ADMIN(2);

    private final int value;

    @Override
    public Integer getValue() {
        return value;
    }
}
