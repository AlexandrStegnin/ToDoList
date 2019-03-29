package com.teamdev.todolist.configuration.support;

/**
 * @author stegnin
 */
public enum OperationEnum {

    CREATE("Создать"),
    UPDATE("Обновить"),
    DELETE("Удалить");

    public String name;

    OperationEnum(String name) {
        this.name = name;
    }

}
