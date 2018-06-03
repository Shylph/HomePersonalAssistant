package com.blogspot.myks790.assistant.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class LoginController {

    @GetMapping(value = "/login")
    public String login() {
        log.info("login get");
        return "login";
    }

    @RequestMapping("/")
    public String root() {
        log.info("root");
        return "redirect:/login";
    }

}
