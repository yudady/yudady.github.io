package tk.tommy.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tk.tommy.dao.projection.BookNamesOnly;
import tk.tommy.dao.projection.BookPublicNameOnly;
import tk.tommy.dao.projection.BookPublicProjectingDTO;
import tk.tommy.model.BookPublic;


import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProjectionInterfaceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IBookRepository repository;

    @Autowired
    private IBookPublicRepository bookPublicRepository;

    @BeforeEach
    public void before() throws Exception {
        final BookPublic bookPublic = new BookPublic();
        bookPublic.name = "C++";
        this.entityManager.persist(bookPublic);
    }


    @Test
    public void testFindByName1() {
        final List<BookNamesOnly> books = this.repository.findNameByName("C++");
        System.out.println("[LOG] books " + books.size());
        assertEquals(1, books.size());
    }

    @Test
    public void testFindByName2() {
        final List<BookPublicNameOnly> books = this.bookPublicRepository.findByName("C++");
        System.out.println("[LOG] books " + books.size());
        books.forEach(e -> {
            System.out.println("[LOG] e.getName() = " + e.getName());
        });
        assertEquals(1, books.size());
    }

    @Test
    public void testFindByName3() {
        final List<BookPublicProjectingDTO> books = this.bookPublicRepository.queryByName("C++");
        System.out.println("[LOG] books " + books.size());
        books.forEach(e -> {
            System.out.println("[LOG] e.getName() = " + e.name);
        });
        assertEquals(1, books.size());
    }

    @Test
    public void testFindByName4() {
        final Map<String, Object> map = this.bookPublicRepository.getMap("C++");
        System.out.println("[LOG] books " + map.get("idMap") + " " + map.get("nameMap") + "  idMap1:" + map.get("idMap1"));


        final String oneValue = this.bookPublicRepository.getOneValue("C++");
        System.out.println("[LOG] oneValue = " + oneValue);
        final Object[] values = this.bookPublicRepository.getValues("C++");

        System.out.println("[LOG] values[i] = " + Arrays.deepToString(values));

    }


}