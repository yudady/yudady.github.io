package com.tommy.listener.scope;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

public class Listener01_ApplicationStartingEvent implements ApplicationListener<ApplicationStartingEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        // 可以堵住應用程式
        for (int i = 10; i > 0; i--) {
            try {
                TimeUnit.NANOSECONDS.sleep(1 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[LOG] ApplicationStartingEvent " + i);
        }

    }
}