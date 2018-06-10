package com.nervose.tktest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class TestResponse {
    @JsonFormat(timezone = "GMT+8", pattern = "时间为：yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
