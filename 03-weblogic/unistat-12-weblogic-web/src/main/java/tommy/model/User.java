package tommy.model;

import javax.ws.rs.FormParam;

public class User {

    @FormParam("email")
    public String email;
    @FormParam("password")
    public String password;

}