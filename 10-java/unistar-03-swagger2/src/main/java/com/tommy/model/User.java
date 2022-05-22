package com.tommy.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("用户实体")
@Data
public class User {
    @ApiModelProperty("用户 id")
    private int id;
}
