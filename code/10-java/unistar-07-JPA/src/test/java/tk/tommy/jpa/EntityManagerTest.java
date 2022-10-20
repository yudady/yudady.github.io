package tk.tommy.jpa;

import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tk.tommy.model.Customer;
import tk.tommy.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EntityManagerTest {
    @Autowired
    private TestEntityManager testEntityManager;


    @BeforeEach
    public void before() {
        IntStream.range(0, 5).forEach(i -> {
            this.testEntityManager.getEntityManager().persist(new User("tommy-" + i, 10 + i));
        });
        IntStream.range(0, 3).forEach(i -> {
            this.testEntityManager.getEntityManager().persist(new Customer("lin-" + i, "tommy-" + i));
        });
    }

    public void test01() {
        final EntityManager entityManager = this.testEntityManager.getEntityManager();
    }

}
