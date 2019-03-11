package com.teamdev.todolist.services;

import com.teamdev.todolist.entities.User;
import com.teamdev.todolist.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.teamdev.todolist.configurations.support.Constants.ROLE_USER;

/**
 * @author Alexandr Stegnin
 */

@Service
public class UserService {

    // TODO: 07.03.2019 Выбрасывать исключения, если пользователь не найден

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder encoder,
                       RoleService roleService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.roleService = roleService;
    }

    public User create(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        if (Objects.equals(null, user.getRoles()) || user.getRoles().isEmpty())
            user.setRoles(Collections.singleton(roleService.findByTitle(ROLE_USER)));
        return userRepository.save(user);
    }

    public User findOne(Long id) {
        return userRepository.getOne(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public void delete(User user) {
        delete(user.getId());
    }

}
