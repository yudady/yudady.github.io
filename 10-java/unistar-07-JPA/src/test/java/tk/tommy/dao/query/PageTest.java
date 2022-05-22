package tk.tommy.dao.query;

import java.util.stream.IntStream;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import tk.tommy.dao.IUserRepository;
import tk.tommy.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PageTest {

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
        userRepository.findByNameStartingWith("tommy", Sort.by(Sort.Direction.DESC, "name"))
            .forEach(System.out::println);
    }

    @Test
    public void test02() {
        final Page<User> userPage = userRepository.findAll(PageRequest.of(1, 20));
        System.out.println("[LOG] userPage.getTotalElements() = " + userPage.getTotalElements());
        userPage.get().forEach(System.out::println);
    }

    @Test
    public void test03() {
        Pageable pageable = PageRequest.of(0, 3, Sort.Direction.DESC, "name");
        final Page<User> userPage = userRepository.findAll(pageable);
        System.out.println("[LOG] userPage.getTotalElements() = " + userPage.getTotalElements());
        userPage.get().forEach(System.out::println);
        final String str = ToStringBuilder.reflectionToString(userPage, ToStringStyle.MULTI_LINE_STYLE);
        System.out.println("[LOG] str = " + str);
    }


}