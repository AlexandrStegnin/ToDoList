package com.teamdev.todolist.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Alexandr Stegnin
 * Абстрактный класс, чтобы не дублировать поля во всех сущностях
 */

@Data
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    @Column
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime created;

    @Column
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime updated;

    @PrePersist
    private void prePersist() {
        created = LocalDateTime.now();
        updated = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        if (Objects.equals(null, created)) created = LocalDateTime.now();
        updated = LocalDateTime.now();
    }

}
