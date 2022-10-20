package com.tommy.unistar.mock.demo1;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HelloActionIntegrationTest {
    HelloActable helloAction;
    Greeter helloGreeter;
    Appendable helloWriterMock;

    @BeforeAll
    public void setUp() {
        System.out.println("[LOG] BeforeAll ");
        helloGreeter = new HelloGreeter("welcome", " says ");
        helloWriterMock = mock(Appendable.class);
        helloAction = new HelloAction(helloGreeter, helloWriterMock);
    }

    @Test
    public void testSayHello() throws Exception {
        when(helloWriterMock.append(any(String.class))).thenReturn(helloWriterMock);

        helloAction.sayHello("integrationTest", "universe");

        verify(helloWriterMock, times(2)).append(any(String.class));
        verify(helloWriterMock, times(1)).append(eq("integrationTest says "));
        verify(helloWriterMock, times(1)).append(eq("welcome universe"));
    }
}