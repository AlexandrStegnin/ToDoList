ALTER TABLE `todo_list_db`.`performer` DROP FOREIGN KEY perfomer_to_task;
ALTER TABLE `todo_list_db`.`performer` DROP FOREIGN KEY perfomer_to_user;
ALTER TABLE `todo_list_db`.`performer` ADD CONSTRAINT `performer_to_task`
  FOREIGN KEY (`task_id`)
    REFERENCES `todo_list_db`.`task` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;
ALTER TABLE `todo_list_db`.`performer` ADD CONSTRAINT `performer_to_user`
  FOREIGN KEY (`perfomer_id`)
  REFERENCES `todo_list_db`.`user` (`id`)
  ON DELETE RESTRICT
     ON UPDATE CASCADE;