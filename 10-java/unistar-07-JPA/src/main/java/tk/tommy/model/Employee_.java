package tk.tommy.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;


@StaticMetamodel(Employee.class)
public class Employee_ {
    public static volatile SingularAttribute<Employee, Long> id;
    public static volatile SingularAttribute<Employee, EmployeeDetail> detail;
    public static volatile SingularAttribute<Employee, Job> job;
}