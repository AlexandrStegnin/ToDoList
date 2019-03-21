package com.teamdev.todolist.entities;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

import static com.teamdev.todolist.configurations.support.Constants.ROLE_PREFIX;

/**
 * @author Leonid Lebidko
 */

@Data
@Entity
@Table(name = "performer")
@EqualsAndHashCode(callSuper = true)
public class Performer extends AbstractEntity {

    @Id
    @TableGenerator(name = "performerSeqStore", table = "SEQ_STORE",
            pkColumnName = "SEQ_NAME", pkColumnValue = "PERFORMER.ID.PK",
            valueColumnName = "SEQ_VALUE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "performerSeqStore")
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The database generated performer ID")
    private Long id;

    @Column
    @ApiModelProperty(notes = "Task ID")
    private Long task_id;

    @Column
    @ApiModelProperty(notes = "User ID")
    private Long performer_id;


}
