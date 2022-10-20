package tk.tommy.dao.query;

import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tk.tommy.dao.IUserRepository;
import tk.tommy.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LikeTest {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private IUserRepository userRepository;


    @BeforeEach
    public void before() {
        IntStream.range(0, 50).forEach(i -> {
            this.testEntityManager.getEntityManager().persist(new User("tommy-" + i, 10 + i));
        });
    }


    @Test
    public void test01() {
        userRepository.findByNameStartingWith("tommy")
            .forEach(System.out::println);
    }

    @Test
    public void test02() {
        userRepository.findByNameEndsWith("3")
            .stream().forEach(System.out::println);
    }

    @Test
    public void test03() {
        userRepository.findByNameContaining("ommy-1")
            .stream().forEach(System.out::println);
    }

    @Test
    public void test04() {
        userRepository.findByNameLike("%ommy-1")
            .stream().forEach(System.out::println);
    }

}