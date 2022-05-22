package com.tommy.listener.scope;

import java.util.concurrent.TimeUnit;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class Listener06_ContextRefreshedEvent implements ApplicationListener<ContextRefreshedEvent> {


    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {

        for (int i = 10; i > 0; i--) {
            try {
                TimeUnit.NANOSECONDS.sleep(1 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[LOG] ContextRefreshedEvent " + i);
        }
    }
}