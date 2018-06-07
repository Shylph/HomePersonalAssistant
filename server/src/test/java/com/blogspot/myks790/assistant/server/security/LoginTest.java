package com.blogspot.myks790.assistant.server.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void hibernateTest() {
        Account account = new Account();
        account.setUsername("test");
        account.setPassword("testpw");
        account.setRole(Role.ROLE_ADMIN);
        accountRepository.save(account);
        accountRepository.delete(account);
    }
}
