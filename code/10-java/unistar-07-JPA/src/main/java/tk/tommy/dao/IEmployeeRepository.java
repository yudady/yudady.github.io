package tk.tommy.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import tk.tommy.model.Employee;

// @Repository
public interface IEmployeeRepository extends JpaSpecificationExecutor<Employee>, PagingAndSortingRepository<Employee, Long> {
}