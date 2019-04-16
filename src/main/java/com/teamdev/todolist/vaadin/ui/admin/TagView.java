package com.teamdev.todolist.vaadin.ui.admin;

import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.Tag;
import com.teamdev.todolist.service.TagService;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.form.TagForm;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.teamdev.todolist.vaadin.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

import static com.teamdev.todolist.configuration.support.Constants.ADMIN_TAGS_PAGE;

/**
 * @author Alexandr Stegnin
 */

@PageTitle("ТЭГИ")
@Route(value = ADMIN_TAGS_PAGE, layout = MainLayout.class)
@HtmlImport("../VAADIN/grid-style.html")
@HtmlImport("../VAADIN/form-elements-style.html")
public class TagView extends CustomAppLayout {

    private final TagService tagService;
    private Grid<Tag> grid;
    private final Button addNewBtn;
    private ListDataProvider<Tag> dataProvider;
    private TagForm tagForm;

    public TagView(TagService tagService, UserService userService) {
        super(userService);
        this.tagService = tagService;
        this.grid = new Grid<>();
        this.dataProvider = new ListDataProvider<>(getAll());
        this.addNewBtn = VaadinViewUtils.createButton("Создать тэг", "add", "submit", "");
        init();
    }

    private void init() {
        addNewBtn.addClickListener(e -> showDialog(OperationEnum.CREATE, new Tag()));

        grid.setDataProvider(dataProvider);

        grid.addColumn(Tag::getTitle)
                .setHeader("Название тэга")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addComponentColumn(tag -> VaadinViewUtils.makeEditorColumnActions(
                e -> showDialog(OperationEnum.UPDATE, tag),
                e -> showDialog(OperationEnum.DELETE, tag)))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setEditorComponent(new Div())
                .setFlexGrow(2)
                .setHeader("Действия");

        VerticalLayout verticalLayout = new VerticalLayout();

        verticalLayout.add(addNewBtn, grid);
        verticalLayout.setAlignItems(FlexComponent.Alignment.END);
        setContent(verticalLayout);
    }

    private List<Tag> getAll() {
        return tagService.findAll();
    }

    private void showDialog(final OperationEnum operation, final Tag tag) {
        TagForm tagForm = new TagForm(tagService, operation, tag);
        this.tagForm = tagForm;
        tagForm.addOpenedChangeListener(e -> refreshDataProvider(e.isOpened(), operation, tag));
        tagForm.open();
    }

    private void refreshDataProvider(final boolean isOpened, final OperationEnum operation, final Tag tag) {
        if (!isOpened && !tagForm.isCanceled()) {
            if (operation.compareTo(OperationEnum.CREATE) == 0) dataProvider.getItems().add(tag);
            else if (operation.compareTo(OperationEnum.DELETE) == 0) dataProvider.getItems().remove(tag);
            else dataProvider.refreshItem(tag);
            dataProvider.refreshAll();
        }
    }

}
