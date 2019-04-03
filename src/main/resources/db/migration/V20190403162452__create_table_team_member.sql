CREATE TABLE todo_list_db.team_member
(
    team_id   INT NOT NULL COMMENT 'ID команды',
    member_id INT NOT NULL COMMENT 'ID участника',
    CONSTRAINT team_member_team_fk FOREIGN KEY (team_id) REFERENCES todo_list_db.team (id),
    CONSTRAINT team_member_user_fk FOREIGN KEY (member_id) REFERENCES todo_list_db.`user` (id)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8
    COLLATE = utf8_general_ci
    COMMENT ='Таблица для связи команд и пользователей-участников';