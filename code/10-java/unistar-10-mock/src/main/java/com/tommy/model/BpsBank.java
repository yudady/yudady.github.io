package com.tommy.model;

import java.sql.Timestamp;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BPS_BANK", schema = "CORE", catalog = "")
public class BpsBank {
    private long bankId;
    private long bankParentId;
    private String bankName;
    private String bankCode;
    private String bankAbbreviation;
    private String displayName;
    private long sorting;
    private Long version;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Boolean category;
    private String paymentTerm;
    private String provider;

    @Id
    @Column(name = "BANK_ID")
    public long getBankId() {
        return bankId;
    }

    public void setBankId(final long bankId) {
        this.bankId = bankId;
    }

    @Basic
    @Column(name = "BANK_PARENT_ID")
    public long getBankParentId() {
        return bankParentId;
    }

    public void setBankParentId(final long bankParentId) {
        this.bankParentId = bankParentId;
    }

    @Basic
    @Column(name = "BANK_NAME")
    public String getBankName() {
        return bankName;
    }

    public void setBankName(final String bankName) {
        this.bankName = bankName;
    }

    @Basic
    @Column(name = "BANK_CODE")
    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(final String bankCode) {
        this.bankCode = bankCode;
    }

    @Basic
    @Column(name = "BANK_ABBREVIATION")
    public String getBankAbbreviation() {
        return bankAbbreviation;
    }

    public void setBankAbbreviation(final String bankAbbreviation) {
        this.bankAbbreviation = bankAbbreviation;
    }

    @Basic
    @Column(name = "DISPLAY_NAME")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    @Basic
    @Column(name = "SORTING")
    public long getSorting() {
        return sorting;
    }

    public void setSorting(final long sorting) {
        this.sorting = sorting;
    }

    @Basic
    @Column(name = "VERSION")
    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }

    @Basic
    @Column(name = "CREATE_TIME")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final Timestamp createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name = "UPDATE_TIME")
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(final Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Basic
    @Column(name = "CATEGORY")
    public Boolean getCategory() {
        return category;
    }

    public void setCategory(final Boolean category) {
        this.category = category;
    }

    @Basic
    @Column(name = "PAYMENT_TERM")
    public String getPaymentTerm() {
        return paymentTerm;
    }

    public void setPaymentTerm(final String paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    @Basic
    @Column(name = "PROVIDER")
    public String getProvider() {
        return provider;
    }

    public void setProvider(final String provider) {
        this.provider = provider;
    }

    @Override
    public int hashCode() {
        int result = (int) (bankId ^ (bankId >>> 32));
        result = 31 * result + (int) (bankParentId ^ (bankParentId >>> 32));
        result = 31 * result + (bankName != null ? bankName.hashCode() : 0);
        result = 31 * result + (bankCode != null ? bankCode.hashCode() : 0);
        result = 31 * result + (bankAbbreviation != null ? bankAbbreviation.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (int) (sorting ^ (sorting >>> 32));
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (paymentTerm != null ? paymentTerm.hashCode() : 0);
        result = 31 * result + (provider != null ? provider.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final BpsBank bpsBank = (BpsBank) o;

        if (bankId != bpsBank.bankId) return false;
        if (bankParentId != bpsBank.bankParentId) return false;
        if (sorting != bpsBank.sorting) return false;
        if (bankName != null ? !bankName.equals(bpsBank.bankName) : bpsBank.bankName != null) return false;
        if (bankCode != null ? !bankCode.equals(bpsBank.bankCode) : bpsBank.bankCode != null) return false;
        if (bankAbbreviation != null ? !bankAbbreviation.equals(bpsBank.bankAbbreviation) : bpsBank.bankAbbreviation != null) return false;
        if (displayName != null ? !displayName.equals(bpsBank.displayName) : bpsBank.displayName != null) return false;
        if (version != null ? !version.equals(bpsBank.version) : bpsBank.version != null) return false;
        if (createTime != null ? !createTime.equals(bpsBank.createTime) : bpsBank.createTime != null) return false;
        if (updateTime != null ? !updateTime.equals(bpsBank.updateTime) : bpsBank.updateTime != null) return false;
        if (category != null ? !category.equals(bpsBank.category) : bpsBank.category != null) return false;
        if (paymentTerm != null ? !paymentTerm.equals(bpsBank.paymentTerm) : bpsBank.paymentTerm != null) return false;
        if (provider != null ? !provider.equals(bpsBank.provider) : bpsBank.provider != null) return false;

        return true;
    }
}
