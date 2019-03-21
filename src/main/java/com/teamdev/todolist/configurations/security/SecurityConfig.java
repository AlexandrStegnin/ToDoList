package com.teamdev.todolist.configurations.security;

import com.vaadin.flow.spring.annotation.EnableVaadin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.teamdev.todolist.configurations.support.Constants.*;

@EnableVaadin
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Order(2)
    @Configuration
    public static class UIConfigurationAdapter extends WebSecurityConfigurerAdapter {

        final
        BCryptPasswordEncoder passwordEncoder;

        final
        UserDetailsServiceImpl userDetailsService;

        @Autowired
        public UIConfigurationAdapter(BCryptPasswordEncoder passwordEncoder, UserDetailsServiceImpl userDetailsService) {
            this.passwordEncoder = passwordEncoder;
            this.userDetailsService = userDetailsService;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll() // важный пункт, без него переход по url сбрасывал аутентификацию
                    .antMatchers(PATH_SEPARATOR + ADMIN_PAGE).hasRole(ADMIN)
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().loginPage(LOGIN_URL).permitAll()
                    .defaultSuccessUrl(PATH_SEPARATOR + PROFILE_PAGE, true)
                    .successHandler(this::loginSuccessHandler)
                    .failureHandler(this::loginFailureHandler)
                    .and()
                    .logout().logoutUrl(LOGOUT_URL).permitAll()
                    .logoutSuccessHandler(this::logoutSuccessHandler)
                    .invalidateHttpSession(true)
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(authenticationProvider());
        }

        @Override
        public void configure(org.springframework.security.config.annotation.web.builders.WebSecurity web) {
            web.ignoring().antMatchers(ALL_WEB_IGNORING_MATCHERS);
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setUserDetailsService(userDetailsService);
            provider.setPasswordEncoder(passwordEncoder);
            return provider;
        }

        private void loginSuccessHandler(
                HttpServletRequest request,
                HttpServletResponse response,
                Authentication authentication) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            response.setStatus(HttpStatus.OK.value());
        }

        private void loginFailureHandler(
                HttpServletRequest request,
                HttpServletResponse response,
                AuthenticationException e) throws IOException {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }

        private void logoutSuccessHandler(
                HttpServletRequest request,
                HttpServletResponse response,
                Authentication authentication) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpStatus.OK.value());
        }
    }

    @Order(1)
    @Configuration
    public static class ApiConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        private UserDetailsServiceImpl userDetailsService;

        @Autowired
        private JwtConfig jwtConfig;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                    .and()
                    .authorizeRequests()
                    .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll() // важный пункт для VAADIN, без него переход по url сбрасывал аутентификацию
                    .antMatchers(PATH_SEPARATOR + PROFILE_PAGE).permitAll()
                    .antMatchers(API_INFO_URL).permitAll()
                    .antMatchers(PATH_SEPARATOR).permitAll()
                    .antMatchers(ALL_HTTP_MATCHERS).permitAll()
                    .regexMatchers(HttpMethod.POST, "/\\?v-r=.*").permitAll() // VAADIN шлёт сюда запросы, нужно открыть
                    .and()
                    // handle an authorized attempts
                    .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                    .and()
                    // Add a filter to validate user credentials and add token in the response header

                    // What's the authenticationManager()?
                    // An object provided by WebSecurityConfigurerAdapter, used to authenticate the user passing user's credentials
                    // The filter needs this auth manager to authenticate the user.
                    .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig))
                    .addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                    // allow all POST requests
                    .antMatchers(HttpMethod.POST, jwtConfig.getUri()).permitAll()
                    .antMatchers(HttpMethod.GET, PATH_SEPARATOR).permitAll()
                    .antMatchers(ALL_SWAGGER_MATCHERS).permitAll()
                    // any other requests must be authenticated
                    .anyRequest().authenticated();
        }

        // Spring has UserDetailsService interface, which can be overriden to provide our implementation for fetching user from database (or any other source).
        // The UserDetailsService object is used by the auth manager to load the user from database.
        // In addition, we need to define the password encoder also. So, auth manager can compare and verify passwords.
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        }

        @Bean
        public JwtConfig jwtConfig() {
            return new JwtConfig();
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

    }
}