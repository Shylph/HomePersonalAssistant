package com.blogspot.myks790.assistant.server.ToDo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/calendar/api")
@RequiredArgsConstructor
public class CalendarRestController {

    private final ToDoRepository toDoRepository;

    @PostMapping
    public ToDo create(@RequestBody ToDo toDo) {
        log.info("/calendar/api : create");
        return toDoRepository.save(toDo);
    }

    @GetMapping("/{id}")
    public ToDo get(@PathVariable long id) {
        return toDoRepository.findById(id).get();
    }


    @GetMapping("/list")
    public List<ToDo> list() {
        log.info("list get");
        return toDoRepository.findAll();
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
