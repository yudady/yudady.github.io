package tk.tommy.model;

import javax.persistence.CascadeType;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @OneToOne(cascade = CascadeType.ALL) // 拥有级联维护的一方，参考http://westerly-lzh.github.io/cn/2014/12/JPA-CascadeType-Explaining/
    @JoinColumn(foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    public EmployeeDetail detail;

    @ManyToOne(fetch = FetchType.LAZY) // 默认 lazy ，通过懒加载，知道需要使用级联的数据，才去数据库查询这个数据，提高查询效率。
    // 设置外键的问题，参考http://mario1412.github.io/2016/06/27/JPA%E4%B8%AD%E5%B1%8F%E8%94%BD%E5%AE%9E%E4%BD%93%E9%97%B4%E5%A4%96%E9%94%AE/
    @JoinColumn(name = "jobId", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    public Job job;
}