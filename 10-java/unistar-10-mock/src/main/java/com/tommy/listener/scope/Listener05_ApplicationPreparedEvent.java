package com.tommy.listener.scope;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

public class Listener05_ApplicationPreparedEvent implements ApplicationListener<ApplicationPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        System.out.println("[LOG] ApplicationPreparedEvent ");
    }
}