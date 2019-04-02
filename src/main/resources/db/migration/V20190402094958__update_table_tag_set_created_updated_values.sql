UPDATE todo_list_db.tag
SET created = CURRENT_TIMESTAMP,
    updated = CURRENT_TIMESTAMP
WHERE 1 = 1;