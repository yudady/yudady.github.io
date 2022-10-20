package com.tommy;

import com.tommy.listener.scope.ApplicationFailedEventListener;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * <p>
 * {@link org.springframework.boot.SpringApplication}
 * {@link SpringApplicationRunListener} instance will be created for each run.
 * {@link SpringApplicationRunListener} to publish {@link SpringApplicationEvent}s.
 * SpringApplicationRunListener有7個方法
 * <p>
 * spring.factories
 * {@link org.springframework.core.io.support.SpringFactoriesLoader}
 *
 *
 * </p>
 * spring-boot-2.2.4.RELEASE.jar!\META-INF\spring.factories
 * spring-boot-autoconfigure-2.2.4.RELEASE.jar!\META-INF\spring.factories
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        registerJvmShutDownHook();

        SpringApplication springApplication = new SpringApplication(App.class);
        //
        springApplication.addListeners(new ApplicationFailedEventListener());

        final ConfigurableApplicationContext run = springApplication.run(args);

        System.exit(callSpringExit(run));
    }

    private static int callSpringExit(final ConfigurableApplicationContext run) {

        System.out.println("[LOG] before SpringApplication.exit(run)");
        final int exit = SpringApplication.exit(run);
        System.out.println("[LOG] after  SpringApplication.exit(run)");
        return exit;
    }

    private static void registerJvmShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (int i = 10; i > 0; i--) {
                try {
                    TimeUnit.NANOSECONDS.sleep(1 * 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("[LOG] callJvmShutDownHook " + i);
            }
        }));
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("[LOG] PreDestroy annotation bean !!!");
    }

}
