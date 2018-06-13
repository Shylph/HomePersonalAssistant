package com.blogspot.myks790.assistant.server.home_info;

import com.blogspot.myks790.assistant.server.security.Account;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class HomeInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    @Column(nullable = false)
    private float temperature;
    @Column(nullable = false)
    private float humidity;
    @ManyToOne
    @JoinColumn(name="account_id")
    private Account account;
}
