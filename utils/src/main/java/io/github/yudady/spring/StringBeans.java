package io.github.yudady.spring;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationContext;

public class StringBeans {
    public static List<Map.Entry<String, Object>> getBeanDefinitions(ApplicationContext applicationContext) {
        return Arrays.stream(applicationContext.getBeanDefinitionNames())
            .flatMap(beanName -> Map.of(beanName, applicationContext.getBean(beanName)).entrySet().stream())
            .collect(Collectors.toList());

    }
}
