package com._p1m.portfolio.common.converter;

import com._p1m.portfolio.common.constant.Status;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter extends BaseEnumConverter<Status, Integer>{

     public StatusConverter() {
        super(Status.class);
    }
}