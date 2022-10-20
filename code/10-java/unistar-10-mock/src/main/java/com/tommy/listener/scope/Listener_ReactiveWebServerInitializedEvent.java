package com.tommy.listener.scope;

import org.springframework.boot.web.reactive.context.ReactiveWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;


/**
 * WebFlux
 */
public class Listener_ReactiveWebServerInitializedEvent implements ApplicationListener<ReactiveWebServerInitializedEvent> {


    @Override
    public void onApplicationEvent(final ReactiveWebServerInitializedEvent event) {
        System.out.println("[LOG] ReactiveWebServerInitializedEvent ");
    }
}