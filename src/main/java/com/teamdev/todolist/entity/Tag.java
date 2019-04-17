package com.teamdev.todolist.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@NoArgsConstructor
@Table(name = "tag")
@ToString(exclude = "workspace")
@EqualsAndHashCode(callSuper = true, of = {"id", "title"})
public class Tag extends AbstractEntity {

    @Id
    @TableGenerator(name = "tagSeqStore", table = "SEQ_STORE",
            pkColumnName = "SEQ_NAME", pkColumnValue = "TAG.ID.PK",
            valueColumnName = "SEQ_VALUE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tagSeqStore")
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The database generated tag ID")
    private Long id;

    @Column
    @Size(min = 3, max = 15, message = "Название тэга должно быть больше 2 и меньше 16 символов")
    @ApiModelProperty(notes = "The tag title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    public Tag(Workspace workspace) {
        this.workspace = workspace;
    }

}
