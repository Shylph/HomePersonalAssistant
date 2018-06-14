package com.blogspot.myks790.assistant.server.todo;

import com.blogspot.myks790.assistant.server.security.UserAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final ToDoRepository toDoRepository;

    @GetMapping
    public ModelAndView  get(UserAuthentication userAuthentication){
        List<ToDo> toDoList = toDoRepository.findAll();
        return new ModelAndView("calendar","toDoList",toDoList);
    }
}
