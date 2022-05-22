package tk.tommy.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tk.tommy.dao.projection.BookNamesOnly;
import tk.tommy.model.Book;

// @Repository
public interface IBookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findByName(String name);

    List<BookNamesOnly> findNameByName(String name);
}