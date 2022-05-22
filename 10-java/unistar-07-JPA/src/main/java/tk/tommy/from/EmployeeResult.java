package tk.tommy.from;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tk.tommy.model.Employee;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResult extends Employee {
    public String name;
    public Integer age;
}