package com.tommy.unistar.mock.demo1;

public class HelloGreeter implements Greeter {
    private String hello;
    private String segmenter;

    public HelloGreeter(String hello, String segmenter) {
        this.hello = hello;
        this.segmenter = segmenter;
    }

    @Override
    public String getGreeting(String subject) {
        return hello + " " + subject;
    }

    @Override
    public String getIntroduction(String actor) {
        return actor + segmenter;
    }
}