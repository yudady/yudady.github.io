package tk.tommy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * https://www.tutorialspoint.com/spring_boot/spring_boot_apache_kafka.htm
 * https://dzone.com/articles/magic-of-kafka-with-spring-boot
 */
@SpringBootApplication
public class KafkaDemoApplication {
    public static void main(final String[] args) {
        SpringApplication.run(KafkaDemoApplication.class, args);
    }
}