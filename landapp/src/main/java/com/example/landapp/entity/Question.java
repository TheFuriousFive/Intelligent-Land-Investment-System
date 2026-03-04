package com.example.landapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Data
@EqualsAndHashCode(callSuper = true)

public class Question extends Message {

    @CreationTimestamp //Automatically set the time when insert
    @Column(updatable = false, nullable = false) //Ensure its never changed or null
    private LocalDateTime createdAt;
}
