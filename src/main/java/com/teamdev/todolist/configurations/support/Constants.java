package com.teamdev.todolist.configurations.support;

import java.util.Locale;

/**
 * @author Alexandr Stegnin
 */
public class Constants {

    public static final String API = "/api";
    public static final String AUTHORITIES_KEY = "authorities";
    public static final String API_INFO_URL = API + "/info";

    //    API для работы с User
    public static final String API_USERS = "/users";
    public static final String API_USER_ID = "userId";
    public static final String API_USERS_USER_ID = "/{userId}";

    //    API для работы с Role
    public static final String API_ROLES = "/roles";
    public static final String API_ROLE_ID = "roleId";
    public static final String API_ROLES_ROLE_ID = "/{roleId}";

    //    API для работы с Task
    public static final String API_TASK_STATUSES = "/task-statuses";
    public static final String API_TASK_STATUS_ID = "taskStatusId";
    public static final String API_TASK_STATUSES_STATUS_ID = "/{taskStatusId}";

    //    App roles constants
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String ROLE_ADMIN = ROLE_PREFIX + ADMIN;
    public static final String ROLE_USER = USER;

    //    SWAGGER Uri
    public static final String[] ALL_SWAGGER_MATCHERS = {"/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**"};

    public static final String PATH_SEPARATOR = "/";
    /* Application pages constants */
    public static final String LOGIN_PAGE = "login";
    public static final String LOGOUT_PAGE = "logout";

    public static final Locale LOCALE_RU = new Locale("ru", "RU");

    public static final String LOGOUT_URL = PATH_SEPARATOR + LOGOUT_PAGE;
    public static final String LOGIN_URL = PATH_SEPARATOR + LOGIN_PAGE;

    /* ADMINS PAGES */
    public static final String ADMIN_PAGE = "admin";
    public static final String USERS_PAGE = "users";
    public static final String ADMIN_USERS_PAGE = ADMIN_PAGE + PATH_SEPARATOR + USERS_PAGE;
    public static final String ROLES_PAGE = "roles";
    public static final String ADMIN_ROLES_PAGE = ADMIN_PAGE + PATH_SEPARATOR + ROLES_PAGE;

    public static final String PROFILE_PAGE = "profile";

    public static final String TASK_LIST_PAGE = "task_list"; //todo: может лучше корчень task, а от него list, create, delete и т.п.?

    public static final String[] ALL_HTTP_MATCHERS = {
            "/VAADIN/**", "/HEARTBEAT/**", "/UIDL/**", "/resources/**",
            "/login", "/login**", "/login/**", "/manifest.json", "/icons/**", "/images/**",
            // (development mode) static resources
            "/frontend/**",
            // (development mode) webjars
            "/webjars/**",
            // (development mode) H2 debugging console
            "/h2-console/**",
            // (production mode) static resources
            "/frontend-es5/**", "/frontend-es6/**"
    };

    public static final String[] ALL_WEB_IGNORING_MATCHERS = {
            // Vaadin Flow static resources
            "/VAADIN/**",

            // the standard favicon URI
            "/favicon.ico",

            // web application manifest
            "/manifest.json",

            // icons and images
            "/icons/**",
            "/images/**",

            // (development mode) static resources
            "/frontend/**",

            // (development mode) webjars
            "/webjars/**",

            // (development mode) H2 debugging console
            "/h2-console/**",

            // (production mode) static resources
            "/frontend-es5/**", "/frontend-es6/**"
    };

}
