package com.blogspot.myks790.assistant.server.home_info;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class HomeInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long info_id;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    @Column(nullable = false)
    private float temperature;
    @Column(nullable = false)
    private float humidity;


}
