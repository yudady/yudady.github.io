package com.tommy.response;

import com.tommy.model.BpsBank;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<BpsBank, Long>, JpaSpecificationExecutor<BpsBank> {

    List<BpsBank> findByBankName(String name);


}