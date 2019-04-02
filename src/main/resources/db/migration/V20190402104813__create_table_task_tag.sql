CREATE TABLE `task_tag`
(
    `task_id` int(11) NOT NULL COMMENT 'Ссылка на задачу',
    `tag_id`  int(11) NOT NULL COMMENT 'Ссылка на тэг',
    KEY `task_tag_to_task_idx` (`task_id`),
    KEY `task_tag_to_tag_idx` (`tag_id`),
    CONSTRAINT `task_tag_to_task` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `task_tag_to_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='Тэги задач'