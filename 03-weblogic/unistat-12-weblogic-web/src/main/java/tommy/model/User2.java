package tommy.model;

import java.util.StringJoiner;
import javax.ws.rs.QueryParam;

public class User2 {
    @QueryParam("email")
    public String email;
    @QueryParam("password")
    public String password;

    @Override
    public String toString() {
        return new StringJoiner(", ", User2.class.getSimpleName() + "[", "]")
            .add("email='" + email + "'")
            .add("password='" + password + "'")
            .toString();
    }
}