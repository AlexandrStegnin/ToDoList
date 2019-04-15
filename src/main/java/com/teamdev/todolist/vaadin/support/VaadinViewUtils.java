package com.teamdev.todolist.vaadin.support;

import com.teamdev.todolist.entity.Role;
import com.teamdev.todolist.entity.User;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.teamdev.todolist.configuration.support.Constants.DEFAULT_SRC;
import static com.teamdev.todolist.configuration.support.Constants.PATH_SEPARATOR;

@Component
public class VaadinViewUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(VaadinViewUtils.class);
    private static String FILE_UPLOAD_DIRECTORY;

    @Value("${spring.config.file-upload-directory}")
    public void setFileUploadDirectory(String value) {
        FILE_UPLOAD_DIRECTORY = value;
    }


    public static Div makeEditorColumnActions(ComponentEventListener<ClickEvent<Button>> editListener,
                                              ComponentEventListener<ClickEvent<Button>> deleteListener) {
        Div actions = new Div();
        Button edit = new Button();
        Html editIcon = new Html("<i class=\"material-icons\">mode_edit</i>");
        edit.addClassNames("btn", "btn-xs", "bg-blue");
        edit.getStyle().set("margin-right", "5px");
        edit.setIcon(editIcon);
        edit.addClickListener(editListener);
        Button delete = new Button();
        Html deleteIcon = new Html("<i class=\"material-icons\">delete</i>");
        delete.addClassNames("btn", "btn-xs", "bg-red");
        delete.getStyle().set("margin-left", "5px");
        delete.setIcon(deleteIcon);
        delete.addClickListener(deleteListener);
        actions.add(edit, delete);
        return actions;
    }

    public static Dialog initDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        return dialog;
    }

    public static Div makeUserRolesDiv(User user, List<Role> availableRoles) {
        Div checkBoxDiv = new Div();
        Div label = new Div();
        label.setText("Choose roles");
        label.getStyle().set("margin", "10px 0");
        checkBoxDiv.add(label);
        Div contentDiv = new Div();
        if (user.getRoles() == null) user.setRoles(new HashSet<>());
        availableRoles.forEach(role -> {
            // создаём checkbox'ы из доступных ролей и отмечаем те, которые есть у пользователя
            Checkbox checkbox = new Checkbox(
                    role.getTitle(),
                    user.getRoles().contains(role));
            checkbox.addValueChangeListener(e -> {

                // вешаем обработчик добавить/удалить роль у пользователя
                String roleName = e.getSource().getElement().getText();
                Role userRole = availableRoles.stream()
                        .filter(r -> r.getTitle().equalsIgnoreCase(roleName))
                        .findAny().orElse(null);
                if (e.getValue()) {
                    user.getRoles().add(userRole);
                } else {
                    user.getRoles().remove(userRole);
                }
            });
            contentDiv.add(checkbox);
        });
        checkBoxDiv.add(contentDiv);
        return checkBoxDiv;
    }

    // в vaadin такая особенность, можно указать картинки, которые лежат в определённых папках (VAADIN/STATIC/IMAGES)
    // точно не помню путь, но он задан довольно жёстко, или делать это как здесь динамически
    private static StreamResource createFileResource(File file) {
        StreamResource sr = new StreamResource("", (InputStreamFactory) () -> {
            try {
                if (!Files.exists(file.toPath())) {
                    return new FileInputStream(Objects.requireNonNull(getDefaultAvatar()));
                } else {
                    return new FileInputStream(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        sr.setCacheTime(0);
        return sr;
    }

    // если у пользователя нет аватара
    private static File getDefaultAvatar() {
        try {
            ClassPathResource classPathResource = new ClassPathResource("static/images/no-avatar2.png");
            File file = File.createTempFile("tmp", ".png");
            file.deleteOnExit();
            InputStream inputStream = classPathResource.getInputStream();
            FileUtils.copyInputStreamToFile(inputStream, file);
            return file;
        } catch (IOException e) {
            LOGGER.error("Изображение по умолчанию не найдено!", e);
            return null;
        }
    }

    public static Image getUserAvatar(final User user, final boolean isNavBarAvatar) {
        String src = user.getProfile().getAvatar() == null ? DEFAULT_SRC :
                (FILE_UPLOAD_DIRECTORY + user.getLogin() +
                PATH_SEPARATOR + user.getProfile().getAvatar());

        File file = new File(src);
        StreamResource streamResource = createFileResource(file);
        Image image = new Image(streamResource, user.getLogin());
        if (!isNavBarAvatar) {
            image.setHeight("150px");
            image.setWidth("150px");
        }
        return image;
    }

    // использовалось для вывода сообщений, если к примеру корзина пустая
    public static Div createInfoDiv(String message) {
        Div div = new Div();
        div.setSizeFull();
        Span span = new Span(message);
        div.add(span);
        div.getStyle()
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("font-size", "xx-large");
        return div;
    }

}
