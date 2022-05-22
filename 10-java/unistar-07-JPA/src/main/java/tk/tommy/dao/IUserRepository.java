package tk.tommy.dao;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tk.tommy.model.User;

// @Repository
public interface IUserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {


    List<User> findByNameStartingWith(String name);

    List<User> findByNameEndsWith(String name);

    List<User> findByNameContaining(String name);

    List<User> findByNameLike(String name);


    List<User> findByNameStartingWith(String name, Sort sort);

    Page<User> findByNameStartingWith(String name, Pageable pageable);
}