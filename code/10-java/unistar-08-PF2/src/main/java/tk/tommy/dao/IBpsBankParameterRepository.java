package tk.tommy.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tk.tommy.model.BpsChannelParameter;

@Repository
public interface IBpsBankParameterRepository extends JpaRepository<BpsChannelParameter, Long> {


    List<BpsChannelParameter> findByBankId(Long bankId);
}
