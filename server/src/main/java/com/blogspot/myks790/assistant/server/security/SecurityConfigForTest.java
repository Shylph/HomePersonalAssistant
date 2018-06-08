package com.blogspot.myks790.assistant.server.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Slf4j
@EnableWebSecurity
@Profile("test")
@Configuration
@RequiredArgsConstructor
public class SecurityConfigForTest extends WebSecurityConfigurerAdapter {

    private final AuthorizationService authorizationService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authorizationService).passwordEncoder(authorizationService.passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) {
        log.info("SecurityConfig configure web");
        web.ignoring().antMatchers("/js/**");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("SecurityConfig configure http : test");

        http.csrf().disable();
        http.authorizeRequests().antMatchers("/**").permitAll();
    }

}
