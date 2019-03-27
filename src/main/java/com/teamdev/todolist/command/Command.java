package com.teamdev.todolist.command;

public interface Command {

    void execute();

    String getCommandName();

}
