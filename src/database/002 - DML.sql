-- Добавление начальных (тестовых) данных
-- @id: 002
-- @author: llebidko
-- @date: 10.03.2019

INSERT INTO `todo_list_db`.`role` (`id`, `title`, `description`) VALUES ('1', 'USER', 'Обычный пользователь системы');
INSERT INTO `todo_list_db`.`role` (`id`, `title`, `description`) VALUES ('2', 'ADMIN', 'Администратор системы');

INSERT INTO `todo_list_db`.`user` (`id`, `name`, `surname`, `middlename`, `login`, `email`) VALUES ('1', 'Иван', 'Иванов', 'Иванович', 'ivan', 'ivan@ivanov.ru');
INSERT INTO `todo_list_db`.`user` (`id`, `name`, `surname`, `login`, `email`) VALUES ('2', 'Петр', 'Петров', 'petr', 'petr@petrov.ru');
INSERT INTO `todo_list_db`.`user` (`id`, `name`, `surname`, `middlename`, `login`, `email`) VALUES ('3', 'Семен', 'Семенов', 'Семенович', 'semen', 'semen@semenov.ru');

INSERT INTO `todo_list_db`.`user_role` (`id`, `user_id`, `role_id`) VALUES ('1', '1', '1');
INSERT INTO `todo_list_db`.`user_role` (`id`, `user_id`, `role_id`) VALUES ('2', '1', '2');
INSERT INTO `todo_list_db`.`user_role` (`id`, `user_id`, `role_id`) VALUES ('3', '2', '1');
INSERT INTO `todo_list_db`.`user_role` (`id`, `user_id`, `role_id`) VALUES ('4', '3', '1');

INSERT INTO `todo_list_db`.`task_status` (`id`, `title`, `description`) VALUES ('1', 'Новая', 'Новая задача');
INSERT INTO `todo_list_db`.`task_status` (`id`, `title`, `description`) VALUES ('2', 'Исполняемая', 'Задача исполняется');
INSERT INTO `todo_list_db`.`task_status` (`id`, `title`, `description`) VALUES ('3', 'Отложенная', 'Задача отложена на неопределенное время');
INSERT INTO `todo_list_db`.`task_status` (`id`, `title`, `description`) VALUES ('4', 'Отмененная', 'Задача отменена');
INSERT INTO `todo_list_db`.`task_status` (`id`, `title`, `description`) VALUES ('5', 'Решенная', 'Задача решена');

INSERT INTO `todo_list_db`.`action_type` (`id`, `title`, `description`) VALUES ('1', 'Изменил статус', 'Пользователь изменил статус у задачи');
INSERT INTO `todo_list_db`.`action_type` (`id`, `title`, `description`) VALUES ('2', 'Оставил комментарий', 'Пользователь оставил комментарий у задачи');
INSERT INTO `todo_list_db`.`action_type` (`id`, `title`, `description`) VALUES ('3', 'Создал вложение', 'Пользователь создал новое вложение к задаче');

INSERT INTO `todo_list_db`.`task` (`id`, `title`, `description`, `author_id`, `creation_date`, `execution_date`, `status_id`) VALUES ('1', 'Первая задача', 'Тут подробная формулировка задания', '1', '10.03.2019', '31.03.2019', '1');

INSERT INTO `todo_list_db`.`perfomer` (`id`, `task_id`, `perfomer_id`) VALUES ('1', '1', '2');
INSERT INTO `todo_list_db`.`perfomer` (`id`, `task_id`, `perfomer_id`) VALUES ('2', '1', '3');

INSERT INTO `todo_list_db`.`task_action` (`id`, `task_id`, `user_id`, `date`, `action_id`, `description`) VALUES ('1', '1', '2', '10.03.2019', '1', 'Требуется подробное описание - что нужно сделать?');
