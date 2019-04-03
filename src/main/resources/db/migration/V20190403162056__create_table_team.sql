CREATE TABLE todo_list_db.team
(
    id    INT         NOT NULL COMMENT 'Уникальный идентификатор',
    title varchar(20) NOT NULL COMMENT 'Название команды',
    CONSTRAINT team_pk PRIMARY KEY (id)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8
    COLLATE = utf8_general_ci
    COMMENT ='Команды рабочих областей';