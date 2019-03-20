package com.teamdev.todolist.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "task")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Task extends AbstractEntity {

    @Id
    @TableGenerator(name = "taskSeqStore", table = "SEQ_STORE",
            pkColumnName = "SEQ_NAME", pkColumnValue = "TASK.ID.PK",
            valueColumnName = "SEQ_VALUE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "taskSeqStore")
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The database generated task ID")
    private Long id;

    @Column
    @NotNull
    @ApiModelProperty(notes = "Task title")
    private String title;

    @Column
    @ApiModelProperty(notes = "Task description")
    private String description;

    @OneToOne
    @JoinColumn(name = "author_id")
    @ApiModelProperty(notes = "Task author")
    private User author;

    @ManyToMany
    @JoinTable(
            name = "performer",
            joinColumns = @JoinColumn(name = "performer_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "task_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "performer_to_task"),
            inverseForeignKey = @ForeignKey(name = "performer_to_user")
    )
    @ApiModelProperty(notes = "Task performers")
    private List<User> performers;

    @Column
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    @ApiModelProperty(notes = "Date and time of creation task")
    private LocalDateTime creationDate;

    @Column
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    @ApiModelProperty(notes = "Date and time of execution task")
    private LocalDateTime executionDate;

    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    @ApiModelProperty(notes = "Parent task")
    private Task parentTask;

    @OneToOne
    private TaskStatus status;

}
