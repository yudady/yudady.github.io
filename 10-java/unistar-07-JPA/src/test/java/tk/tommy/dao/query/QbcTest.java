package tk.tommy.dao.query;

import java.util.List;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tk.tommy.model.Customer;
import tk.tommy.model.User;
import tk.tommy.model.UserTo;
import tk.tommy.model.User_;

/**
 * https://www.ibm.com/developerworks/cn/java/j-typesafejpa/
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QbcTest {

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

    @Test
    public void testCriteria01() {
        final EntityManager em = testEntityManager.getEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        query.select(root).where(
            builder.and(
                builder.equal(root.get("id"), 2L)
            )
        );
        User user = em.createQuery(query).getSingleResult();
        System.out.println("[LOG] user = " + user);

    }

    @Test
    public void testCriteria02() {
        final EntityManager em = testEntityManager.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> userRoot = query.from(User.class);
        query.select(userRoot);
        TypedQuery<User> q = em.createQuery(query);
        List<User> allitems = q.getResultList();
        allitems.forEach(System.out::println);
    }

    @Test
    public void testCriteria03() {
        final EntityManager em = testEntityManager.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<User> userRoot = query.from(User.class);
        query.select(userRoot.get("name"));
        TypedQuery<String> q = em.createQuery(query);
        List<String> allitems = q.getResultList();
        allitems.forEach(System.out::println);
    }


    /**
     * Statement
     */
    @Test
    public void testCriteria03_1() {
        final EntityManager em = testEntityManager.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        //
        Root<User> userRoot = query.from(User.class);
        // sql
        query.select(userRoot.get(User_.id))
            .where(cb.lessThan(userRoot.get(User_.id), 4L));
        // query
        em.createQuery(query)
            .getResultList()
            .forEach(System.out::println);
    }

    /**
     * PreparedStatement
     */
    @Test
    public void testCriteria03_2() {
        final EntityManager em = testEntityManager.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        //
        Root<User> userRoot = query.from(User.class);
        ParameterExpression<Long> idExpr = cb.parameter(Long.class);
        final Path<Long> idPath = userRoot.get(User_.id);
        final Predicate condition1 = cb.lt(idPath, idExpr); // 不包含
        final Predicate condition2 = cb.le(idPath, idExpr); // 包含
        final Predicate condition3 = cb.lessThan(idPath, idExpr);
        // sql
        final CriteriaQuery<User> select = query.select(userRoot);
        select.where(condition1, condition2, condition3);
        // query
        em.createQuery(query)
            .setParameter(idExpr, 4L)
            .getResultList()
            .forEach(System.out::println);
    }

    /**
     * 查询投影 UserTo , cb.construct
     */
    @Test
    public void testCriteria04() {
        final EntityManager em = testEntityManager.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserTo> query = cb.createQuery(UserTo.class);
        Root<User> from = query.from(User.class);
        query.select(cb.construct(UserTo.class, from.get("id"), from.get("name")));
        TypedQuery<UserTo> q = em.createQuery(query);
        List<UserTo> allitems = q.getResultList();
        allitems.forEach(System.out::println);
    }

    /**
     * use annotation StaticMetamodel(User.class)
     * <p>
     * 查询投影 UserTo , cb.construct
     */
    @Test
    public void testCriteria04_1() {
        final EntityManager em = testEntityManager.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserTo> query = cb.createQuery(UserTo.class);
        Root<User> from = query.from(User.class);
        query.select(cb.construct(UserTo.class, from.get(User_.id), from.get(User_.name)));
        TypedQuery<UserTo> q = em.createQuery(query);
        List<UserTo> allitems = q.getResultList();
        allitems.forEach(System.out::println);
    }

    /**
     * 返回元组(Tuple)
     */
    @Test
    public void testCriteria06() {
        final EntityManager em = testEntityManager.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<User> from = query.from(User.class);

        // column
        final Selection<Object> id = from.get("id").alias("id");
        final Selection<Object> name = from.get("name").alias("name");

        // where
        Predicate like = cb.like(from.get("name"), "tommy%");

        query.select(cb.tuple(id, name))
            .where(like)
        ;

        TypedQuery<Tuple> q = em.createQuery(query);

        List<Tuple> allitems = q.getResultList();
        allitems.forEach(tuple -> {
            final UserTo userTo = new UserTo(
                tuple.get("id", Long.class),
                tuple.get("name", String.class));
            System.out.println("[LOG] userTo = " + userTo);
        });
    }

    @Test
    public void testCriteria07() {
        final EntityManager em = testEntityManager.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<User> from = query.from(User.class);
        final Selection<Object> id = from.get("id").alias("id");
        final Selection<Object> name = from.get("name").alias("name");
        Predicate like = cb.like(from.get("name"), "tommy%");
        query.select(cb.tuple(id, name))
            .where(like)
        ;

        TypedQuery<Tuple> q = em.createQuery(query);

        //设置分页参数 select c from native_user c where c.user_name like ? limit ?
        q.setFirstResult(0);
        q.setMaxResults(4);

        List<Tuple> allitems = q.getResultList();
        allitems.forEach(tuple -> {
            final UserTo userTo = new UserTo(
                tuple.get("id", Long.class),
                tuple.get("name", String.class));
            System.out.println("[LOG] userTo = " + userTo);
        });
    }

    @Test
    public void testCriteriaError() {
        final EntityManager em = testEntityManager.getEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = builder.createQuery(Object[].class);
        Root<User> userRoot = criteriaQuery.from(User.class);
        Root<Customer> customerRoot = criteriaQuery.from(Customer.class);

        // 因為entity沒有join，所以報錯
        final Predicate equal = builder.equal(userRoot.get("id"), customerRoot.get("id"));

        criteriaQuery.where(equal);

        TypedQuery<Object[]> query = em.createQuery(criteriaQuery);
        List<Object[]> list = query.getResultList();
        for (Object[] objects : list) {
            User user = (User) objects[0];
            Customer customer = (Customer) objects[1];
            System.out.println("user NAME=" + user.name + "\t customer NAME=" + customer.firstName);
        }
    }
}