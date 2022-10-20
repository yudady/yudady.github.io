package tk.tommy.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import tk.tommy.model.Employee;
import tk.tommy.model.Job;


// @Repository
public interface JobRepository extends JpaSpecificationExecutor<Job>, PagingAndSortingRepository<Employee, Long> {
}