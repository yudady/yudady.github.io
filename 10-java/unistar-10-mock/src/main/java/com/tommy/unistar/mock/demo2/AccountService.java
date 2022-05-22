package com.tommy.unistar.mock.demo2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountDao accountDao;

    public Account getAccount(String name) {
        System.out.println("[LOG] call getAccount ");
        return accountDao.findAccountByName(name);
    }
    public AccountDao getAccountException(String name) {
        throw new UnsupportedOperationException("AccountService");
    }
}
