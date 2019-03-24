update todo_list_db.user
set account_non_expired = 1,
    account_non_locked = 1,
    credentials_non_expired = 1
where 1 = 1