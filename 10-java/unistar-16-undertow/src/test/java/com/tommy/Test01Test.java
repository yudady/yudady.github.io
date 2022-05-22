package com.tommy;

import java.util.stream.IntStream;
import org.junit.Test;

public class Test01Test {

    @Test
    public void testttt01() {


        IntStream.range(0, 10).forEach(i -> System.out.println("[LOG] " + i));
    }

    @Test
    public void testttt02() {


        IntStream.range(0, 10).forEach(i -> System.out.println("[LOG] " + i));
    }


}