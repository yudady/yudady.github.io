package io.github.yudady.spring;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.ApplicationContext;

public class SpringBeans {
    public static List<Map.Entry<String, Object>> getBeanDefinitions(ApplicationContext applicationContext) {
        return Arrays.stream(applicationContext.getBeanDefinitionNames())
            .flatMap(beanName -> Map.of(beanName, applicationContext.getBean(beanName)).entrySet().stream())
            .collect(Collectors.toList());

    }


    public static <T> T mapToBean(Map<String, ?> map, Class<T> clazz) {

        try {
            T bean = clazz.getDeclaredConstructor().newInstance();
            BeanMap beanMap = BeanMap.create(bean);
            beanMap.putAll(map);
            return bean;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            return null;
        }
    }
}
