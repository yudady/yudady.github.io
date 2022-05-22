package tk.tommy.model;

import java.util.StringJoiner;

public class UserTo {
    public final Long id;
    public final String name;

    public UserTo(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserTo.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("name='" + name + "'")
            .toString();
    }
}
