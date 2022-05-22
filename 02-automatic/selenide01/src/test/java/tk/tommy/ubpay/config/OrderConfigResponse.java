package tk.tommy.ubpay.config;

import core.framework.api.json.Property;
import core.framework.api.validate.Digits;
import core.framework.api.validate.Max;
import core.framework.api.validate.Min;
import core.framework.api.validate.NotNull;

public class OrderConfigResponse {
    @NotNull
    @Property(name = "id")
    public Integer id;

    @NotNull
    @Min(1)
    @Max(999)
    @Digits(integer = 3, fraction = 0)
    @Property(name = "withdrawalWaitingTime")
    public Integer withdrawalWaitingTime; // 提現等待時長(分)

    @NotNull
    @Min(1)
    @Max(999)
    @Digits(integer = 3, fraction = 0)
    @Property(name = "depositWaitingTime")
    public Integer depositWaitingTime; // 充值等待時長(分)

    @NotNull
    @Property(name = "enableOrderApply")
    public Boolean enableOrderApply; // 是否允許訂單申請

}
