package com.teamdev.todolist.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "user")
@EqualsAndHashCode(of = {"id", "login"})
public class User implements UserDetails {

    @Id
    @TableGenerator(name = "userSeqStore", table = "SEQ_STORE",
            pkColumnName = "SEQ_NAME", pkColumnValue = "USER.ID.PK",
            valueColumnName = "SEQ_VALUE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "userSeqStore")
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The database generated user ID")
    private Long id;

    @Column
    @Size(min = 3, max = 45, message = "Login must be greater than 3 and less than 45 characters")
    @ApiModelProperty(notes = "User login")
    private String login;

    @JsonIgnore
    @Column(name = "password_hash", length = 60)
    @ApiModelProperty(notes = "User password hash")
    private String passwordHash;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "user_role_to_user"),
            inverseForeignKey = @ForeignKey(name = "user_role_to_role")
    )
    @ApiModelProperty(notes = "Collection of user roles")
    private Set<Role> roles;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;

    @Column
    private boolean accountNonExpired = true;

    @Column
    private boolean accountNonLocked = true;

    @Column
    private boolean credentialsNonExpired = true;

    //необходимо при создании нового пользователя, что бы задать пароль 
    private transient String password;

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonSetter
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isAccountNonExpired() &&
                isAccountNonLocked() &&
                isCredentialsNonExpired();
    }

    public User() {
        this.profile = new UserProfile();
        this.profile.setUser(this);
    }
}
