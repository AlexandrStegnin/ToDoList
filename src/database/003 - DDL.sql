-- Изменения в структуре таблиц user и task
-- @id: 003
-- @author: llebidko
-- @date: 11.03.2019

ALTER TABLE `todo_list_db`.`user`
  ADD COLUMN `password` VARCHAR(100) NOT NULL COMMENT 'Пароль' AFTER `login`,
  ADD UNIQUE INDEX `login_UNIQUE` (`login` ASC);

ALTER TABLE `todo_list_db`.`task`
  ADD COLUMN `parent_task_id` INT NULL COMMENT 'Ссылка на родительскую задачу (если она есть)' AFTER `status_id`,
  ADD INDEX `task_to_task_idx` (`parent_task_id` ASC);

ALTER TABLE `todo_list_db`.`task`
  ADD CONSTRAINT `task_to_task`
    FOREIGN KEY (`parent_task_id`)
      REFERENCES `todo_list_db`.`task` (`id`)
      ON DELETE RESTRICT
      ON UPDATE CASCADE;