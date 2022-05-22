package tk.tommy.dao;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tk.tommy.model.Book;


import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IBookRepository repository;

    @BeforeEach
    public void before() throws Exception {
        this.entityManager.persist(new Book("C++"));
        this.entityManager.persist(new Book("Java"));
        this.entityManager.persist(new Book("Node"));
        this.entityManager.persist(new Book("Python"));
    }

    @Test
    public void testFindByName() {
        final List<Book> books = this.repository.findByName("C++");
        System.out.println("[LOG] books " + books.size());
        assertEquals(1, books.size());
    }

    @Test
    public void testFindAll() {
        final List<Book> books = this.repository.findAll();
        System.out.println("[LOG] books " + books.size());
        assertEquals(4, books.size());
    }
}