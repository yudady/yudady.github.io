package com.tommy.unistar.mock.demo2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
class MockitoTest01 {

    @Test
    void testMockAccountDao02() {
        final AccountDao accountDao = mock(AccountDao.class);
        final Account account = accountDao.findAccountByName("account");
        System.out.println("[LOG] account = " + account);
    }

    @Test
    void testMockAccountDao03() {
        final AccountDao accountDao = mock(AccountDao.class, Mockito.RETURNS_SMART_NULLS);
        final Account account = accountDao.findAccountByName("account");
        System.out.println("[LOG] account = " + account);
    }
}