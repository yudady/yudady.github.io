package tk.tommy.model;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Job  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;

    @OneToMany(targetEntity = Employee.class, mappedBy = "job") // mappedBy 只有在双向关联的时候设置，表示关系维护的一端，否则会生成中间表A_B
    @org.hibernate.annotations.ForeignKey(name = "none") // 注意这里不能使用 @JoinColumn 不然会生成外键
    public Set<Employee> employees;
}