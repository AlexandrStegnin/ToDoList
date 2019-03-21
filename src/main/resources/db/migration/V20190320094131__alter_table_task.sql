ALTER TABLE `todo_list_db`.`task` ADD COLUMN created DATETIME NULL;
ALTER TABLE `todo_list_db`.`task` ADD COLUMN updated DATETIME NULL;

UPDATE `todo_list_db`.`task`
SET task.created = CURRENT_TIMESTAMP,
    task.updated = CURRENT_TIMESTAMP
WHERE 1 = 1;