package com.blogspot.myks790.assistant.server.todo;

import com.blogspot.myks790.assistant.server.security.Account;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Data
@Entity
class ToDo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private Date date;
    @Column(nullable = false)
    private int hour;
    @Column(nullable = false, unique = true, length = 200)
    private String task;
    @ManyToOne
    @JoinColumn(name="account_id")
    private Account account;
}
