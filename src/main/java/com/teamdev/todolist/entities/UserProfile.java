package com.teamdev.todolist.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * @author Alexandr Stegnin
 */


@Data
@Entity
@ToString(of = {"id", "email"})
@Table(name = "user_profile")
@EqualsAndHashCode(callSuper = true, exclude = "user")
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

    @Email
    @Column
    @ApiModelProperty(notes = "User email")
    private String email;

    @Column
    @ApiModelProperty(notes = "The image path user avatar")
    private String avatar;

}
