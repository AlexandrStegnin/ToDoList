package com.teamdev.todolist.entities;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "task_status")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskStatus extends AbstractEntity {

    @Id
    @TableGenerator(name = "taskStatusSeqStore", table = "SEQ_STORE",
            pkColumnName = "SEQ_NAME", pkColumnValue = "TASK.STATUS.ID.PK",
            valueColumnName = "SEQ_VALUE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "taskStatusSeqStore")
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The database generated task status ID")
    private Long id;

    @Column
    @Size(min = 4, message = "Task status must be greater than 3 characters")
    @ApiModelProperty(notes = "Task status title")
    private String title;

    @Column
    @ApiModelProperty(notes = "Task status description")
    private String description;

}
