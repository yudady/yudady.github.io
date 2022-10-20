package com.tommy.unistar.mock.demo2;

import org.springframework.stereotype.Repository;

@Repository
public class AccountDao {

    public Account findAccountByName(String accountName) {
        System.out.println("[LOG] call AccountDao.findAccountByName ");
        throw new UnsupportedOperationException("AccountDao");
    }
}
