package com.teamdev.todolist.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "work_space")
@EqualsAndHashCode(callSuper = true, of = "id")
public class Workspace extends AbstractEntity {

    @Id
    @TableGenerator(name = "workSpaceSeqStore", table = "SEQ_STORE",
            pkColumnName = "SEQ_NAME", pkColumnValue = "WORKSPACE.ID.PK",
            valueColumnName = "SEQ_VALUE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "workSpaceSeqStore")
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The database generated work space ID")
    private Long id;

    @Column
    @Size(min = 3, max = 20, message = "Название рабочего пространства должно быть от 3 до 20 символов")
    @ApiModelProperty(notes = "Workspace title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @ApiModelProperty(notes = "Workspace owner")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @ApiModelProperty(notes = "Workspace team")
    private Team team;

    @OneToMany(mappedBy = "workspace", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Task> tasks;

    @OneToMany(mappedBy = "workspace", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Tag> tags;

}
