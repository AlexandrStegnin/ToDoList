package com.teamdev.todolist.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Size;

import static com.teamdev.todolist.configuration.support.Constants.ROLE_PREFIX;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "role")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Role extends AbstractEntity implements GrantedAuthority {

    @Id
    @TableGenerator(name = "roleSeqStore", table = "SEQ_STORE",
            pkColumnName = "SEQ_NAME", pkColumnValue = "ROLE.ID.PK",
            valueColumnName = "SEQ_VALUE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "roleSeqStore")
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The database generated role ID")
    private Long id;

    @Column
    @Size(min = 3, max = 10, message = "Role title must be greater than 2 and less than 10 characters")
    @ApiModelProperty(notes = "The role title")
    private String title;

    @Column
    @ApiModelProperty(notes = "The role description")
    private String description;

    @Override
    @ApiModelProperty(notes = "Role with role prefix (ROLE_)")
    public String getAuthority() {
        return title.startsWith(ROLE_PREFIX) ? title : ROLE_PREFIX + title;
    }

    @PrePersist
    public void serRole() {
        if (!title.trim().toUpperCase().startsWith(ROLE_PREFIX)) title = ROLE_PREFIX + title.trim().toUpperCase();
        else title = title.trim().toUpperCase();
    }

    public Role(GrantedAuthority authority) {
        this.title = authority.getAuthority();
    }
}
