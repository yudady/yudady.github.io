package com.tommy.jpa;

import com.tommy.model.BpsBank;
import com.tommy.response.BookRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


/**
 * before spring boot 2.1 use @SpringBootTest  + @DataJpaTest
 * after spring boot 2.2 use  @DataJpaTest + @AutoConfigureTestDatabase
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {


    @Autowired
    private BookRepository repository;


    @Test
    public void testFindByName() {
        final List<BpsBank> byName = this.repository.findByBankName("C++");
        System.out.println("[LOG] books " + byName.size());
    }

}