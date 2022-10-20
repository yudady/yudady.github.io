package tk.tommy.dao.query;

import java.util.List;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tk.tommy.model.Customer;
import tk.tommy.model.User;
import tk.tommy.model.UserTJoinCustomer;
import tk.tommy.model.UserTo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class HqlTest {

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


    /**
     * hql是hibernate造出来的对象sql语言，只有hibernate框架能够解析，并将其通过一系列的映射转换，拼凑成sql语言。
     * jpql是EJB3.0中的JPA造出来的对象查询语言
     */
    @Test
    public void testJpal01() {
        //定义sql语句
        String sql = "FROM User where id = 1";
        //获得Query对象
        final EntityManager em = testEntityManager.getEntityManager();

        Query query = em.createQuery(sql);
        //设置参数
        query.setParameter(1, 2L);
        final List resultList = query.getResultList();
        resultList.forEach(System.out::println);
    }

    @Test
    public void testJpal02() {
        //定义sql语句
        String sql = "from User where id = ?1";
        //获得Query对象
        final EntityManager em = testEntityManager.getEntityManager();

        em.createQuery(sql, User.class)
            .setParameter(1, 2L)
            .getResultList()
            .forEach(System.out::println);

    }

    /**
     * ==================================
     * ======= TypedQuery ===============
     * ==================================
     */
    @Test
    public void testJpal03() {
        final EntityManager em = testEntityManager.getEntityManager();
        TypedQuery<UserTo> q = em.createQuery(
            "SELECT new tk.tommy.model.UserTo(u.id, u.name) from User u where u.name like :name", UserTo.class);
        q.setParameter("name", "tommy%");
        List<UserTo> users = q.getResultList();

        for (UserTo u : users) {
            System.out.println("[LOG] u = " + u);
        }
    }

    @Test
    public void testJpalJoin01() {
        final EntityManager em = testEntityManager.getEntityManager();
        TypedQuery<UserTJoinCustomer> q = em.createQuery(
            "SELECT new tk.tommy.model.UserTJoinCustomer(u.id, u.name , c.firstName) "
                + "from User u inner JOIN Customer c on u.id = c.id", UserTJoinCustomer.class);
        List<UserTJoinCustomer> users = q.getResultList();

        for (UserTJoinCustomer u : users) {
            System.out.println("[LOG] u = " + u);
        }
    }

    /**
     * ==================================
     * ======= Tuple ===============
     * ==================================
     */

    @Test
    public void testJpalJoinTuple01() {
        final EntityManager em = testEntityManager.getEntityManager();
        TypedQuery<Tuple> q = em.createQuery(
            "SELECT u.id, u.name , c.firstName "
                + "from User u inner JOIN Customer c on u.id = c.id", Tuple.class);
        List<Tuple> users = q.getResultList();

        for (Tuple u : users) {
            System.out.print("[LOG] id        = " + u.get(0, Long.class));
            System.out.print("[LOG] name      = " + u.get(1, String.class));
            System.out.println("[LOG] firstName = " + u.get(2, String.class));
        }
    }

    @Test
    public void testJpalJoinTuple02() {
        final EntityManager em = testEntityManager.getEntityManager();
        TypedQuery<Tuple> q = em.createQuery(
            "SELECT u.id as id, u.name as name, c.firstName as ttt "
                + "from User u inner JOIN Customer c on u.id = c.id", Tuple.class);
        List<Tuple> users = q.getResultList();

        for (Tuple u : users) {
            System.out.print("[LOG] id        = " + u.get("id", Long.class));
            System.out.print("[LOG] name      = " + u.get("name", String.class));
            System.out.println("[LOG] firstName = " + u.get("ttt", String.class));
        }
    }
}