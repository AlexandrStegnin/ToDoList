package com.teamdev.todolist.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Alexandr Stegnin
 * Абстрактный класс, чтобы не дублировать поля во всех сущностях
 */

@Data
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;

    @PrePersist
    private void prePersist() {
        if (created == null) created = LocalDateTime.now();
        updated = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updated = LocalDateTime.now();
    }

}
