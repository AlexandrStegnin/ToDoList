package com.teamdev.todolist.controllers;

import com.teamdev.todolist.entities.User;
import com.teamdev.todolist.services.UserService;
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
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Создать пользователя
     * @param user - пользователь в формате json
     * @return - User
     */
    @PostMapping
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    /**
     * Найти пользователя по id
     * @param userId - id пользователя
     * @return - пользователя
     */
    @GetMapping(value = API_USERS_USER_ID)
    public User findById(@PathVariable(API_USER_ID) Long userId) {
        return userService.findOne(userId);
    }

    /**
     * Достать всех пользователей
     * @return - список пользователей
     */
    @GetMapping
    public List<User> findAllUsers() {
        return userService.findAll();
    }

    /**
     * Изменить пользователя
     * @param userId - id пользователя
     * @param user - данные пользователя для изменения в формате json
     * @return - response с сообщением
     */
    @PutMapping(value = API_USERS_USER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public User update(@PathVariable(API_USER_ID) Long userId, @RequestBody User user) {
        user.setId(userId);
        return userService.update(user);
    }

    /**
     * Удалить пользователя по id
     * @param userId - id пользователя
     */
    @DeleteMapping(value = API_USERS_USER_ID)
    public void remove(@PathVariable(API_USER_ID) Long userId) {
        userService.delete(userId);
    }

}
