package com.blogspot.myks790.assistant.server;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
public class NoticeScheduler {
    private ThreadPoolTaskScheduler scheduler;
    private ScheduledFuture weatherFuture;
    private ScheduledFuture collectorFuture;

    public NoticeScheduler() {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
    }

    public void stopScheduler() {
        scheduler.shutdown();
    }

    private ScheduledFuture startScheduler(Runnable runnable, Trigger trigger) {
        return scheduler.schedule(runnable, trigger);
    }


    public void registerWeatherNotification(Runnable runnable, int notificationTime) {
        if (weatherFuture != null) {
            weatherFuture.cancel(false);
            weatherFuture = startScheduler(runnable, new CronTrigger("0 0 " + notificationTime + " * * *"));
        }
    }

    public void unregisterWeatherNotification() {
        if (weatherFuture != null)
            weatherFuture.cancel(false);
    }

    public void registerT_H_Collector(Runnable runnable, int collectTerm) {
        if (collectorFuture != null) {
            collectorFuture.cancel(false);
            collectorFuture = startScheduler(runnable, new CronTrigger("0 */" + collectTerm + " * * * *"));
        }
    }

    public void unregisterT_H_Collector() {
        if (collectorFuture != null)
            collectorFuture.cancel(false);
    }
}
