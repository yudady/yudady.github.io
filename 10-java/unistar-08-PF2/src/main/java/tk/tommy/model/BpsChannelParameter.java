package tk.tommy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import tk.tommy.model.to.DepositTerminal;

@Entity
@Table(schema = "CORE", name = "BPS_BANK_PARA")
public class BpsChannelParameter extends BaseEntity {

    private static final long serialVersionUID = 1484169865164122584L;

    @Id
    @GeneratedValue(generator = "BANK_PARA_ID_GENERATOR", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "BANK_PARA_ID_GENERATOR", sequenceName = "SEQ_BANK_PARA", allocationSize = 1)
    @Column(name = "BANK_PARA_ID", nullable = false)
    private Long bankParaId;

    @Column(name = "BANK_ID", nullable = false)
    private Long bankId;

    @Column(name = "PARA_NAME", nullable = false)
    private String paraName;

    @Column(name = "PARA_VALUE", nullable = false)
    private String paraValue;

    @Column(name = "REMARK")
    private String remark;

    @Column(name = "SORTING", nullable = false)
    private Integer sorting;

    @Column(name = "TERMINAL", nullable = false)
    @Enumerated(EnumType.STRING)
    private DepositTerminal terminal;


    public Long getBankParaId() {
        return this.bankParaId;
    }

    public void setBankParaId(final Long bankParaId) {
        this.bankParaId = bankParaId;
    }

    public Long getBankId() {
        return this.bankId;
    }

    public void setBankId(final Long bankId) {
        this.bankId = bankId;
    }

    public String getParaName() {
        return this.paraName;
    }

    public void setParaName(final String paraName) {
        this.paraName = paraName;
    }

    public String getParaValue() {
        return this.paraValue;
    }

    public void setParaValue(final String paraValue) {
        this.paraValue = paraValue;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    public Integer getSorting() {
        return this.sorting;
    }

    public void setSorting(final Integer sorting) {
        this.sorting = sorting;
    }

    public DepositTerminal getTerminal() {
        return this.terminal;
    }

    public void setTerminal(final DepositTerminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public String toString() {
        return "BpsChannelParameter{" +
            "bankParaId=" + this.bankParaId +
            ", bankId=" + this.bankId +
            ", paraName='" + this.paraName + '\'' +
            ", paraValue='" + this.paraValue + '\'' +
            ", remark='" + this.remark + '\'' +
            ", sorting=" + this.sorting +
            ", terminal=" + this.terminal +
            '}';
    }
}