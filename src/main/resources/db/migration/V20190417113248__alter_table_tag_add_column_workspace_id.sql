SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE todo_list_db.tag ADD workspace_id int(11) NOT NULL;
ALTER TABLE todo_list_db.tag ADD CONSTRAINT tag_work_space_fk FOREIGN KEY (workspace_id) REFERENCES todo_list_db.work_space(id) ON DELETE CASCADE ON UPDATE CASCADE;