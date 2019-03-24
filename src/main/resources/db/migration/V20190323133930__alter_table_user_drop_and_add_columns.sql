ALTER TABLE todo_list_db.`user` DROP COLUMN name;
ALTER TABLE todo_list_db.`user` DROP COLUMN surname;
ALTER TABLE todo_list_db.`user` DROP COLUMN middlename;
ALTER TABLE todo_list_db.`user` DROP COLUMN email;
ALTER TABLE todo_list_db.`user` DROP COLUMN avatar;
ALTER TABLE todo_list_db.`user` ADD account_non_expired BOOL NOT NULL;
ALTER TABLE todo_list_db.`user` ADD account_non_locked BOOL NOT NULL;
ALTER TABLE todo_list_db.`user` ADD credentials_non_expired BOOL NOT NULL;
