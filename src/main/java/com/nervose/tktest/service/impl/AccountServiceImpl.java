package com.nervose.tktest.service.impl;

import com.nervose.tktest.entity.Account;
import com.nervose.tktest.mapper.AccountMapper;
import com.nervose.tktest.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = {"accountCache"})
public class AccountServiceImpl implements AccountService{
    @Autowired
    private AccountMapper accountMapper;

    @Override
    @Cacheable(value = "Users")
    public List<Account> listUsers() {
        return accountMapper.selectAll();
    }

    @Override
    public Account saveUser(Account account) {
        int id=accountMapper.insert(account);
        return accountMapper.selectByPrimaryKey(id);
    }

    @Override
    @Cacheable(key = "#id")
    public Account selectUser(int id) {
        return accountMapper.selectByPrimaryKey(id);
    }

    @Override
    @CacheEvict(key = "#p0.id")
    public Account updateUser(Account user) {
        accountMapper.updateByPrimaryKeySelective(user);
        return user;
    }
}
