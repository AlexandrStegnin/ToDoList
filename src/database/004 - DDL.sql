ALTER TABLE `todo_list_db`.`user` ADD COLUMN created DATETIME NULL;
ALTER TABLE `todo_list_db`.`user` ADD COLUMN updated DATETIME NULL;

ALTER TABLE `todo_list_db`.`role` ADD COLUMN created DATETIME NULL;
ALTER TABLE `todo_list_db`.`role` ADD COLUMN updated DATETIME NULL;