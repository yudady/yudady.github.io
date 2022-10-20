package tk.tommy.dao;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tk.tommy.model.BpsChannel;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ChannelRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IBpsBankRepository iBpsBankRepository;

    @BeforeEach
    public void before() throws Exception {
        this.entityManager.persist(new BpsChannel(1L, "Java"));
        this.entityManager.persist(new BpsChannel(2L, "Java 2"));
    }

    @Test
    public void testBpsBank() {
        final List<BpsChannel> byBankName = this.iBpsBankRepository.findByBankName("Java");
        System.out.println("[LOG] byBankNames " + byBankName.size());
    }

    @Test
    public void testFindAll() {
        final List<BpsChannel> byBankName = this.iBpsBankRepository.findAll();
        System.out.println("[LOG] byBankNames " + byBankName.size());
    }

}