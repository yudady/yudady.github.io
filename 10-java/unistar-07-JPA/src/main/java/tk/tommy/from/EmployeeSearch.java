package tk.tommy.from;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeSearch {
    public Long employeeId;
    public String employeeName;
    public String jobName;
}