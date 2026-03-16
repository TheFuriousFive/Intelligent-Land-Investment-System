package com.example.landapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Question extends Message {

    @CreationTimestamp //Automatically set the time when insert
    @Column(updatable = false, nullable = false) //Ensure its never changed or null
    private LocalDateTime createdAt;
    private long investorId;
}
