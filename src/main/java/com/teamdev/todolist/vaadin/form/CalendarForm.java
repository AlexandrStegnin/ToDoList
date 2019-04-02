package com.teamdev.todolist.vaadin.form;

import com.teamdev.todolist.entity.*;
import com.teamdev.todolist.service.TaskService;
import com.teamdev.todolist.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * @author Leonid Lebidko
 */

public class CalendarForm extends Dialog {
    private final UserService userService;
    private final TaskService taskService;
    private final User currentUser;

    private final Button exitBtn;

    public CalendarForm(UserService userService, TaskService taskService, User currentUser) {
        this.userService = userService;
        this.taskService = taskService;
        this.currentUser = currentUser;
        this.exitBtn = new Button("Закрыть", e -> this.close());
        init();
    }

    private void init() {
        setWidth("600px");
        setWidth("300px");
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.add(new Span("Здесь когда-то будет календарь назначенных на пользователя задач"));
        mainLayout.add(exitBtn);
        add(mainLayout);
    }
}
