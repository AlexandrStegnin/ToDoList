package com.teamdev.todolist.vaadin.form;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.command.tag.CreateTagCommand;
import com.teamdev.todolist.command.tag.DeleteTagCommand;
import com.teamdev.todolist.command.tag.UpdateTagCommand;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.Tag;
import com.teamdev.todolist.service.TagService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

/**
 * @author Alexandr Stegnin
 */

public class TagForm extends Dialog {

    private final TagService tagService;

    private final TextField title;
    private final Button cancel;
    private final Tag tag;
    private final OperationEnum operation;
    private final HorizontalLayout buttons;
    private final Binder<Tag> tagBinder;
    private final VerticalLayout content;

    private boolean canceled = false;

    private Button submit;

    public TagForm(TagService tagService, OperationEnum operation, Tag tag) {
        this.tagBinder = new BeanValidationBinder<>(Tag.class);
        this.title = new TextField("НАЗВАНИЕ");
        this.cancel = new Button("ОТМЕНИТЬ", e -> {
            this.canceled = true;
            this.close();
        });
        this.submit = new Button(operation.name.toUpperCase());
        this.content = new VerticalLayout();
        this.buttons = new HorizontalLayout();
        this.tagService = tagService;
        this.operation = operation;
        this.tag = tag;
        init();
    }

    private void init() {
        stylizeForm();
        prepareSubmitButton(operation);
        buttons.add(submit, cancel);
        add(title, buttons);
        tagBinder.setBean(tag);
        tagBinder.bindInstanceFields(this);
    }

    private void prepareSubmitButton(OperationEnum operation) {
        switch (operation) {
            case CREATE:
                submit.addClickListener(e -> executeCommand(new CreateTagCommand(tagService, tag)));
                break;
            case UPDATE:
                submit.addClickListener(e -> executeCommand(new UpdateTagCommand(tagService, tag)));
                break;
            case DELETE:
                submit.addClickListener(e -> executeCommand(new DeleteTagCommand(tagService, tag)));
                break;
        }
    }

    private void executeCommand(Command command) {
        if (command instanceof DeleteTagCommand) {
            command.execute();
            this.close();
        } else if (tagBinder.writeBeanIfValid(tag)) {
            command.execute();
            this.close();
        }
    }

    public boolean isCanceled() {
        return canceled;
    }

    private void stylizeForm() {
        setWidth("200px");
        setHeight("100px");

        title.setWidthFull();

        submit.addClassNames("btn", "bg-green", "waves-effect");
        submit.getStyle().set("padding", "8px 10px 25px");

        cancel.addClassNames("btn", "bg-red", "waves-effect");
        cancel.getStyle().set("padding", "8px 10px 25px");

        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);

        content.setHeightFull();
    }

}
