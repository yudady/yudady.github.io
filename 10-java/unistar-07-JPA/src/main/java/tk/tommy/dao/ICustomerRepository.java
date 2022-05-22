package tk.tommy.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import tk.tommy.model.Customer;

// @Repository
public interface ICustomerRepository extends CrudRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
}