package com.blogspot.myks790.assistant.server;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String ip;
    @Column(nullable = false)
    private int port;
    @Column(nullable = false, length = 200)
    private String name;
}
