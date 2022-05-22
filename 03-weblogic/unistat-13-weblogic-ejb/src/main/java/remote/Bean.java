package remote;

import java.io.Serializable;
import javax.ejb.Stateless;

@Stateless
public class Bean implements BeanRemote, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public String method() {
        return "method";
    }

}
