package com.teamdev.todolist.controllers;

import com.teamdev.todolist.entities.User;
import com.teamdev.todolist.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.teamdev.todolist.configurations.support.Constants.*;

/**
 * @author Alexandr Stegnin
 */

@RestController
@RequestMapping(API + API_USERS)
@Api(value = API + API_USERS, description = "Operations with system users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Создать пользователя
     *
     * @param user - пользователь в формате json
     * @return - User
     */
    @ApiOperation(value = "Create system user", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @PostMapping
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    /**
     * Найти пользователя по id
     *
     * @param userId - id пользователя
     * @return - пользователя
     */
    @ApiOperation(value = "Get user by id", response = User.class)
    @GetMapping(value = API_USERS_USER_ID)
    public User findById(@PathVariable(API_USER_ID) Long userId) {
        return userService.findOne(userId);
    }

    /**
     * Достать всех пользователей
     *
     * @return - список пользователей
     */
    @ApiOperation(value = "View a list of available users", response = User.class, responseContainer = "List")
    @GetMapping
    public List<User> findAllUsers() {
        return userService.findAll();
    }

    /**
     * Изменить пользователя
     *
     * @param user   - данные пользователя для изменения в формате json
     * @return - User
     */
    @ApiOperation(value = "Update user", response = User.class)
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public User update(@RequestBody User user) {
        return userService.update(user);
    }

    /**
     * Удалить пользователя по id
     *
     * @param userId - id пользователя
     */
    @ApiOperation(value = "Delete user by id")
    @DeleteMapping(value = API_USERS_USER_ID)
    public void remove(@PathVariable(API_USER_ID) Long userId) {
        userService.delete(userId);
    }

    /**
     * Изменить пароль пользователя
     *
     * @param userId   - id пользователя для изменения
     * @param newPassword - новый пароль пользователя
     * @return - String
     */
    @ApiOperation(value = "Change user password", response = String.class)
    @PutMapping(path = API_USERS_CHANGE_PASSWORD, produces = MediaType.APPLICATION_JSON_VALUE)
    public String changePassword(@RequestParam(name = "userId") Long userId, @RequestParam(name = "password") String newPassword) {
        userService.changePassword(userId, newPassword);
        return "Password have been changed successfully";
    }

}
