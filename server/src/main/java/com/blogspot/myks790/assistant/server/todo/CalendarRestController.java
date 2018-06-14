package com.blogspot.myks790.assistant.server.todo;

import com.blogspot.myks790.assistant.server.security.UserAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/calendar/api")
@RequiredArgsConstructor
public class CalendarRestController {

    private final ToDoRepository toDoRepository;

    @PostMapping
    public ToDo create(@RequestBody ToDo toDo, UserAuthentication authentication) {
        log.info("/calendar/api : create");
        toDo.setAccount(authentication.getAccount());
        return toDoRepository.save(toDo);
    }

    @GetMapping("/{id}")
    public ToDo get(@PathVariable long id) {
        return toDoRepository.findById(id).get();
    }


    @GetMapping("/list")
    public List<ToDo> list( UserAuthentication authentication) {
        log.info("list get");
        return toDoRepository.findAll(authentication.getAccount());
    }

    @GetMapping("/week_report")
    public List<ToDo> weekReport(@RequestParam(value = "start_date") String startDate, @RequestParam(value = "end_date") String endDate) {
        return toDoRepository.findAll(Date.valueOf(startDate), Date.valueOf(endDate));
    }

    @PutMapping
    public void modify(@RequestBody ToDo toDo) {
        toDoRepository.save(toDo);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        toDoRepository.delete(toDoRepository.findById(id).get());
    }
}
