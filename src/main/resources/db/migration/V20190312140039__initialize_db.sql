-- Начальный (базовый) скрипт создания структуры БД
-- @id: 001
-- @author: llebidko
-- @date: 10.03.2019
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `todo_list_db`.`user`;

CREATE TABLE `todo_list_db`.`user`
(
  `id`         INT         NOT NULL COMMENT 'Уникальный идентификатор',
  `name`       VARCHAR(45) NOT NULL COMMENT 'Имя',
  `surname`    VARCHAR(45) NOT NULL COMMENT 'Фамилия',
  `middlename` VARCHAR(45) NULL COMMENT 'Отчество',
  `login`      VARCHAR(45) NOT NULL COMMENT 'Логин',
  `email`      VARCHAR(45) NOT NULL COMMENT 'Электронный адрес',
  `avatar`     VARCHAR(45) NULL COMMENT 'Ссылка на аватар пользователя',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Пользователи';

DROP TABLE IF EXISTS `todo_list_db`.`role`;

CREATE TABLE `todo_list_db`.`role`
(
  `id`          INT          NOT NULL COMMENT 'Уникальный идентификатор',
  `title`       VARCHAR(45)  NOT NULL COMMENT 'Краткое название (человекочитаемый код-название) роли',
  `description` VARCHAR(180) NULL COMMENT 'Описание роли',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC)
)
  COMMENT = 'Возможные роли пользователей в системе';

DROP TABLE IF EXISTS `todo_list_db`.`user_role`;

CREATE TABLE `todo_list_db`.`user_role`
(
  `id`      INT NOT NULL COMMENT 'Уникальный идентификатор',
  `user_id` INT NOT NULL COMMENT 'Ссылка на user.id',
  `role_id` INT NOT NULL COMMENT 'Ссылка на role.id',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `user_id_idx` (`user_id` ASC),
  INDEX `user_role_to_role_idx` (`role_id` ASC),
  CONSTRAINT `user_role_to_user`
    FOREIGN KEY (`user_id`)
      REFERENCES `todo_list_db`.`user` (`id`)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  CONSTRAINT `user_role_to_role`
    FOREIGN KEY (`role_id`)
      REFERENCES `todo_list_db`.`role` (`id`)
      ON DELETE CASCADE
      ON UPDATE CASCADE
)
  COMMENT = 'Назначенные роли пользователям системы';

DROP TABLE IF EXISTS `todo_list_db`.`task_status`;

CREATE TABLE `todo_list_db`.`task_status`
(
  `id`          INT          NOT NULL COMMENT 'Уникальный идентификатор',
  `title`       VARCHAR(45)  NOT NULL COMMENT 'Краткое название статуса',
  `description` VARCHAR(180) NULL COMMENT 'Описание статуса',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC)
)
  COMMENT = 'Доступные статусы для задач';

DROP TABLE IF EXISTS `todo_list_db`.`task`;

CREATE TABLE `todo_list_db`.`task`
(
  `id`             INT          NOT NULL COMMENT 'Уникальный идентификатор',
  `title`          VARCHAR(45)  NOT NULL COMMENT 'Название задачи',
  `description`    VARCHAR(180) NULL COMMENT 'Описание (постановка) задачи',
  `author_id`      INT          NOT NULL COMMENT 'Ссылка на пользователя (автора задачи)',
  `creation_date`  DATE         NOT NULL COMMENT 'Дата создания задачи',
  `execution_date` DATE         NULL COMMENT 'Дата исполнения (может быть null - задача без даты исполнения)',
  `status_id`      INT          NOT NULL COMMENT 'id текущего статуса задачи',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `task_to_user_idx` (`author_id` ASC),
  INDEX `task_to_task_status_idx` (`status_id` ASC),
  CONSTRAINT `task_to_user`
    FOREIGN KEY (`author_id`)
      REFERENCES `todo_list_db`.`user` (`id`)
      ON DELETE RESTRICT
      ON UPDATE CASCADE,
  CONSTRAINT `task_to_task_status`
    FOREIGN KEY (`status_id`)
      REFERENCES `todo_list_db`.`task_status` (`id`)
      ON DELETE RESTRICT
      ON UPDATE CASCADE
)
  COMMENT = 'Задачи';

