package com._p1m.portfolio.common.converter;

import com._p1m.portfolio.common.constant.BaseEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public abstract class BaseEnumConverter<E extends Enum<E> & BaseEnum<V>, V> implements AttributeConverter<E, V> {

    private final Class<E> enumClass;

    protected BaseEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public V convertToDatabaseColumn(E attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public E convertToEntityAttribute(V dbData) {
        return dbData != null ? BaseEnum.fromValue(enumClass, dbData) : null;
    }
}