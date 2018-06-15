package com.blogspot.myks790.assistant.server.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;

@Slf4j
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private final Environment environment;
    private final AuthorizationService authorizationService;
    private final AuthProvider authProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authorizationService).passwordEncoder(authorizationService.passwordEncoder());

    }

    @Override
    public void configure(WebSecurity web) {
        log.info("SecurityConfig configure web");
        web.ignoring().antMatchers("/js/**","/css/**","/images/**","/voice");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("SecurityConfig configure http");
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            log.info("SecurityConfig configure http - test profiles setting");
            http.csrf().disable();
            http.authorizeRequests().antMatchers("/**").permitAll();
        } else {
            http.authorizeRequests()
                    .antMatchers("/", "/login", "/login-error", "/login-processing","/voice").permitAll()
                    .antMatchers("/**").authenticated();

            http.formLogin()
                    .loginPage("/")
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .failureUrl("/login-error")
                    .defaultSuccessUrl("/calendar", true)
                    .usernameParameter("id")
                    .passwordParameter("password");

            http.logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true);

            http.authenticationProvider(authProvider);
        }

    }
}
