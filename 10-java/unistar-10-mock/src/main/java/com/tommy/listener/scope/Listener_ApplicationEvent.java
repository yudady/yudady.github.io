package com.tommy.listener.scope;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class Listener_ApplicationEvent implements ApplicationListener {
    @Override
    public void onApplicationEvent(final ApplicationEvent envet) {
        System.out.println("[LOG] ApplicationEvent ");
    }
}
