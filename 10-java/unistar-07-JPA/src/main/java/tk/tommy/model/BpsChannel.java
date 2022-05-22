package tk.tommy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BPS_BANK")
public class BpsChannel extends BaseEntity {

    private static final long serialVersionUID = -7269813419955360998L;

    @Id
    @Column(name = "BANK_ID")
    private Long bankId;
    @Column(name = "BANK_PARENT_ID")
    private Long bankParentId;
    @Column(name = "BANK_ABBREVIATION")
    private String bankAbbreviation;
    @Column(name = "BANK_NAME")
    private String bankName;
    @Column(name = "BANK_CODE")
    private String bankCode;
    @Column(name = "DISPLAY_NAME")
    private String displayName;
    @Column(name = "SORTING")
    private Long sorting;
    @Column(name = "CATEGORY")
    private String category;
    @Column(name = "PAYMENT_TERM")
    @Enumerated(EnumType.STRING)
    private DepositPaymentTerm paymentTerm;
    @Column(name = "PROVIDER")
    @Enumerated(EnumType.STRING)
    private DepositProvider provider;

    public BpsChannel() {
    }

    public BpsChannel(final Long bankId, final String bankName) {
        this.bankId = bankId;
        this.bankName = bankName;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getBankAbbreviation() {
        return this.bankAbbreviation;
    }

    public void setBankAbbreviation(final String bankAbbreviation) {
        this.bankAbbreviation = bankAbbreviation;
    }

    public Long getBankId() {
        return this.bankId;
    }

    public void setBankId(final Long bankId) {
        this.bankId = bankId;
    }

    public Long getBankParentId() {
        return this.bankParentId;
    }

    public void setBankParentId(final Long bankParentId) {
        this.bankParentId = bankParentId;
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
}
