package tk.tommy.model;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Job.class)
public class Job_ {
    public static volatile SingularAttribute<Job, Long> id;
    public static volatile SingularAttribute<Job, String> name;
    public static volatile SetAttribute<Job, Employee> employees;
}