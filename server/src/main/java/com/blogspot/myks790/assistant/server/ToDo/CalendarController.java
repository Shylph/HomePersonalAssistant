package com.blogspot.myks790.assistant.server.ToDo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/calendar")
public class CalendarController {
    @GetMapping
    public String get() {
        return "calendar";
    }
}
