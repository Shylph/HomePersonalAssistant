package com.blogspot.myks790.assistant.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AuthProvider implements AuthenticationProvider {

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("authenticate");
        String id = authentication.getName();
        String password = authentication.getCredentials().toString();
        return authenticate(id, password);
    }

    public Authentication authenticate(String id, String password) throws AuthenticationException {
        Account account = authorizationService.login(id, password);
        if (account == null) return null;
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        String role = account.getRole().toString();
        grantedAuthorityList.add(new SimpleGrantedAuthority(role));
        return new UserAuthentication(id, password, grantedAuthorityList, account);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
