package com.teamdev.todolist.vaadin.ui.admin;

import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.Role;
import com.teamdev.todolist.service.RoleService;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.form.RoleForm;
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

import static com.teamdev.todolist.configuration.support.Constants.ADMIN_ROLES_PAGE;

@PageTitle("РОЛИ")
@Route(value = ADMIN_ROLES_PAGE, layout = MainLayout.class)
@HtmlImport("../VAADIN/grid-style.html")
@HtmlImport("../VAADIN/form-elements-style.html")
public class RoleView extends CustomAppLayout {

    private final RoleService roleService;
    private Grid<Role> grid;
    private final Button addNewBtn;
    private ListDataProvider<Role> dataProvider;
    private RoleForm roleForm;

    public RoleView(RoleService roleService, UserService userService) {
        super(userService);
        this.roleService = roleService;
        this.grid = new Grid<>();
        this.dataProvider = new ListDataProvider<>(getAll());
        this.addNewBtn = VaadinViewUtils.createButton("СОЗДАТЬ РОЛЬ", "add", "submit", "8px 13px 28px 7px");
        init();
    }

    private void init() {
        addNewBtn.addClickListener(e -> showRoleForm(OperationEnum.CREATE, new Role()));
        grid.setDataProvider(dataProvider);

        grid.addColumn(Role::getTitle)
                .setHeader("НАЗВАНИЕ")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(Role::getDescription)
                .setHeader("ОПИСАНИЕ")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addComponentColumn(role -> VaadinViewUtils.makeEditorColumnActions(
                e -> showRoleForm(OperationEnum.UPDATE, role),
                e -> showRoleForm(OperationEnum.DELETE, role)))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setEditorComponent(new Div())
                .setFlexGrow(2)
                .setHeader("ДЕЙСТВИЯ");

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(addNewBtn, grid);
        verticalLayout.setAlignItems(FlexComponent.Alignment.END);
        setContent(verticalLayout);
    }

    private List<Role> getAll() {
        return roleService.findAll();
    }

    private void showRoleForm(final OperationEnum operation, final Role role) {
        RoleForm roleForm = new RoleForm(operation, role, roleService);
        this.roleForm = roleForm;
        roleForm.addOpenedChangeListener(e -> refreshDataProvider(e.isOpened(), operation, role));
        roleForm.open();
    }

    private void refreshDataProvider(final boolean isOpened, final OperationEnum operation, final Role role) {
        if (!isOpened && !roleForm.isCanceled()) {
            if (operation.compareTo(OperationEnum.CREATE) == 0) dataProvider.getItems().add(role);
            else if (operation.compareTo(OperationEnum.DELETE) == 0) dataProvider.getItems().remove(role);
            dataProvider.refreshAll();
        }
    }

}
