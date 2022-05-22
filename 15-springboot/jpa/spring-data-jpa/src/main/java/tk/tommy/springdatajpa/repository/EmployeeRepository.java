package tk.tommy.springdatajpa.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tk.tommy.springdatajpa.model.Employee;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    // query methods
}