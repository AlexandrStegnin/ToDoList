package com.teamdev.todolist.vaadin.ui.admin;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.command.role.CreateRoleCommand;
import com.teamdev.todolist.command.role.DeleteRoleCommand;
import com.teamdev.todolist.command.role.UpdateRoleCommand;
import com.teamdev.todolist.entity.Role;
import com.teamdev.todolist.service.RoleService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.form.RoleForm;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.List;

import static com.teamdev.todolist.configuration.support.Constants.ADMIN_ROLES_PAGE;

@PageTitle("Roles")
@Route(ADMIN_ROLES_PAGE)
@Theme(value = Material.class, variant = Material.LIGHT)
public class RoleView extends CustomAppLayout {

    private final RoleService roleService;
    private Grid<Role> grid;
    private final Button addNewBtn;
    private ListDataProvider<Role> dataProvider;
    private Binder<Role> binder;
    private Role role;

    public RoleView(RoleService roleService) {
        this.roleService = roleService;
        this.grid = new Grid<>();
        this.dataProvider = new ListDataProvider<>(getAll());
        this.addNewBtn = new Button("New role", VaadinIcon.PLUS.create(),
                e -> showDialog(new CreateRoleCommand(roleService, new Role()), new Role()));
        this.binder = new BeanValidationBinder<>(Role.class);
        init();
    }

    private void init() {
        addNewBtn.setIconAfterText(true);

        grid.setDataProvider(dataProvider);

        grid.addColumn(Role::getTitle)
                .setHeader("Role name")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(Role::getDescription)
                .setHeader("Description")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addComponentColumn(role -> VaadinViewUtils.makeEditorColumnActions(
                e -> showDialog(new UpdateRoleCommand(roleService, role), role),
                e -> showDialog(new DeleteRoleCommand(roleService, role), role)))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setEditorComponent(new Div())
                .setFlexGrow(2)
                .setHeader("Actions");

        VerticalLayout verticalLayout = new VerticalLayout();

        verticalLayout.add(addNewBtn, grid);
        verticalLayout.setAlignItems(FlexComponent.Alignment.END);
        setContent(verticalLayout);
    }

    private List<Role> getAll() {
        return roleService.findAll();
    }

    private void showDialog(Command command, Role role) {
//        FormLayout roleForm = new FormLayout();
//        TextField nameField = new TextField("Role name");
//        nameField.setValue(role.getTitle() == null ? "" : role.getTitle());
//        binder.forField(nameField)
//                .bind(Role_.TITLE);
//
//        TextField humanized = new TextField("Description");
//        humanized.setValue(role.getDescription() == null ? "" : role.getDescription());
//        binder.forField(humanized)
//                .bind(Role_.DESCRIPTION);
//
//        roleForm.add(nameField, humanized);
//
        Dialog dialog = VaadinViewUtils.initDialog();
//        Button save = new Button("Save");
//        Button cancel = new Button("Cancel", e -> dialog.close());
//
//        HorizontalLayout actions = new HorizontalLayout();
//        actions.add(save, cancel);
//
//        VerticalLayout content = new VerticalLayout();
//
//        switch (operation) {
//            case UPDATE:
//                content.add(roleForm, actions);
//                save.addClickListener(e -> {
//                    if (binder.writeBeanIfValid(role)) {
//                        saveRole(role);
//                        dialog.close();
//                    }
//                });
//                break;
//            case CREATE:
//                content.add(roleForm, actions);
//                save.addClickListener(e -> {
//                    if (binder.writeBeanIfValid(role)) {
//                        dataProvider.getItems().add(role);
//                        saveRole(role);
//                        dialog.close();
//                    }
//                });
//                break;
//            case DELETE:
//                Div contentText = new Div();
//                contentText.setText("Confirm delete role: " + role.getTitle() + "?");
//                content.add(contentText, actions);
//                save.setText("Yes");
//                save.addClickListener(e -> {
//                    deleteRole(role);
//                    dialog.close();
//                });
//                break;
//        }
//
//        dialog.add(content);
        dialog.add(new RoleForm(command, dialog, role));
        dialog.open();
//        nameField.getElement().callFunction("focus");
    }

    private void saveRole(Role role) {
        roleService.create(role);
        dataProvider.refreshAll();
    }

    private void deleteRole(Role role) {
        dataProvider.getItems().remove(role);
        roleService.delete(role.getId());
        dataProvider.refreshAll();
    }

}