DROP TABLE IF EXISTS `todo_list_db`.`perfomer`;

CREATE TABLE `todo_list_db`.`perfomer`
(
  `id`          INT NOT NULL COMMENT 'Уникальный идентификатор',
  `task_id`     INT NOT NULL COMMENT 'Ссылка на задачу',
  `perfomer_id` INT NULL COMMENT 'Ссылка на пользователя-исполнителя',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `perfomer_to_task_idx` (`task_id` ASC),
  INDEX `perfomer_to_user_idx` (`perfomer_id` ASC),
  CONSTRAINT `perfomer_to_task`
    FOREIGN KEY (`task_id`)
      REFERENCES `todo_list_db`.`task` (`id`)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  CONSTRAINT `perfomer_to_user`
    FOREIGN KEY (`perfomer_id`)
      REFERENCES `todo_list_db`.`user` (`id`)
      ON DELETE RESTRICT
      ON UPDATE CASCADE
)
  COMMENT = 'Исполнители задач';

DROP TABLE IF EXISTS `todo_list_db`.`task_attachment`;

CREATE TABLE `todo_list_db`.`task_attachment`
(
  `id`      INT         NOT NULL COMMENT 'Уникальный идентификатор',
  `task_id` INT         NOT NULL COMMENT 'Ссылка на задачу',
  `user_id` INT         NOT NULL COMMENT 'Ссылка на пользователя который создал вложение',
  `url`     VARCHAR(45) NOT NULL COMMENT 'URL вложения',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `task_attachment_to_task_idx` (`task_id` ASC),
  INDEX `task_attachment_to_user_idx` (`user_id` ASC),
  CONSTRAINT `task_attachment_to_task`
    FOREIGN KEY (`task_id`)
      REFERENCES `todo_list_db`.`task` (`id`)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  CONSTRAINT `task_attachment_to_user`
    FOREIGN KEY (`user_id`)
      REFERENCES `todo_list_db`.`user` (`id`)
      ON DELETE NO ACTION
      ON UPDATE CASCADE
)
  COMMENT = 'Вложения к задаче';

DROP TABLE IF EXISTS `todo_list_db`.`action_type`;

CREATE TABLE `todo_list_db`.`action_type`
(
  `id`          INT          NOT NULL COMMENT 'Уникальный идентификатор',
  `title`       VARCHAR(45)  NOT NULL COMMENT 'Название действия',
  `description` VARCHAR(180) NULL COMMENT 'Описание действия',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC)
)
  COMMENT = 'Типы действий по задаче';

DROP TABLE IF EXISTS `todo_list_db`.`task_action`;

CREATE TABLE `todo_list_db`.`task_action`
(
  `id`          INT          NOT NULL COMMENT 'Уникальный идентификатор',
  `task_id`     INT          NOT NULL COMMENT 'Ссылка на задачу',
  `user_id`     INT          NOT NULL COMMENT 'Ссылка на пользователя который произвел изменения',
  `date`        DATE         NOT NULL COMMENT 'Дата изменений',
  `action_id`   INT          NOT NULL COMMENT 'Ссылка на тип изменения',
  `description` VARCHAR(180) NULL COMMENT 'Описание произведенных изменений',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `task_action_to_task_idx` (`task_id` ASC),
  INDEX `task_action_to_user_idx` (`user_id` ASC),
  INDEX `task_action_to_action_type_idx` (`action_id` ASC),
  CONSTRAINT `task_action_to_task`
    FOREIGN KEY (`task_id`)
      REFERENCES `todo_list_db`.`task` (`id`)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  CONSTRAINT `task_action_to_user`
    FOREIGN KEY (`user_id`)
      REFERENCES `todo_list_db`.`user` (`id`)
      ON DELETE NO ACTION
      ON UPDATE CASCADE,
  CONSTRAINT `task_action_to_action_type`
    FOREIGN KEY (`action_id`)
      REFERENCES `todo_list_db`.`action_type` (`id`)
      ON DELETE RESTRICT
      ON UPDATE CASCADE
)
  COMMENT = 'Исполнение задачи';

SET FOREIGN_KEY_CHECKS = 1;