package com.teamdev.todolist.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "roles")
@EqualsAndHashCode(callSuper = true)
public class Role extends AbstractEntity implements GrantedAuthority {

    private String title;

    @Override
    public String getAuthority() {
        return title;
    }
}
