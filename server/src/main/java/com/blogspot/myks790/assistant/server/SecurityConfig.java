package com.blogspot.myks790.assistant.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthProvider authProvider;

    @Autowired
    AuthorizationService authorizationService;



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
        log.info("SecurityConfig configure http");
        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/", "/login", "/login-error", "/login-processing").permitAll()
                .antMatchers("/**").authenticated();

        http.formLogin()
                .loginPage("/")
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .failureUrl("/login-error")
                .defaultSuccessUrl("/home", true)
                .usernameParameter("id")
                .passwordParameter("password");

        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true);

        http.authenticationProvider(authProvider);


    }
}
