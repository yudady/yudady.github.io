package tk.tommy.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tk.tommy.model.BpsChannel;

// @Repository
public interface IBpsBankRepository extends JpaRepository<BpsChannel, Long>, JpaSpecificationExecutor<BpsChannel> {
    List<BpsChannel> findByBankName(String bankName);

    List<BpsChannel> findByPaymentTermNotNullOrderBySorting();

}
