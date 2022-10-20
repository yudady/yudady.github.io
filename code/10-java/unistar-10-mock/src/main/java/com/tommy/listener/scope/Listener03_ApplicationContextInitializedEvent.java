package com.tommy.listener.scope;

import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;

public class Listener03_ApplicationContextInitializedEvent implements ApplicationListener<ApplicationContextInitializedEvent> {

    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent applicationStartedEvent) {
        System.out.println("[LOG] ApplicationContextInitializedEvent ");
    }
}