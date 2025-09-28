package com._p1m.portfolio.common.entity;

import com._p1m.portfolio.common.constant.Status;
import com._p1m.portfolio.common.converter.StatusConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class MasterData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    @Column
    @Convert(converter = StatusConverter.class)
    private Status status = Status.ACTIVE;

    public void delete() {
        this.setStatus(Status.INACTIVE);
        this.setDeletedAt(LocalDateTime.now());
    }
}