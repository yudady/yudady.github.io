package tk.tommy.dao;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tk.tommy.model.BpsChannel;
import tk.tommy.model.to.ChannelTO;

@Repository
public interface IBpsBankRepository extends JpaRepository<BpsChannel, Long>, JpaSpecificationExecutor<BpsChannel> {
    List<BpsChannel> findByBankName(String bankName);

    List<BpsChannel> findByPaymentTermNotNullOrderBySorting();

    List<BpsChannel> findByPaymentTermNotNull(Sort sort);


    @Query("select NEW tk.tommy.model.to.ChannelTO(c.bankId , c.bankName , c.paymentTerm , c.provider) from BpsChannel c where c.paymentTerm is not null order by c.sorting")
    List<ChannelTO> findData();


}
