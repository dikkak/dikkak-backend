package com.dikkak.entity;

import com.dikkak.entity.user.StatusType;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime lastModifiedAt;

    @Column
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ACTIVE'")
    private StatusType status = StatusType.ACTIVE;
}
