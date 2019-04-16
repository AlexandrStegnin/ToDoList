package com.teamdev.todolist.vaadin.form;

import com.teamdev.todolist.entity.*;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.stefan.fullcalendar.BusinessHours;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;

import java.time.LocalTime;
import java.util.List;
import java.util.Random;

/**
 * @author Leonid Lebidko
 */

public class CalendarForm extends Dialog {

    private final Button exitBtn;
    private List<Task> tasks;

    public CalendarForm(List<Task> tasks) {
        this.tasks = tasks;
        this.exitBtn = new Button("Закрыть", e -> this.close());
        init();
    }

    private void init() {
        setWidth("600px");
        setHeight("700px");
        VerticalLayout mainLayout = new VerticalLayout();

        FullCalendar calendar = FullCalendarBuilder.create().build();
        mainLayout.add(calendar);
        mainLayout.setFlexGrow(1, calendar);

        tasks.forEach(task -> {
            Entry entry = new Entry();
            entry.setTitle(task.getTitle());
            entry.setStart(task.getCreationDate());
            entry.setEnd(task.getExecutionDate());
            entry.setAllDay(true);
            entry.setColor(generateColor());
            calendar.addEntries(entry);
        });

        calendar.setBusinessHours(new BusinessHours(LocalTime.of(0, 0), LocalTime.of(23, 59), BusinessHours.DEFAULT_BUSINESS_WEEK));

        calendar.setHeight(620);

        mainLayout.add(exitBtn);
        add(mainLayout);
    }

    private String generateColor() {
        int newColor = new Random().nextInt(0x1000000);
        return String.format("#%06X", newColor);
    }
}
