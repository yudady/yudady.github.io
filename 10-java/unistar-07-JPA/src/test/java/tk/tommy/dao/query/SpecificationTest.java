package tk.tommy.dao.query;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tk.tommy.dao.IUserRepository;
import tk.tommy.model.Customer;
import tk.tommy.model.User;
import tk.tommy.model.User_;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SpecificationTest {


    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private IUserRepository userRepository;

    @BeforeEach
    public void before() {
        for (int i = 0; i < 5; i++) {
            this.testEntityManager.getEntityManager().persist(new User("tommy" + i, i + 10));
        }
    }


    @Test
    public void test01() {
        userRepository.findAll((root, query, builder)
            -> builder.like(root.get(User_.name), "tommy%")
        ).forEach(System.out::println);
    }

    @Test
    public void test02() {
        userRepository.findAll((root, query, builder) -> {
                final Predicate like = builder.like(root.get(User_.name), "tommy%");
                return like;
            }
        ).forEach(System.out::println);
    }

    @Test
    public void test03() {
        userRepository.findAll((root, query, cb) -> {

                Predicate predicateId = cb.lt(root.get(User_.id), 3L);
                Predicate predicateName = cb.isNotNull(root.<User>get("name"));

                Predicate endPredicate = cb.and(predicateId, predicateName);
                return endPredicate;
            }
        ).forEach(System.out::println);
    }

    @Test
    public void test04() {
        userRepository.findAll((root, query, cb) -> {

                Predicate predicateId = cb.lt(root.get(User_.id), 3L);
                Predicate predicateName = cb.like(root.get(User_.name), "tommy%");

                Predicate endPredicate = cb.and(predicateId, predicateName);
                return endPredicate;
            }
        ).forEach(System.out::println);
    }


    @Test
    public void test05() {

        userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {

                criteriaQuery.orderBy(criteriaBuilder.desc(root.get(User_.id)));
                // final ParameterExpression<Long> idParameter = criteriaBuilder.parameter(Long.class);
                // Predicate predicateId = criteriaBuilder.lt(root.get(User_.id), idParameter);

                Predicate predicateId = criteriaBuilder.lt(root.get(User_.id), 3);
                return criteriaBuilder.and(predicateId);
            }
        ).forEach(System.out::println);
    }

    @Test
    public void testJoinError() {
        // because table not join
        userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
                Join<User, Customer> owners = root.join("id");
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
                return criteriaBuilder.gt(owners.get("id"), 0L);
            }
        ).forEach(System.out::println);
    }
}
