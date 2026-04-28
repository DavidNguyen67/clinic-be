package com.camel.clinic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;


@Getter
@Setter
@MappedSuperclass
@SQLRestriction("deleted_at IS NULL")
@EntityListeners(AuditingEntityListener.class)
public abstract class SoftDeletableEntity extends BaseEntity {

    @Column(name = "deleted_at")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date deletedAt;

    public void softDelete(String deletedBy) {
        this.deletedAt = new Date();
    }

    public void restore() {
        this.deletedAt = null;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
