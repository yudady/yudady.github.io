package com.tommy.unistar.mock.demo1;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class HelloActionUnitTest {

    Greeter helloGreeterMock;
    Appendable helloWriterMock;
    HelloActable helloAction;

    @BeforeAll
    public void setUp() {
        this.helloGreeterMock = mock(Greeter.class);
        this.helloWriterMock = mock(Appendable.class);
        this.helloAction = new HelloAction(this.helloGreeterMock, this.helloWriterMock);
    }

    @Test
    public void testSayHello() throws Exception {
        when(this.helloWriterMock.append(any(String.class))).thenReturn(this.helloWriterMock);
        when(this.helloGreeterMock.getIntroduction(eq("unitTest"))).thenReturn("unitTest : ");
        when(this.helloGreeterMock.getGreeting(eq("world"))).thenReturn("hi world");

        this.helloAction.sayHello("unitTest", "world");

        verify(this.helloGreeterMock).getIntroduction(eq("unitTest"));
        verify(this.helloGreeterMock).getGreeting(eq("world"));

        verify(this.helloWriterMock, times(2)).append(any(String.class));
        verify(this.helloWriterMock, times(1)).append(eq("unitTest : "));
        verify(this.helloWriterMock, times(1)).append(eq("hi world"));
    }
}