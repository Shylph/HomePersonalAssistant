package com.blogspot.myks790.assistant.server.ToDo;

import org.springframework.data.jpa.repository.JpaRepository;

interface ToDoRepository extends JpaRepository<ToDo, Long> {
}
