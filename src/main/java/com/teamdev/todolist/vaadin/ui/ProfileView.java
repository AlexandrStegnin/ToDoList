package com.teamdev.todolist.vaadin.ui;

import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import static com.teamdev.todolist.configuration.support.Constants.PROFILE_PAGE;

/**
 * @author stegnin
 */

@Route(PROFILE_PAGE)
@PageTitle("Profile")
@Theme(value = Material.class)
public class ProfileView extends CustomAppLayout {

    public ProfileView() {
        init();
    }

    private void init() {
        User currentUser = SecurityUtils.getCurrentUser();
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Div content = new Div();
        content.getStyle()
                .set("display", "flex")
                .set("flex-flow", "row wrap")
                .set("justify-content", "center")
                .set("margin", "1em");
        if (currentUser != null) {
            content.add(currentUser.getLogin());
        }
        horizontalLayout.add(content);
        setContent(horizontalLayout);
    }

}
