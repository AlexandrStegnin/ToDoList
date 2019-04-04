SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE todo_list_db.task ADD workspace_id INT NOT NULL;
ALTER TABLE todo_list_db.task ADD CONSTRAINT task_work_space_fk FOREIGN KEY (workspace_id) REFERENCES todo_list_db.work_space(id) ON DELETE CASCADE ON UPDATE CASCADE;
