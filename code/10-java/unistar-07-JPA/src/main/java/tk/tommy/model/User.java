package tk.tommy.model;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "native_user")
public class User {
    /**
     * 用户id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //使用主键自动增长
    @Column(name = "user_id")
    public Long id;

    /**
     * 用户名
     */
    @Column(name = "user_name", unique = true)
    public String name;

    /**
     * 用户年龄
     */
    @Column(name = "user_age")
    public Integer age;

    /**
     * 记录创建时间
     */
    @Column(name = "gmt_create")
    public LocalDateTime create;

    /**
     * 记录最后一次修改时间
     */
    @Column(name = "gmt_modified")
    public LocalDateTime modified;

    public User() {
    }

    public User(final String name, final int age) {
        this.name = name;
        this.age = age;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("name='" + name + "'")
            .add("age=" + age)
            .add("create=" + create)
            .add("modified=" + modified)
            .toString();
    }
}