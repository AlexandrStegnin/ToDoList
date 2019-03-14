package com.teamdev.todolist.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractEntity {

    @Id
    @TableGenerator(name = "userSeqStore", table = "SEQ_STORE",
            pkColumnName = "SEQ_NAME", pkColumnValue = "USER.ID.PK",
            valueColumnName = "SEQ_VALUE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "userSeqStore")
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The database generated user ID")
    private Long id;

    @Column
    @NotNull
    @ApiModelProperty(notes = "User name")
    private String name;

    @Column
    @NotNull
    @ApiModelProperty(notes = "User surname")
    private String surname;

    @Column
    @ApiModelProperty(notes = "User middlename")
    private String middlename;

    @Column
    @Size(min = 3, max = 45, message = "Login must be greater than 3 and less than 45 characters")
    @ApiModelProperty(notes = "User login")
    private String login;

    @Column
    @Size(min = 3, message = "Password must be greater than 3 characters")
    @ApiModelProperty(notes = "User password")
    private String password;

    @Email
    @Column
    @ApiModelProperty(notes = "User email")
    private String email;

    @Column
    @ApiModelProperty(notes = "The image path user avatar")
    private String avatar;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "user_role_to_user"),
            inverseForeignKey = @ForeignKey(name = "user_role_to_role")
    )
    @ApiModelProperty(notes = "Collection of user roles")
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
