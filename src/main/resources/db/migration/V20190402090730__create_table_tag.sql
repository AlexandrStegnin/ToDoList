CREATE TABLE todo_list_db.tag
(
    id    INT         NOT NULL COMMENT 'Уникальный идентификатор',
    title varchar(30) NOT NULL COMMENT 'Название ',
    CONSTRAINT tag_pk PRIMARY KEY (id)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8
    COLLATE = utf8_general_ci;