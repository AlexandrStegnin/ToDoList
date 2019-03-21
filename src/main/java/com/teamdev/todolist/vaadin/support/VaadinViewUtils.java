package com.teamdev.todolist.vaadin.support;

import com.teamdev.todolist.entities.Role;
import com.teamdev.todolist.entities.User;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;

@Component
public class VaadinViewUtils {

//    private static String fileUploadDirectory;
//
//    @Value("${spring.config.file-upload-directory}")
//    public void setFileUploadDirectory(String value) {
//        fileUploadDirectory = value;
//    }


    public static Div makeEditorColumnActions(ComponentEventListener<ClickEvent<Button>> editListener,
                                              ComponentEventListener<ClickEvent<Button>> deleteListener) {
        Div actions = new Div();
        Button edit = new Button("", VaadinIcon.EDIT.create());
        edit.addClickListener(editListener);
        Button delete = new Button("", VaadinIcon.TRASH.create());
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
            Checkbox checkbox = new Checkbox(
                    role.getTitle(),
                    user.getRoles().contains(role));
            checkbox.addValueChangeListener(e -> {
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

    private static StreamResource createFileResource(File file) {
        StreamResource sr = new StreamResource("", (InputStreamFactory) () -> {
            try {
                if (!Files.exists(file.toPath())) {
                    return new FileInputStream(getDefaultImage());
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

    private static File getDefaultImage() {
        try {
            return ResourceUtils.getFile("classpath:static/images/users-png.png");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Изображение по умолчанию не найдено!", e);
        }
    }

//    public static Image getProductImage(Product product, boolean defaultStyle) {
//        String src = product.getImages().isEmpty() ? DEFAULT_SRC :
//                (fileUploadDirectory + product.getVendorCode() +
//                PATH_SEPARATOR + product.getImages().get(0).getPath());
//
//        File file = new File(src);
//        StreamResource streamResource = createFileResource(file);
//        Image image = new Image(streamResource, product.getTitle());
//        image.setHeight("150px");
//        image.setWidth("150px");
//        if (defaultStyle) {
//            image.getStyle().set("position", "relative");
//            image.getStyle().set("left", "25%");
//            image.getStyle().set("margin", "0");
//        }
//        return image;
//    }
//
//    public static Details createDetails(Order order, @Nullable Anchor link) {
//        Details details = new Details();
//        details.getElement().getStyle().set("margin", "1em");
//        Div div = new Div();
//        order.getOrderItems().forEach(orderItem -> {
//            Image image = VaadinViewUtils.getProductImage(orderItem.getProduct(), false);
//            image.getStyle().set("float", "left");
//            image.getStyle().set("margin-right", "1em");
//            div.add(image);
//            Span span = new Span(orderItem.getProduct().getShortDescription());
//            div.add(span);
//            div.getStyle().set("display","flex");
//            div.getStyle().set("align-items", "center");
//            details.setSummaryText(NumberFormat.getCurrencyInstance(LOCALE_RU).format(order.getPrice()) + " - " + order.getStatus().getTitle());
//            details.setContent(div);
//            if (link != null) {
//                link.getStyle()
//                        .set("margin", "1em 0em 1em 1em")
//                        .set("float", "right");
//                details.addContent(link);
//            }
//        });
//        return details;
//    }

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
