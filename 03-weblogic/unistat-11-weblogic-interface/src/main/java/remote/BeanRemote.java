package remote;

import javax.ejb.Remote;

@Remote
public interface BeanRemote {
    String method();
}
