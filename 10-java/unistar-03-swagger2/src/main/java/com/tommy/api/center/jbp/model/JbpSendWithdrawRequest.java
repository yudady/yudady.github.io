package com.tommy.api.center.jbp.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;

public class JbpSendWithdrawRequest {

    // "company_id": 17,
    //     "bank_id": 12,
    //     "amount": 100.00,
    //     "company_order_num": "UBd6cW1701f0593b7",
    //     "company_user": "57559",
    //     "card_num": "0000000000000017",
    //     "card_name": "胚尼苓苓一",
    //     "issue_bank_name": "平安银行",
    //     "issue_bank_address": "大连_大连",
    //     "memo": "4",
    //     "key": "e5457d6376ae7dbcfdf78a823dc71dcf",
    //     "web_url": "http://jbppf2.uenvqa.com/callback/"

    @ApiModelProperty(value = "1", example = "1")
    public Long bankId;
    @ApiModelProperty(value = "1", example = "1")
    public String companyOrderNum;
    @ApiModelProperty(value = "234.54", example = "234.54")
    public BigDecimal amount;
    @ApiModelProperty(value = "123456789", example = "123456789")
    public String cardNum;
    @ApiModelProperty(value = "0000000000000017", example = "0000000000000017")
    public String cardName;
    @ApiModelProperty(value = "tommyCardDA", example = "tommyCardDA")
    public String key;
    @ApiModelProperty(value = "tommyDA", example = "tommyDA")
    public String companyUser;
    @ApiModelProperty(value = "", example = "")
    public String issueBankName;
    @ApiModelProperty(value = "平安银行", example = "平安银行")
    public String issueBankAddress;
    @ApiModelProperty(value = "大连_大连", example = "大连_大连")
    public String memo;
}