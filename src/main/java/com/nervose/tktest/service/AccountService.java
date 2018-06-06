package com.nervose.tktest.service;

import com.nervose.tktest.entity.Account;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AccountService {

    List<Account> listUsers();

    Account saveUser(Account user);

    Account selectUser(int id);

    Account updateUser(Account user);
}
