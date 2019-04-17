package com.teamdev.todolist.vaadin.form;

import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.*;

import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.teamdev.todolist.vaadin.ui.WorkspaceView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.stefan.fullcalendar.BusinessHours;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;

/**
 * @author Leonid Lebidko
 */

public class CalendarForm extends Dialog {

    private final Button exitBtn;
    private final Button todayBtn;
    private final Button prevBtn;
    private final Button nextBtn;
    private FullCalendar calendar;
    private List<Task> tasks;
    private WorkspaceView view;

    public CalendarForm(List<Task> tasks, WorkspaceView view) {
        this.view = view;
        this.tasks = tasks;
        calendar = FullCalendarBuilder.create().build();
        calendar.setLocale(Locale.forLanguageTag("ru"));
        calendar.setFirstDay(DayOfWeek.MONDAY);
        calendar.setBusinessHours(new BusinessHours(LocalTime.of(0, 0), LocalTime.of(23, 59), BusinessHours.DEFAULT_BUSINESS_WEEK));
        todayBtn = VaadinViewUtils.createButton("ВОЗВРАТ", "restore", "regular", "8px 10px 20px 8px");
        todayBtn.addClickListener(e -> calendar.today());
        prevBtn = VaadinViewUtils.createButton("ПРЕДЫДУЩИЙ", "arrow_back", "regular", "8px 10px 20px 8px");
        prevBtn.addClickListener(e -> calendar.previous());
        nextBtn = VaadinViewUtils.createButton("СЛЕДУЮЩИЙ", "arrow_forward", "regular", "8px 10px 20px 8px");
        nextBtn.setIconAfterText(true);
        nextBtn.addClickListener(e -> calendar.next());
        exitBtn = VaadinViewUtils.createButton("ЗАКРЫТЬ", "", "cancel", "8px 10px 20px 8px");
        exitBtn.addClickListener(e -> this.close());
        init();
    }

    private void init() {
        setWidth("600px");
        setHeight("700px");
        Div spaceDiv = new Div();
        spaceDiv.setWidth("97px");
        HorizontalLayout btnPanel = new HorizontalLayout();
        btnPanel.add(todayBtn, prevBtn, nextBtn, spaceDiv, exitBtn);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.add(btnPanel);
        mainLayout.add(calendar);
        mainLayout.setFlexGrow(1, calendar);

        tasks.forEach(task -> {
            Entry entry = new Entry();
            entry.setTitle(task.getTitle());
            entry.setStart(task.getCreationDate());
            entry.setEnd(task.getExecutionDate().plusDays(1));
            entry.setAllDay(true);
            entry.setColor(generateColor());
            calendar.addEntries(entry);
        });

        calendar.setHeight(620);

        calendar.addEntryClickedListener(task -> {
            Optional<Task> clickedTask = tasks.stream().filter(e -> e.getTitle().equals(task.getEntry().getTitle()))
                    .filter(e -> e.getCreationDate().equals(task.getEntry().getStart()))
                    .filter(e -> e.getExecutionDate().plusDays(1).equals(task.getEntry().getEnd()))
                    .findFirst();
            if (clickedTask.isPresent()) view.showTaskForm(OperationEnum.UPDATE, clickedTask.get());
        });
        add(mainLayout);
    }

    private String generateColor() {
        int newColor = new Random().nextInt(0x1000000);
        return String.format("#%06X", newColor);
    }
}
