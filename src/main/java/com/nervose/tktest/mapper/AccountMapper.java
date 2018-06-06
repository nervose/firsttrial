package com.nervose.tktest.mapper;

import com.nervose.tktest.entity.Account;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

@org.apache.ibatis.annotations.Mapper
public interface AccountMapper extends Mapper<Account>, MySqlMapper<Account> {
}