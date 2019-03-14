package com.teamdev.todolist.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Alexandr Stegnin
 */

@Controller
@RequestMapping("/")
@Api(value = "MainController", description = "Redirect to swagger-ui when open default app path (\"/\")")
public class MainController {

    @GetMapping
    @ApiOperation(value = "Go to swagger ui page", response = String.class)
    public String swaggerUiPage() {
        return "redirect:/swagger-ui.html";
    }

    @GetMapping("/ui/users")
    @ApiOperation(value = "Go to user-list page for example", response = String.class)
    public String getUsersList() {
        return "user-list";
    }
}
