package com.tommy.api.center.ped.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;

public class PedSendDepositRequest {
    @ApiModelProperty(notes = "银行编码，详细编码请见 8.银行编码", position = 1, value = "1", example = "1")
    @JsonProperty("bank_id")
    public Long bankId;
    @ApiModelProperty(notes = "充值金额；单位元，需保留两位小数点", position = 2, value = "543.78", example = "543.78")
    public BigDecimal amount;
    @ApiModelProperty(notes = "商户订单号：唯一性的字符串", position = 3, value = "orderNumber", example = "商户订单号")
    @JsonProperty("company_order_num")
    public String companyOrderNum;
    @ApiModelProperty(notes = "提交订单用户昵称；建议加密后传入", position = 4, value = "UB5TommyyDA", example = "UB5TommyyDA")
    @JsonProperty("company_user")
    public String companyUser;
    @ApiModelProperty(notes = "充值渠道：1 , 2 ", position = 5, value = "1", example = "1")
    @JsonProperty("deposit_mode")
    public Integer depositMode;
    @ApiModelProperty(notes = "备用字段", position = 7, value = "1", example = "1")
    public String memo;
    @ApiModelProperty(notes = "附言", position = 8, value = "", example = "")
    public String note;
    @ApiModelProperty(notes = "订单匹配模式", position = 9, value = "1", example = "1")
    @JsonProperty("note_model")
    public Integer noteModel;
    @ApiModelProperty(notes = "使用终端：1电脑端2手机端", position = 10, value = "1", example = "1")
    public Integer terminal;
    public String key;
}