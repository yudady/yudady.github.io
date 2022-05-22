package tk.tommy.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import tk.tommy.dao.IEmployeeRepository;
import tk.tommy.from.EmployeeSearch;
import tk.tommy.model.Employee;


import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan("tk.tommy")
class EmployeeServiceTest {

    @Autowired
    private IEmployeeRepository iemployeeRepository;
    @Autowired
    private EmployeeService employeeService;

    @Test
    public void testPageBySearch() throws Exception {
        assertNotNull(iemployeeRepository);
        assertNotNull(employeeService);
        EmployeeSearch employeeSearch = new EmployeeSearch(null, null, "程序猿");
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Page<Employee> employees = employeeService.pageBySearch(employeeSearch, PageRequest.of(0, 5, sort));
        assertNotNull(employees);

    }
}