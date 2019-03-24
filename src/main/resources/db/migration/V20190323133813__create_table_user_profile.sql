CREATE TABLE `todo_list_db`.`user_profile` (
                              `id` int(11) NOT NULL COMMENT 'Уникальный идентификатор',
                              `name` varchar(45) NOT NULL COMMENT 'Имя',
                              `surname` varchar(45) NOT NULL COMMENT 'Фамилия',
                              `middlename` varchar(45) DEFAULT NULL COMMENT 'Отчество',
                              `email` varchar(45) NOT NULL COMMENT 'Email',
                              `avatar` varchar(45) DEFAULT NULL COMMENT 'Ссылка на аватар пользователя',
                              `created` datetime DEFAULT NULL COMMENT 'Дата и время создания',
                              `updated` datetime DEFAULT NULL COMMENT 'Дата и время обновления'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4