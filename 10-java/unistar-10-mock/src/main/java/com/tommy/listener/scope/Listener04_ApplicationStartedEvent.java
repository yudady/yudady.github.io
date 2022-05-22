package com.tommy.listener.scope;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

public class Listener04_ApplicationStartedEvent implements ApplicationListener<ApplicationStartedEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println("[LOG] ApplicationStartedEvent ");
    }
}