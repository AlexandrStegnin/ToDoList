CREATE TABLE todo_list_db.task_log
(
    id        int(11)       NOT NULL,
    task_id   int(11)       NOT NULL,
    user_id   int(11)       NOT NULL,
    operation varchar(6)    NOT NULL,
    comment   varchar(4000) NULL,
    CONSTRAINT task_log_pk PRIMARY KEY (id),
    CONSTRAINT task_log_task_fk FOREIGN KEY (task_id) REFERENCES todo_list_db.task (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT task_log_user_fk FOREIGN KEY (user_id) REFERENCES todo_list_db.`user` (id) ON DELETE CASCADE ON UPDATE CASCADE
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8
    COLLATE = utf8_general_ci;
