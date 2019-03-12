UPDATE `todo_list_db`.`user` SET `password` = '$2a$10$rFL0cavcGHC.7UZtclFtheeDAq4jKYJNKvzNq2dYcSpTohDSFrv/C' WHERE 1 = 1;

UPDATE `todo_list_db`.`user`
SET user.created = CURRENT_TIMESTAMP,
    user.updated = CURRENT_TIMESTAMP
WHERE 1 = 1;

UPDATE `todo_list_db`.`role`
SET role.created = CURRENT_TIMESTAMP,
    role.updated = CURRENT_TIMESTAMP
WHERE 1 = 1;