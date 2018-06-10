package com.blogspot.myks790.assistant.server.ToDo;

import com.blogspot.myks790.assistant.server.ToDo.ToDo;
import com.blogspot.myks790.assistant.server.ToDo.ToDoRepository;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/*
 *   테스트를 돌리려면 SecurityCongig에서 csrf를 꺼야함
 *   또한 path를 허용해줘야 함
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CalendarTest {
    private static final String PATH = "/calendar/api";
    @Autowired
    ToDoRepository toDoRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void create() {
        ToDo toDo = getTestToDoData();
        ToDo createdTodo = createToDo(toDo);
        validate(toDo, createdTodo);
    }

    private void validate(ToDo toDo, ToDo createdToDo) {
        ToDo resultToDo = restTemplate.getForObject(PATH + "/" + createdToDo.getId(), ToDo.class);
        assertThat(resultToDo.getDate().toString(), is(toDo.getDate().toString()));
        assertThat(resultToDo.getHour(), is(toDo.getHour()));
        assertThat(resultToDo.getTask(), is(toDo.getTask()));
    }

    private ToDo createToDo(ToDo toDo) {
        return restTemplate.postForObject(PATH, toDo, ToDo.class);
    }

    @Test
    public void list() {
        List<ToDo> toDoList = restTemplate.getForObject(PATH + "/list", List.class);
        assertThat(toDoList, not(IsEmptyCollection.empty()));
    }


    @Test
    public void modify() {
        ToDo toDo = getTestToDoData();
        ToDo createdTodo = createToDo(toDo);

        int hour = 5;
        createdTodo.setHour(hour);
        toDo.setHour(hour);

        restTemplate.put(PATH, createdTodo);
        validate(toDo, createdTodo);
    }


    @Test
    public void delete() {
        ToDo toDo = getTestToDoData();
        ToDo createdToDo = createToDo(toDo);
        validate(toDo, createdToDo);
        restTemplate.delete(PATH + "/" + createdToDo.getId());
        ToDo resultToDo = restTemplate.getForObject(PATH + "/" + createdToDo.getId(), ToDo.class);
        assertThat(resultToDo.getId(), is(0L));
        assertThat(resultToDo.getDate(), is(nullValue()));
        assertThat(resultToDo.getHour(), is(0));
        assertThat(resultToDo.getTask(), is(nullValue()));
    }

    private ToDo getTestToDoData() {
        ToDo toDo = new ToDo();
        toDo.setDate(Date.valueOf("2018-6-6"));
        toDo.setHour(1);
        toDo.setTask("캘린더 동작하게하기");
        return toDo;
    }
}