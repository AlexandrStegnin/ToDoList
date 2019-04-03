CREATE TABLE todo_list_db.work_space
(
    id       INT         NOT NULL COMMENT 'Уникальный идентификатор',
    title    varchar(20) NOT NULL COMMENT 'Название рабочего пространства',
    owner_id INT         NOT NULL COMMENT 'ID владельца рабочего пространства',
    team_id  INT         NULL COMMENT 'ID команды рабочего пространства, если есть',
    CONSTRAINT work_space_pk PRIMARY KEY (id),
    CONSTRAINT work_space_user_fk FOREIGN KEY (owner_id) REFERENCES todo_list_db.`user` (id) ON DELETE CASCADE ON UPDATE CASCADE
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8
    COLLATE = utf8_general_ci
    COMMENT ='Рабочие области пользователей';