package com.teamdev.todolist.vaadin.form;

import com.teamdev.todolist.entity.*;

import com.vaadin.flow.component.Component;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import org.vaadin.addon.calendar.Calendar;
import org.vaadin.addon.calendar.handler.BasicDateClickHandler;
import org.vaadin.addon.calendar.item.BasicItem;
import org.vaadin.addon.calendar.item.BasicItemProvider;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Leonid Lebidko
 */

public class CalendarForm extends Dialog {

    class TaskItem extends BasicItem {

        private final Task task;

        public TaskItem(Task task) {
            super(task.getTitle(), task.getDescription(),
                    ZonedDateTime.of(task.getCreationDate(), ZoneId.of("Europe/Moscow")),
                    ZonedDateTime.of(task.getExecutionDate(), ZoneId.of("Europe/Moscow")));
            this.task = task;
        }

        public Task getTask() {
            return task;
        }
    }

    private BasicItemProvider<TaskItem> performerDataProvider;
    private Calendar<TaskItem> calendar;

    private final Button exitBtn;

    public CalendarForm(List<Task> currentPerformerTask) {
        this.performerDataProvider = new BasicItemProvider<>();
        currentPerformerTask.stream().map(TaskItem::new).forEach(this.performerDataProvider::addItem);
        this.exitBtn = new Button("Закрыть", e -> this.close());
        init();
    }

    private void init() {
        setWidth("600px");
        setHeight("300px");

        calendar = new Calendar<>(performerDataProvider);

        calendar.addStyleName("meetings");
        calendar.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        calendar.setHeight(100.0f, Sizeable.Unit.PERCENTAGE);
        calendar.setResponsive(true);

        calendar.setItemCaptionAsHtml(true);
        calendar.setContentMode(ContentMode.HTML);

//        calendar.setLocale(Locale.JAPAN);
//        calendar.setZoneId(ZoneId.of("America/Chicago"));
//        calendar.setWeeklyCaptionProvider(date ->  "<br>" + DateTimeFormatter.ofPattern("dd.MM.YYYY", getLocale()).format(date));
//        calendar.setWeeklyCaptionProvider(date -> DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale()).format(date));

        calendar.withVisibleDays(1, 7);
//        calendar.withMonth(ZonedDateTime.now().getMonth());
//        calendar.setStartDate(ZonedDateTime.of(2017, 9, 10, 0,0,0, 0, calendar.getZoneId()));
//        calendar.setEndDate(ZonedDateTime.of(2017, 9, 16, 0,0,0, 0, calendar.getZoneId()));
        addCalendarEventListeners();
        setupBlockedTimeSlots();

        VerticalLayout mainLayout = new VerticalLayout();
        // todo: проблема совместимости AbstractComponent (vaadin8) и Component(vaadin13). Переписать Calendar?
//        mainLayout.add((Component) calendar);
        mainLayout.add(exitBtn);
        add(mainLayout);
    }

    private void addCalendarEventListeners() {
        calendar.setHandler(new BasicDateClickHandler(true));
//        calendar.setHandler(this::onCalendarClick);
//        calendar.setHandler(this::onCalendarRangeSelect);
    }

    private void setupBlockedTimeSlots() {

//        java.util.Calendar cal = java.util.Calendar.getInstance();
//        cal.set(java.util.Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
//        cal.clear(java.util.Calendar.MINUTE);
//        cal.clear(java.util.Calendar.SECOND);
//        cal.clear(java.util.Calendar.MILLISECOND);
//
//        GregorianCalendar bcal = new GregorianCalendar(UI.getCurrent().getLocale());
//        bcal.clear();
//
//        long start = bcal.getTimeInMillis();
//
//        bcal.add(java.util.Calendar.HOUR, 7);
//        bcal.add(java.util.Calendar.MINUTE, 30);
//        long end = bcal.getTimeInMillis();
//
//        calendar.addTimeBlock(start, end, "my-blocky-style");
//
//        cal.add(java.util.Calendar.DAY_OF_WEEK, 1);
//
//        bcal.clear();
//        bcal.add(java.util.Calendar.HOUR, 14);
//        bcal.add(java.util.Calendar.MINUTE, 30);
//        start = bcal.getTimeInMillis();
//
//        bcal.add(java.util.Calendar.MINUTE, 60);
//        end = bcal.getTimeInMillis();
//
//        calendar.addTimeBlock(start, end);
    }
}
