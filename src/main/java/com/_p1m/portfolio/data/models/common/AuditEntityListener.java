package com._p1m.portfolio.data.models.common;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

public class AuditEntityListener {

    @PrePersist
    public void setCreatedAtAndUpdatedAt(Object entity) {
        if (entity instanceof Auditable auditable) {
            LocalDateTime now = LocalDateTime.now();
            auditable.setCreatedAt(now);
            auditable.setUpdatedAt(now);
        }
    }

    @PreUpdate
    public void setUpdatedAt(Object entity) {
        if (entity instanceof Auditable auditable) {
            auditable.setUpdatedAt(LocalDateTime.now());
        }
    }
}
