package tk.tommy.springdatajpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.tommy.springdatajpa.model.Employee;
import tk.tommy.springdatajpa.repository.EmployeeRepository;

@SpringBootApplication
public class SpringDataJpaApplication implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(SpringDataJpaApplication.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJpaApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("服務已啟動，執行command line runner。");

        Employee employee = new Employee();
        employee.empId = LocalDateTime.now().toString();
        employee.name = "tommy-" + LocalDateTime.now().toString();
        employee.age = 1;
        employee.createDate = LocalDate.now();
        employee.updateTime = LocalDateTime.now();
        employeeRepository.save(employee);

    }
}
