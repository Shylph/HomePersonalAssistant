package com.blogspot.myks790.assistant.server.security;


import com.blogspot.myks790.assistant.server.security.Account;
import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Data
public class UserAuthentication extends UsernamePasswordAuthenticationToken {
    private Account account;

    public UserAuthentication(String id, String password, List<GrantedAuthority> grantedAuthorityList, Account account) {
        super(id, password, grantedAuthorityList);
        this.account = account;
    }
}

