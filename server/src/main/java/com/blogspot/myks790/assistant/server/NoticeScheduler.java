package com.blogspot.myks790.assistant.server;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
public class NoticeScheduler {
    private ThreadPoolTaskScheduler scheduler;

    public NoticeScheduler() {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();

    }

    public void stopScheduler() {
        scheduler.shutdown();
    }

    public void startScheduler(Runnable runnable, Trigger trigger) {
        ScheduledFuture future = scheduler.schedule(runnable, trigger);

    }


}
