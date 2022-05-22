package com.tommy.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BPS_BANK_PARA", schema = "CORE")
public class BpsBankPara {
    @Id
    @Column(name = "BANK_PARA_ID")
    public long bankParaId;
    @Column(name = "BANK_ID")
    public long bankId;
    @Column(name = "PARTITION_TYPE")
    public String partitionType;
    @Column(name = "PARA_NAME")
    public String paraName;
    @Column(name = "PARA_VALUE")
    public String paraValue;
    @Column(name = "REMARK")
    public String remark;
    @Column(name = "SORTING")
    public long sorting;
    @Column(name = "VERSION")
    public Long version;
    @Column(name = "CREATE_TIME")
    public LocalDateTime createTime;
    @Column(name = "UPDATE_TIME")
    public LocalDateTime updateTime;
    @Column(name = "DEVICE_TYPE")
    public String deviceType;
    @Column(name = "TERMINAL")
    public String terminal;

}
