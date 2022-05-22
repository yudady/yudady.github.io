package tommy;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import tommy.model.User;
import tommy.model.User2;

@Path("/login")
public class LoginResource {
    @POST
    @Path("/form-param")
    @Consumes("application/x-www-form-urlencoded")
    public String login(@FormParam("email") String e, @FormParam("password") String p) {
        return "FormParam with " + e + " " + p;
    }

    @POST
    @Path("/bean-param")
    @Consumes("application/x-www-form-urlencoded")
    public String login(@BeanParam User form) {
        return "BeanParam with " + form.email + " " + form.password;
    }

    @GET
    @Path("/path-param/{zip}")
    public String pathParam(@PathParam("zip") String id) {
        return "PathParam Id is " + id;
    }

    @GET
    @Path("/query-param")
    public String queryParam(@QueryParam("zip") String id) {
        return "QueryParam Id is " + id;
    }

    @GET
    @Path("/matrix-param")
    public String matrixParam(@MatrixParam("email") String email,
                              @MatrixParam("password") String password) {

        return "MatrixParam with " + email + " password:" + password;
    }

    @GET
    @Path("/bean-param")
    public void createCustomer(@BeanParam User2 user) {
        System.out.println(user);
    }
}