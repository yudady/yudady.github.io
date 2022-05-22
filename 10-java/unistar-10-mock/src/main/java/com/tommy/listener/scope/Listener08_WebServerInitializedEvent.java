package com.tommy.listener.scope;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;

public class Listener08_WebServerInitializedEvent implements ApplicationListener<WebServerInitializedEvent> {


    @Override
    public void onApplicationEvent(final WebServerInitializedEvent event) {
        System.out.println("[LOG] WebServerInitializedEvent ");
    }
}