package com.expernet.corpcard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@MappedSuperclass
public class BaseEntity {
    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private Timestamp createdAt;
    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;
}
