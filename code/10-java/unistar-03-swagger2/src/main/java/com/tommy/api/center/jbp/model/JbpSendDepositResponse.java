package com.tommy.api.center.jbp.model;

import java.math.BigDecimal;

public class JbpSendDepositResponse {
    public String bankCardNum;
    public String bankAccName;
    public BigDecimal amount;
    public String email;
    public String companyOrderNum;
    public String dateTime;
    public String key;
    public String note;
    public String mownecumOrderNum;
    public Integer status;
    public String errorMsg = "";
    public Integer mode;
    public String issuingBankAddress;
    public String breakUrl;
    public Integer depositMode;
    public Integer collectionBankId;

}
