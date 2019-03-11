package com.teamdev.todolist.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * @author Alexandr Stegnin
 * Абстрактный класс, чтобы не дублировать поля во всех сущностях
 */

@Data
@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate created;

    private LocalDate updated;

    @PrePersist
    private void prePersist() {
        if (created == null) created = LocalDate.now();
        updated = LocalDate.now();
    }

    @PreUpdate
    private void preUpdate() {
        updated = LocalDate.now();
    }

}
