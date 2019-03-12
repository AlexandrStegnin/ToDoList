package com.teamdev.todolist.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "user")
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractEntity {

    @Id
    @TableGenerator(name = "seqStore", table = "SEQ_STORE",
            pkColumnName = "SEQ_NAME", pkColumnValue = "USER.ID.PK",
            valueColumnName = "SEQ_VALUE", initialValue = 4, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seqStore")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column
    @NotNull
    private String name;

    @Column
    @NotNull
    private String surname;

    @Column
    private String middlename;

    @Column
    @Size(min = 3, max = 45, message = "Login must be greater than 3 and less than 45 characters")
    private String login;

    @Column
    @Size(min = 3, message = "Password must be greater than 3 characters")
    private String password;

    @Email
    @Column
    private String email;

    @Column
    private String avatar;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "user_role_to_user"),
            inverseForeignKey = @ForeignKey(name = "user_role_to_role")
    )
    private Set<Role> roles;

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

}
