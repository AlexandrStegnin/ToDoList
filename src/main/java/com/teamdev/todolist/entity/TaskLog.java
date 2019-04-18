package com.teamdev.todolist.entity;

import com.teamdev.todolist.configuration.support.OperationEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@ToString
@Table(name = "task_log")
@EqualsAndHashCode(callSuper = false)
public class TaskLog extends AbstractEntity {

    @Id
    @TableGenerator(name = "taskLogSeqStore", table = "SEQ_STORE",
            pkColumnName = "SEQ_NAME", pkColumnValue = "TASK.LOG.ID.PK",
            valueColumnName = "SEQ_VALUE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "taskLogSeqStore")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "user_id")
    private Long editorId;

    @Column(name = "operation")
    @Enumerated(EnumType.STRING)
    private OperationEnum operation;

    @Column(name = "comment")
    private String comment;

}
