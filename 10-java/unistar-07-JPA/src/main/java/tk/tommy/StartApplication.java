package tk.tommy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StartApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StartApplication.class);

    public static void main(final String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

    @Override
    public void run(final String... args) {
        log.info("StartApplication...");
    }

}
