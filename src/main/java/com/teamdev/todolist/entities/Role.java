package com.teamdev.todolist.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Table;

import static com.teamdev.todolist.configurations.support.Constants.ROLE_PREFIX;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "role")
@EqualsAndHashCode(callSuper = true)
public class Role extends AbstractEntity implements GrantedAuthority {

    private String title;

    private String description;

    @Override
    public String getAuthority() {
        return ROLE_PREFIX + title;
    }
}
