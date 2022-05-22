# JAX-RS


## [@Context](https://stackoverflow.com/questions/20937362/what-objects-can-i-inject-using-the-context-annotation/35868654#35868654)
> The @Context annotation can be used to inject 12 objects. Here is a quick summary of each of them

- HttpHeaders - HTTP header valuesand parameters
- UriInfo - URI query parameters and path variables
- SecurityContext - Gives access to security-related data for the given HTTP request
- Request - Allows precondition request processing
- ServletConfig - The ServletConfig
- ServletContext - The ServletContext
- HttpServletRequest - The HttpServletRequest instance for the request
- HttpServletResponse - The HttpServletResponse instance
- Application, Configuration, and Providers -> Provide information about the JAX-RS application, configuration and providers
- ResourceContext - Gives access to resource class instances

```java
@Path("/")
public class EndpointResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllHttpHeaders(final @Context HttpHeaders httpHeaders){
      // Code here that uses httpHeaders
  }
}
```

```java
@Path("/")
public class EndpointResource {

  private final @Context HttpHeaders httpHeaders;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllHttpHeaders(){
      // Code here that uses httpHeaders
  }
}
```




## [@BeanParam](https://stackoverflow.com/questions/38216487/should-i-use-queryparam-or-beanparam-in-jax-rs)
[@XXXParam](http://www.mastertheboss.com/jboss-frameworks/resteasy/resteasy-tutorial-part-two-web-parameters)
> The @BeanParam annotation was introduced in JAX-RS 2.0 as a parameter aggregator (which means it cannot be used in JAX-RS 1.0).
- @CookieParam
- @FormParam
- @HeaderParam
- @MatrixParam
- @PathParam
- @QueryParam
- @BeanParam 

> Map individual parameters to corresponding the method parameters:
```java
@GET
public String blah(@QueryParam("testParam") String testParam) {

}
```

> Map all parameters to the properties of a Java bean:
### [@BeanParam](https://dennis-xlc.gitbooks.io/restful-java-with-jax-rs-2-0-2rd-edition/en/part1/chapter5/bean_param.html)
```java
@Path("/users")
public class UserResource {

    @POST
    public void createCustomer(@BeanParam UserInput user){
        System.out.println(user);
    }

}


public class UserInput{

    @FormParam("name")
    private String name;

    @QueryParam("count")
    private int count;

    @HeaderParam("Content-Type")
    private String contentType;

    @Override
    public String toString() {
        return "UserInput{" +
                "name='" + name + '\'' +
                ", count=" + count +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
```


## [Java object conversion](https://dennis-xlc.gitbooks.io/restful-java-with-jax-rs-2-0-en/cn/part1/chapter5/common_functionality.html)
> Besides primitives, this string request data can be converted into a Java object before it is injected into your JAX-RS method parameter. 
> This object’s class must have a `constructor` or a static method named `valueOf()` that takes a single String parameter.
>  @MatrixParam , enum valueOf()
```java
public enum Color {
   BLACK,
   BLUE,
   RED,
   WHITE,
   SILVER
}

public class CarResource {

   @GET
   @Path("/{model}/{year}")
   @Produces("image/jpeg")
   public Jpeg getPicture(@PathParam("make") String make,
                          @PathParam("model") String model,
                          @MatrixParam("color") Color color) {
}
```







