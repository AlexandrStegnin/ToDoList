package com.teamdev.todolist.service;

import com.teamdev.todolist.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexandr Stegnin
 */

@Service
@Transactional
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public boolean emailIsBusy(String email) {
        return !userProfileRepository.existsByEmail(email);
    }

}
