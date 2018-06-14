package com.blogspot.myks790.assistant.server.home_info;

import com.blogspot.myks790.assistant.server.security.Account;
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
    @ManyToOne
    @JoinColumn(name="account_id")
    private Account account;
}
