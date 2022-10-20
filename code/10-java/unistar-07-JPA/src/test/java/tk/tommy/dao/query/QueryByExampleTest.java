package tk.tommy.dao.query;

import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import tk.tommy.dao.IUserRepository;
import tk.tommy.model.Customer;
import tk.tommy.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QueryByExampleTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private IUserRepository userRepository;

    @BeforeEach
    public void before() {
        IntStream.range(0, 5).forEach(i -> {
            this.testEntityManager.getEntityManager().persist(new User("tommy-" + i, 10 + i));
        });
        IntStream.range(0, 3).forEach(i -> {
            this.testEntityManager.getEntityManager().persist(new Customer("lin-" + i, "tommy-" + i));
        });
    }

    @Test
    public void test01() {
        User user = new User();
        user.name = "tommy-1";

        Example<User> example = Example.of(user);

        userRepository.findAll(example)
            .forEach(System.out::println);
    }

    @Test
    public void test02() {
        User user = new User();
        user.name = "tommy";
        ExampleMatcher matcher = ExampleMatcher.matching()
            .withIgnorePaths("create")
            // .withMatcher("name", startsWith().ignoreCase())
            .withMatcher("name", match -> match.contains());

        //


        Example<User> example = Example.of(user, matcher);
        userRepository.findAll(example)
            .forEach(System.out::println);
    }
}
