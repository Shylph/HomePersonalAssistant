package com.blogspot.myks790.assistant.server.ToDo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

interface ToDoRepository extends JpaRepository<ToDo, Long> {
    @Query("select to_do from ToDo to_do where to_do.date between :start_date and :end_date")
    List<ToDo> findAll(@Param("start_date") Date startDate, @Param("end_date") Date endDate);
}
