package com.teamdev.todolist.configuration.support;

/**
 * @author stegnin
 */
public enum OperationEnum {

    CREATE("Create"),
    UPDATE("Update"),
    DELETE("Delete");

    public String name;

    OperationEnum(String name) {
        this.name = name;
    }

}
