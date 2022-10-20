package com.tommy.unistar.mock.jupiter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * https://howtodoinjava.com/junit-5-tutorial/
 *
 * @BeforeEach 在方法上注解，在每个测试方法运行之前执行。
 * @AfterEach 在方法上注解，在每个测试方法运行之后执行
 * @BeforeAll 该注解方法会在所有测试方法之前运行，该方法必须是静态的。
 * @AfterAll 该注解方法会在所有测试方法之后运行，该方法必须是静态的。
 * @Test 用于将方法标记为测试方法
 * @DisplayName 用于为测试类或测试方法提供任何自定义显示名称
 * @Disable 用于禁用或忽略测试类或方法
 * @Nested 用于创建嵌套测试类
 * @Tag 用于测试发现或过滤的标签来标记测试方法或类
 * @TestFactory 标记一种方法是动态测试的测试工场
 */
public class Jupiter注解 {

    @BeforeAll
    public static void beforeAll() {
        System.out.println("[LOG] BeforeAll 该注解方法会在所有测试方法之前运行，该方法必须是静态的");
    }

    @AfterAll
    public static void afterAll() {
        System.out.println("[LOG] AfterAll 该注解方法会在所有测试方法之前运行，该方法必须是静态的");
    }

    @BeforeEach
    public void beforeEach() {
        System.out.println("[LOG] BeforeEach 在方法上注解，在每个测试方法运行之前执行。");
    }

    @AfterEach
    public void AfterEach() {
        System.out.println("[LOG] AfterEach 在方法上注解，在每个测试方法运行之后执行");
    }

    @Test
    public void test01() {
        System.out.println("[LOG] Test test01 用于将方法标记为测试方法");
    }

    @Test
    public void test02() {
        System.out.println("[LOG] Test test02 用于将方法标记为测试方法");
    }

    @Test
    public void test03() {
        System.out.println("[LOG] Test test03 用于将方法标记为测试方法");
    }

}
