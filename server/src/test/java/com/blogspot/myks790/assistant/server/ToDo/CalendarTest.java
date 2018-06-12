package com.blogspot.myks790.assistant.server.ToDo;

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
 *   테스트 하려면 vm options 에 프로파일 관련하여 -Dspring.profiles.active=test 추가해야 함.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CalendarTest {
    private static final String PATH = "/calendar/api";

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
        delete(createdToDo);
    }

    private void delete(ToDo createdToDo) {
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

    private ToDo getTestToDoData(Date date) {
        ToDo toDo = new ToDo();
        toDo.setDate(date);
        toDo.setHour(1);
        toDo.setTask("캘린더 동작하게하기");
        return toDo;
    }

    @Test
    public void getWeekToDoReport() {
/*
        ToDo toDo1 = getTestToDoData(Date.valueOf("2018-7-12"));
        createToDo(toDo1);
        ToDo toDo2 = getTestToDoData(Date.valueOf("2018-7-16"));
        createToDo(toDo2);

        ToDo result1 = doList.get(0);
        ToDo result2 = doList.get(1);
        validate(toDo1, result1);
        validate(toDo2, result2);
        delete(result1);
        delete(result2);
*/
        String startDate = "2018-7-10";
        String endDate = "2018-7-17";
        List<ToDo> toDoList = restTemplate.getForObject(PATH + "/week_report?start_date=" + startDate + "&end_date=" + endDate, List.class);
        assertThat(toDoList, not(IsEmptyCollection.empty()));
    }


}
