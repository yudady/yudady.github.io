package tk.tommy;


import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.tommy.dao.IBpsBankParameterRepository;
import tk.tommy.model.BpsChannelParameter;

// @SpringBootApplication
public class BpsBankParameterApp implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BpsBankParameterApp.class);
    @Autowired
    private IBpsBankParameterRepository repository;

    public static void main(final String[] args) {
        SpringApplication.run(BpsBankParameterApp.class, args);
    }


    @Override
    public void run(final String... args) {

        log.info("StartApplication...");
        List<BpsChannelParameter> params = repository.findByBankId(1L);
        params.stream().forEach(System.out::println);
    }

}
