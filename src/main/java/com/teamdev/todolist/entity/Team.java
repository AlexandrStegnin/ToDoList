package com.teamdev.todolist.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@NoArgsConstructor
@Table(name = "team")
@EqualsAndHashCode(callSuper = false, of = "id")
public class Team extends AbstractEntity {

    @Id
    @TableGenerator(name = "teamSeqStore", table = "SEQ_STORE",
            pkColumnName = "SEQ_NAME", pkColumnValue = "TEAM.ID.PK",
            valueColumnName = "SEQ_VALUE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "teamSeqStore")
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The database generated team ID")
    private Long id;

    @Column
    @Size(min = 3, max = 20, message = "Название команды должно быть от 3 до 20 символов")
    @ApiModelProperty(notes = "Team title")
    private String title;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "team_member",
            joinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "member_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "team_member_team_fk"),
            inverseForeignKey = @ForeignKey(name = "team_member_user_fk")
    )
    @ApiModelProperty(notes = "Collection of team members")
    private Set<User> members;

}
