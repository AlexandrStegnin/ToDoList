package com.teamdev.todolist.entity;

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
@NoArgsConstructor
@Table(name = "tag")
@EqualsAndHashCode(callSuper = true)
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
    @Size(min = 3, max = 15, message = "Tag title must be greater than 2 and less than 15 characters")
    @ApiModelProperty(notes = "The tag title")
    private String title;

}
