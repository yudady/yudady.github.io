package tk.tommy.springdatajpa.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Employee {
    @Id
    public String empId;

    public String name;

    public Integer age;

    public LocalDate createDate;

    public LocalDateTime updateTime;

}