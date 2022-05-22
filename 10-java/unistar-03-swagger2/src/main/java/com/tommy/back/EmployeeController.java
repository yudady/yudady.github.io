package com.tommy.back;

import com.tommy.model.Employee;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1")
@Api(value = "Employee Management System", description = "Operations pertaining to employee in Employee Management System")
public class EmployeeController {


    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation(value = "View a list of available employees", response = List.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved list"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {

        final String uri = "https://howtodoinjava.com/spring-boot2/resttemplate/resttemplate-httpclient-java-config/";

        String result = restTemplate.getForObject(uri, String.class);
        System.out.println("[LOG]  " + result);
        return null;
    }

    @ApiOperation(value = "Get an employee by Id")
    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(
        @ApiParam(value = "Employee id from which employee object will retrieve", required = true)
        @PathVariable(value = "id") Long employeeId) {
        return null;
    }

    @ApiOperation(value = "Add an employee")
    @PostMapping("/employees")
    public Employee createEmployee(
        @ApiParam(value = "Employee object store in database table", required = true)
        @Valid @RequestBody Employee employee) {
        return null;
    }

    @ApiOperation(value = "Update an employee")
    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> updateEmployee(
        @ApiParam(value = "Employee Id to update employee object", required = true)
        @PathVariable(value = "id") Long employeeId,
        @ApiParam(value = "Update employee object", required = true)
        @Valid @RequestBody Employee employeeDetails) {
        return null;
    }

    @ApiOperation(value = "Delete an employee")
    @DeleteMapping("/employees/{id}")
    public Map<String, Boolean> deleteEmployee(
        @ApiParam(value = "Employee Id from which employee object will delete from database table", required = true)
        @PathVariable(value = "id") Long employeeId) {
        return null;
    }
}
