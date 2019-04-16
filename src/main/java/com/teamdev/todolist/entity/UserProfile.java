package com.teamdev.todolist.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */


@Data
@Entity
@ToString(of = {"id", "email"})
@Table(name = "user_profile")
@EqualsAndHashCode(callSuper = false, exclude = "user")
public class UserProfile extends AbstractEntity {

    @Id
    @ApiModelProperty(notes = "The database generated user profile ID")
    private Long id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "id")
    @MapsId
    private User user;

    @Column
    @NotNull(message = "Необходимо указать имя")
    @Size(min = 1, max = 100, message = "Имя должно быть от 1 до 100 символов")
    @ApiModelProperty(notes = "User name")
    private String name;

    @Column
    @NotNull(message = "Необходимо указать фамилию")
    @Size(min = 1, max = 100, message = "Фамилия должна быть от 1 до 100 символов")
    @ApiModelProperty(notes = "User surname")
    private String surname;

    @Column
    @ApiModelProperty(notes = "User middlename")
    private String middlename;

    @Email
    @Column
    @NotBlank(message = "Необходимо указать email")
    @NotNull(message = "Необходимо указать email")
    @Size(min = 6, message = "Необходимо указать корректный email")
    @ApiModelProperty(notes = "User email")
    private String email;

    @Column
    @ApiModelProperty(notes = "The image path user avatar")
    private String avatar;

    @OneToMany(mappedBy = "owner")
    @ApiModelProperty(notes = "User work spaces")
    private Set<Workspace> workspaces = new HashSet<>();

    @ManyToMany(mappedBy = "members")
    @ApiModelProperty(notes = "User teams")
    private Set<Team> teams;

}
