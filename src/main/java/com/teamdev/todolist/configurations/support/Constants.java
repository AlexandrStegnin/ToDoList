package com.teamdev.todolist.configurations.support;

/**
 * @author Alexandr Stegnin
 */
public class Constants {

    public static final String API = "/api";
    public static final String AUTHORITIES_KEY = "authorities";

    //    API для работы с User
    public static final String API_USERS = "/users";
    public static final String API_USER_ID = "userId";
    public static final String API_USERS_USER_ID = "/{userId}";

    //    App roles constants
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String ROLE_ADMIN = ROLE_PREFIX + ADMIN;
    public static final String ROLE_USER = ROLE_PREFIX + USER;


}
