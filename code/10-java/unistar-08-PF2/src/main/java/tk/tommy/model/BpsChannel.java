package tk.tommy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import tk.tommy.model.to.DepositPaymentTerm;
import tk.tommy.model.to.DepositProvider;

@Entity
@Table(name = "BPS_BANK")
public class BpsChannel extends BaseEntity {

    private static final long serialVersionUID = -7269813419955360998L;

    @Id
    @Column(name = "BANK_ID")
    private Long bankId;
    @Column(name = "BANK_NAME")
    private String bankName;
    @Column(name = "BANK_CODE")
    private String bankCode;
    @Column(name = "DISPLAY_NAME")
    private String displayName;
    @Column(name = "SORTING")
    private Long sorting;
    @Column(name = "PAYMENT_TERM")
    @Enumerated(EnumType.STRING)
    private DepositPaymentTerm paymentTerm;
    @Column(name = "PROVIDER")
    @Enumerated(EnumType.STRING)
    private DepositProvider provider;

    public Long getBankId() {
        return this.bankId;
    }

    public void setBankId(final Long bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(final String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return this.bankCode;
    }

    public void setBankCode(final String bankCode) {
        this.bankCode = bankCode;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public Long getSorting() {
        return this.sorting;
    }

    public void setSorting(final Long sorting) {
        this.sorting = sorting;
    }

    public DepositPaymentTerm getPaymentTerm() {
        return this.paymentTerm;
    }

    public void setPaymentTerm(final DepositPaymentTerm paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    public DepositProvider getProvider() {
        return this.provider;
    }

    public void setProvider(final DepositProvider provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        return "BpsChannel{" +
            "bankId=" + this.bankId +
            ", bankName='" + this.bankName + '\'' +
            ", bankCode='" + this.bankCode + '\'' +
            ", displayName='" + this.displayName + '\'' +
            ", sorting=" + this.sorting +
            ", paymentTerm=" + this.paymentTerm +
            ", provider=" + this.provider +
            '}';
    }
}
