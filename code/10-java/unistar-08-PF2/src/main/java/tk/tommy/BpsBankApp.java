package tk.tommy;


import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import tk.tommy.dao.IBpsBankRepository;
import tk.tommy.model.BpsChannel;
import tk.tommy.model.to.ChannelTO;

// @SpringBootApplication
public class BpsBankApp implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BpsBankApp.class);

    public static void main(final String[] args) {
        SpringApplication.run(BpsBankApp.class, args);
    }

    @Autowired
    private IBpsBankRepository iBpsBankRepository;

    @Override
    public void run(final String... args) {

        log.info("StartApplication...");
        final List<BpsChannel> byBankName = this.iBpsBankRepository.findByPaymentTermNotNullOrderBySorting();
        System.out.println("[LOG] byBankNames " + byBankName.size());

        byBankName.stream().forEach(System.out::println);

        System.out.println("[LOG] ===================== ");
        final List<ChannelTO> data = this.iBpsBankRepository.findData();
        data.stream().forEach(System.out::println);
        log.info("StartApplication... done");
    }

}
