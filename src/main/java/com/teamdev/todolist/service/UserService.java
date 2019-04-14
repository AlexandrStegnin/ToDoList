package com.teamdev.todolist.service;

import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.repository.UserRepository;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static com.teamdev.todolist.configuration.support.Constants.PATH_SEPARATOR;
import static com.teamdev.todolist.configuration.support.Constants.ROLE_USER;

/**
 * @author Alexandr Stegnin
 */

@Service
@Transactional(readOnly = true)
public class UserService {

    @Value("${spring.config.file-upload-directory}")
    private String FILE_UPLOAD_DIRECTORY;

    // TODO: 07.03.2019 Выбрасывать исключения, если пользователь не найден

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder encoder;
    private final UserProfileService userProfileService;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder,
                       RoleService roleService, UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.roleService = roleService;
    }

    @Transactional
    public User create(User user) {
        user.setPasswordHash(encoder.encode(user.getPassword()));
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

    @Transactional
    public User update(User user) {
        return save(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void delete(User user) {
        delete(user.getId());
    }

    /**
     * Рассчитываем хэш пароля
     *
     * @param password - пароль для расчёта хэша
     * @return String hash
     */
    private String passwordToHash(String password) {
        if (Objects.equals(null, password) || org.springframework.util.StringUtils.isEmpty(password)) {
            return null;
        }
        return encoder.encode(password);
    }

    /**
     * Сохраняет пользователя, не меняя пароля.
     *
     * @param secUser - пользователь, которому надо поменять пароль
     * @return User
     */
    @Transactional
    public User save(User secUser) {
        User dbUser = userRepository.findByLogin(secUser.getLogin());
        // Пароль не сохраняется (transient !), сохраняется только HASH
        if (!StringUtils.isEmpty(secUser.getPassword())) {
            secUser.setPasswordHash(passwordToHash(secUser.getPassword()));
        } else {
            // нам нужно сохранить пароль (если он не задан)
            // подставляем старый пароль из базы
            secUser.setPasswordHash(dbUser.getPasswordHash());
        }
        if (Objects.equals(null, secUser.getRoles())) secUser.setRoles(dbUser.getRoles());
        if (Objects.equals(null, secUser.getProfile())) secUser.setProfile(dbUser.getProfile());
        return userRepository.save(secUser);
    }

    @Transactional
    public void registerNewUser(User newUser) {
        newUser.setPasswordHash(passwordToHash(newUser.getPassword()));
        newUser.setRoles(new HashSet<>(Collections.singletonList(roleService.getDefaultUserRole())));
        userRepository.save(newUser);
    }

    private User getById(Long useId) {
        return userRepository.getOne(useId);
    }

    @Transactional
    public void changePassword(long userId, String passwordNew) {
        //TODO добавть проверок для пароля
        User userDb = getById(userId);
        userDb.setPasswordHash(passwordToHash(passwordNew));
        userRepository.save(userDb);
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public boolean isLoginFree(String login) {
        return Objects.equals(null, findByLogin(login));
    }

    public boolean emailIsBusy(String email) {
        return userProfileService.emailIsBusy(email);
    }

    public void saveUserAvatar(User user, MemoryBuffer buffer) {
        if (!"".equals(buffer.getFileName())) {
            final File[] targetFile = {null};
            dropUserDir(user); // todo проверять наличие файла аватара в папке и удалять
            createUserDir(user);
            String fileName = buffer.getFileName();
            targetFile[0] = new File(FILE_UPLOAD_DIRECTORY + user.getLogin() + PATH_SEPARATOR + fileName);
            try {
                Files.copy(buffer.getInputStream(), targetFile[0].toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка копирования файла", e);
            }
            user.getProfile().setAvatar(fileName);
        }
    }

    private void createUserDir(User user) {
        Path userDir = Paths.get(FILE_UPLOAD_DIRECTORY + user.getLogin());
        if (!Files.exists(userDir)) {
            try {
                Files.createDirectories(userDir);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при создании директории: " + userDir.getFileName().toString(), e);
            }
        }
    }

    private void dropUserDir(User user) {
        Path userDir = Paths.get(FILE_UPLOAD_DIRECTORY + user.getLogin());
        if (Files.exists(userDir)) {
            try {
                Files.walk(userDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при удалении папки с изображениями пользователя", e);
            }
        }
    }

    public boolean matchesPasswords(String oldPass, String dbPass) {
        return encoder.matches(oldPass, dbPass);
    }

    public Long count() {
        return userRepository.count();
    }

}
