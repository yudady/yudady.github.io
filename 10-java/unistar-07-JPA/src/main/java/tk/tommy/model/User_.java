package tk.tommy.model;

import java.time.LocalDateTime;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;


@StaticMetamodel(User.class)
public class User_ {
    public static volatile SingularAttribute<User, Long> id;
    public static volatile SingularAttribute<User, String> name;
    public static volatile SingularAttribute<User, Integer> age;
    public static volatile SingularAttribute<User, LocalDateTime> create;
    public static volatile SingularAttribute<User, LocalDateTime> modified;
}