package tk.tommy.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;


@StaticMetamodel(EmployeeDetail.class)
public class EmployeeDetail_ {
    public static volatile SingularAttribute<EmployeeDetail, Long> id;
    public static volatile SingularAttribute<EmployeeDetail, String> name;
    public static volatile SingularAttribute<EmployeeDetail, String> phone;
    public static volatile SingularAttribute<EmployeeDetail, Integer> age;
}