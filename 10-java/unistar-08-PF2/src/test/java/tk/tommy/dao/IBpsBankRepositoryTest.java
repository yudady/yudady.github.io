package tk.tommy.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tk.tommy.App;
import tk.tommy.model.BpsChannel;
import tk.tommy.model.to.ChannelTO;
import tk.tommy.model.to.DepositPaymentTerm;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = App.class)
class IBpsBankRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private IBpsBankRepository iBpsBankRepository;

    /**
     * https://www.cnblogs.com/rulian/p/6533109.html
     * 局限性
     * 1、支持动态查询:没有输入值，就忽略这个过滤条件
     * 2、不支持过滤条件分组。即不支持过滤条件用 or(或) 来连接，所有的过滤查件，都是简单一层的用 and(并且) 连接。
     * 3、仅支持字符串的开始/包含/结束/正则表达式匹配 和 其他属性类型的精确匹配。
     */
    @Test
    public void testPojo() {
        // JPQL
        final String hql = "select NEW tk.tommy.model.to.ChannelTO(p.bankId , p.bankName , p.paymentTerm , p.provider) FROM BpsChannel p where p.paymentTerm is not null and p.bankId > :bankId";
        final TypedQuery<ChannelTO> query = this.entityManager.createQuery(hql, ChannelTO.class);
        query.setParameter("bankId", 1000L);
        final List<ChannelTO> list = query.getResultList();
        list.stream().forEach(System.out::println);
    }

    @Test
    public void testExampleMatcher() {
        // A、实体对象
        final BpsChannel person = new BpsChannel();
        person.setBankName("中国");
        final BpsChannel channel = new BpsChannel();
        channel.setBankName("中国");
        channel.setPaymentTerm(DepositPaymentTerm.OFFLINE);

        // B、匹配器
        final ExampleMatcher matcher = ExampleMatcher.matching()
            .withMatcher("bankName", contains())
            // .withIgnorePaths("provider", "paymentTerm")
            // .withIncludeNullValues()
            ;

        final Example<BpsChannel> example = Example.of(channel, matcher);
        final Example<BpsChannel> example2 = Example.of(channel);
        final Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "bankId"));

        final List<BpsChannel> all = this.iBpsBankRepository.findAll(example);

        System.out.println("[LOG] all ");
        all.stream().forEach(System.out::println);
        System.out.println("[LOG] Page ");
        final Page<BpsChannel> all1 = this.iBpsBankRepository.findAll(example, pageable);
        all1.get().forEach(System.out::println);
    }

    @Test
    void init() {
        assertThat(this.entityManager).isNotNull();
        assertThat(this.iBpsBankRepository).isNotNull();
    }

    // BpsChannel{bankId=3, bankName='中国建设银行', bankCode='3', displayName='CCB', sorting=30, paymentTerm=OFFLINE, provider=PED}
    // BpsChannel{bankId=4, bankName='中国农业银行', bankCode='4', displayName='ABC', sorting=40, paymentTerm=OFFLINE, provider=PED}
    // BpsChannel{bankId=5, bankName='中国银行', bankCode='5', displayName='BOC', sorting=50, paymentTerm=OFFLINE, provider=PED}
    // BpsChannel{bankId=7, bankName='中国民生银行', bankCode='7', displayName='CMBC', sorting=70, paymentTerm=OFFLINE, provider=PED}
    // BpsChannel{bankId=10, bankName='中国邮政', bankCode='10', displayName='PSBC', sorting=100, paymentTerm=OFFLINE, provider=PED}
    // BpsChannel{bankId=11, bankName='中国光大银行', bankCode='11', displayName='CEB', sorting=110, paymentTerm=OFFLINE, provider=PED}
    // BpsChannel{bankId=1004, bankName='快充-中国银行', bankCode='5', displayName='BOC', sorting=51, paymentTerm=THIRD_PARTY, provider=PED}

    @Test
    public void testSpecificaiton() {
        this.iBpsBankRepository.findAll(this.predicate_and()).stream().forEach(System.out::println);
        System.out.println("========================");

        // Defining sort expressions using the type-safe API
        final Sort.TypedSort<BpsChannel> typedSort = Sort.sort(BpsChannel.class);
        final Sort sort = typedSort.by(BpsChannel::getBankId).ascending()
            .and(typedSort.by(BpsChannel::getBankName).descending());

        this.iBpsBankRepository.findByPaymentTermNotNull(sort).stream().forEach(System.out::println);
        System.out.println("========================");
        final Pageable sortedByBankId = PageRequest.of(0, 3, sort);
        this.iBpsBankRepository.findAll(this.predicate_or(), sortedByBankId).stream().forEach(System.out::println);
        System.out.println("========================");
        this.iBpsBankRepository.findAll(this.predicate_conjunction(), sortedByBankId).stream().forEach(System.out::println);
        System.out.println("========================");
    }

    private Specification<BpsChannel> predicate_and() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            final Path<String> bankName = root.get("bankName");
            final Predicate p1 = criteriaBuilder.like(bankName, "%中国%");
            //
            final Path<Long> bankId = root.get("bankId");
            final Predicate p2 = criteriaBuilder.greaterThan(bankId, 1L);
            //将两个查询条件联合起来之后返回Predicate对象
            final Predicate predicate = criteriaBuilder.and(p1, p2);
            return predicate;
        };
    }

    private Specification<BpsChannel> predicate_or() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            final Predicate p1 = criteriaBuilder.like(root.get("bankName"), "%中国%");
            final Predicate p2 = criteriaBuilder.greaterThan(root.get("bankId"), 1000L);
            return criteriaBuilder.or(p1, p2);
        };
    }

    private Specification<BpsChannel> predicate_conjunction() {
        final String agency = "55";
        return (root, criteriaQuery, criteriaBuilder) -> {
            // 用來連接 criteriaBuilder
            final Predicate predicate = criteriaBuilder.conjunction();

            predicate.getExpressions().add(criteriaBuilder.and(root.get("bankName").isNotNull()));

            if (!StringUtils.isEmpty(agency)) {
                predicate.getExpressions().add(criteriaBuilder.greaterThan(root.get("bankId"), 1000L));
            }

            return predicate;
        };


    }

    @Test
    void findByPaymentTermNotNullOrderBySorting() {
        final List<BpsChannel> channels = this.iBpsBankRepository.findByPaymentTermNotNullOrderBySorting();
        channels.stream().forEach(System.out::println);
    }
}