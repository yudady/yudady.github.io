package com.tommy.api.center.jbp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;

/**
 * notes: 對API資源做描述
 * <p>
 * hiddem=true/false: 此屬性隱藏起來
 * <p>
 * position: Swagger UI介面的傳入json順序，1就是第一個，2就是第二個.....
 * <p>
 * required=true/false: 是否為必填的field
 * <p>
 * value: 在Example的json格式中，key值的腳色，就是我們的DTO的物件屬性
 * <p>
 * example: 範例的值，就是物件屬性給他的值預設要多少
 */
@ApiModel(description = "JBP充值申請參數")
public class JbpSendDepositRequest {

    @ApiModelProperty(notes = "银行编码，详细编码请见 8.银行编码", position = 1, value = "1", example = "1")
    public Long channelId;

    @ApiModelProperty(notes = "充值金额；单位元，需保留两位小数点", position = 2, value = "543.78", example = "543.78")
    @NotNull
    public BigDecimal amount;

    @ApiModelProperty(notes = "商户订单号：唯一性的字符串", position = 3, value = "orderNumber", example = "商户订单号")
    @NotNull
    public String companyOrderNum;

    @ApiModelProperty(notes = "提交订单用户昵称；建议加密后传入", position = 4, value = "UB5TommyyDA", example = "UB5TommyyDA")
    @NotNull
    public String companyUser;

    @ApiModelProperty(notes = "充值渠道：1 , 2 ", position = 5, value = "1", example = "1")
    @NotNull
    public Integer depositMode;


    @ApiModelProperty(notes = "备用字段", position = 7, value = "memo", example = "memo")
    public String memo;

    @ApiModelProperty(notes = "附言", position = 8, value = "note", example = "note")
    public String note;

    @ApiModelProperty(notes = "订单匹配模式", position = 9, value = "1", example = "1")
    public Integer noteModel;

    @ApiModelProperty(notes = "使用终端：1电脑端2手机端", position = 10, value = "1", example = "1")
    @NotNull
    public Integer terminal;

}