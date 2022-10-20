package com.tommy.listener.scope;

import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;

public class Listener07_ServletWebServerInitializedEvent implements ApplicationListener<ServletWebServerInitializedEvent> {


    @Override
    public void onApplicationEvent(final ServletWebServerInitializedEvent event) {
        System.out.println("[LOG] ServletWebServerInitializedEvent ");
    }
}