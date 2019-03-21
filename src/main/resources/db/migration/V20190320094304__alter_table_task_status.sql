ALTER TABLE `todo_list_db`.`task_status` ADD COLUMN created DATETIME NULL;
ALTER TABLE `todo_list_db`.`task_status` ADD COLUMN updated DATETIME NULL;

UPDATE `todo_list_db`.`task_status`
SET task_status.created = CURRENT_TIMESTAMP,
    task_status.updated = CURRENT_TIMESTAMP
WHERE 1 = 1;