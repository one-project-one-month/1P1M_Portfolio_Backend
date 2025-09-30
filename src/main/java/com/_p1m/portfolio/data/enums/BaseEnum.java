package com._p1m.portfolio.data.enums;

public interface BaseEnum<V> {

    V getValue();

    static <E extends Enum<E> & BaseEnum<?>> E fromValue(Class<E> enumClass, Object value) {
        for (E type : enumClass.getEnumConstants()) {
            if (type.getValue().toString().equals(value.toString())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}