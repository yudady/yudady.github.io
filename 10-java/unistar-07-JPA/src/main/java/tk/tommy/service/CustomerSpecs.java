package tk.tommy.service;

import org.springframework.data.jpa.domain.Specification;
import tk.tommy.model.Customer;

public class CustomerSpecs {

    public static Specification<Customer> isLongTermCustomer() {
        return (root, query, builder) -> {
            return builder.equal(root.get("lastName"), "tommy");
        };
    }

}