package tk.tommy.jdk8;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;


public class ConsumerTest {

    public static Consumer<Person> process(List<Person> lisiList) {

        Consumer<Person> consumer = (x -> {
            if (x.name.equals("lisi")) {
                lisiList.add(x);
            }
        });

        consumer = consumer.andThen(
            x -> lisiList.removeIf(y -> y.age < 22)
        );

        consumer = consumer.andThen(
            x -> lisiList.removeIf(y -> y.age > 25)
        );

        return consumer;
    }

    public static void main(String[] args) {
        List<Person> lisiList = new ArrayList<>();
        Consumer<Person> consumer = process(lisiList);
        Stream.of(
            new Person(21, "zhangsan"),
            new Person(22, "lisi"),
            new Person(23, "wangwu"),
            new Person(24, "wangwu"),
            new Person(23, "lisi"),
            new Person(26, "lisi"),
            new Person(26, "zhangsan")
        ).forEach(consumer);

//        System.out.println(JSON.toJSONString(lisiList));
    }

}
