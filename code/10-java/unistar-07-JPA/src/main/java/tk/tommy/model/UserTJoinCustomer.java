package tk.tommy.model;

import java.util.StringJoiner;

public class UserTJoinCustomer {
    public final Long id;
    public final String name;
    public final String firstName;

    public UserTJoinCustomer(final Long id, final String name, final String firstName) {
        this.id = id;
        this.name = name;
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserTJoinCustomer.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("name='" + name + "'")
            .add("firstName='" + firstName + "'")
            .toString();
    }
}
