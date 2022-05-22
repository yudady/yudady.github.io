package com;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test01 {
    public static void main(String[] args) {
//        String[] strings = {"WEB", "ALL"};
        String[] strings = {"WEB", "ALL" , "ALL22"};
//        String[] strings = {"WEB"};
        String collect = Stream.of(strings).collect(Collectors.joining("','", "('", "')"));
        System.out.println(collect);
    }
}
