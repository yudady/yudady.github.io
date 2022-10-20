package tk.tommy.dao;

import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tk.tommy.dao.projection.BookPublicNameOnly;
import tk.tommy.dao.projection.BookPublicProjectingDTO;
import tk.tommy.model.Book;
import tk.tommy.model.BookPublic;

// @Repository
public interface IBookPublicRepository extends JpaRepository<BookPublic, Long>, JpaSpecificationExecutor<Book> {

    List<BookPublicNameOnly> findByName(String name);

    List<BookPublicProjectingDTO> queryByName(String name);


    @Query(value = "select new map(bp.id as idMap,bp.name as nameMap) from BookPublic bp where bp.name = ?1")
    Map<String, Object> getMap(String name);

    @Query(value = "select bp.name from BookPublic bp where bp.name = ?1")
    String getOneValue(String name);

    @Query(value = "select bp.name , bp.id from BookPublic bp where bp.name = ?1")
    Object[] getValues(String name);


}