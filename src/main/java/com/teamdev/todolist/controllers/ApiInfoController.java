package com.teamdev.todolist.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.teamdev.todolist.configurations.support.Constants.API_INFO_URL;

/**
 * @author Alexandr Stegnin
 */

@Controller
@RequestMapping(API_INFO_URL)
@Api(value = "ApiInfoController", description = "Information page about this API")
public class ApiInfoController {

    @GetMapping
    @ApiOperation(value = "Go to swagger ui page", response = String.class)
    public String swaggerUiPage() {
        return "redirect:/swagger-ui.html";
    }

}
