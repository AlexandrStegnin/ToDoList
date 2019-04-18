package com.teamdev.todolist.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "task")
@NoArgsConstructor
@ToString(exclude = "workspace")
@EqualsAndHashCode(callSuper = true, of = {"id", "title", "description"})
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
    @Size(min = 5, message = "Название задачи должно быть более 4 символов")
    @ApiModelProperty(notes = "Task title")
    private String title;

    @Column
    @Size(min = 5, message = "Описание задачи должно быть более 4 символов")
    @ApiModelProperty(notes = "Task description")
    private String description;

    @OneToOne
    @JoinColumn(name = "author_id")
    @ApiModelProperty(notes = "Task author")
    private User author;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "performer",
            joinColumns = @JoinColumn(name = "task_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "performer_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "performer_to_task"),
            inverseForeignKey = @ForeignKey(name = "performer_to_user")
    )
    @ApiModelProperty(notes = "Task performers")
    private Set<User> performers = new HashSet<>();

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @ApiModelProperty(notes = "Date and time of creation task")
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @ApiModelProperty(notes = "Date and time of execution task")
    private LocalDateTime executionDate = creationDate.plusDays(1);

    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    @ApiModelProperty(notes = "Parent task")
    private Task parentTask;

    @OneToOne
    private TaskStatus status;

    @Column
    private String comment;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "task_tag",
            joinColumns = @JoinColumn(name = "task_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "task_tag_to_task"),
            inverseForeignKey = @ForeignKey(name = "task_tag_to_tag")
    )
    @ApiModelProperty(notes = "Task tags")
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne
    @JsonBackReference(value = "task-workspace")
    @JoinColumn(name = "workspace_id")
    @ApiModelProperty(notes = "Task workspace")
    @NotNull(message = "Выбери рабочую область для задачи")
    private Workspace workspace;

    public void addPerformer(User performer) {
        performers.add(performer);
    }

    public void removePerformer(User performer) {
        if (!Objects.equals(null, performers)) performers.remove(performer);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void removeTag(Tag tag) {
        if (!Objects.equals(null, tags)) tags.remove(tag);
    }

}
