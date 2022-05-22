package com.tommy.unistar.mock.demo1;

import java.io.IOException;

public class HelloAction implements HelloActable {
    private Greeter helloGreeter;
    private Appendable helloWriter;

    public HelloAction(Greeter helloGreeter, Appendable helloWriter) {
        super();
        this.helloGreeter = helloGreeter;
        this.helloWriter = helloWriter;
    }

    @Override
    public void sayHello(String actor, String subject) throws IOException {
        helloWriter.append(helloGreeter.getIntroduction(actor)).append(helloGreeter.getGreeting(subject));
    }
}