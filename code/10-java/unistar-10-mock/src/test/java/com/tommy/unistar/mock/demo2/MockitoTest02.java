package com.tommy.unistar.mock.demo2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

/**
 * @see MockitoExtension
 */
@ExtendWith(MockitoExtension.class)
class MockitoTest02 {

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private AccountDao accountDao;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AccountService accountService;

    @Spy
    private AccountDao accountDaoSpy;

    @Test
    void testMockAccountDao01() {
        final Account account = accountDao.findAccountByName("account");
        System.out.println("[LOG] account = " + account);
    }

    @Test
    void testMockAccountDao02() {
        final AccountDao errorDao = accountService.getAccountException("error");
        final Account error = errorDao.findAccountByName("error");
        System.out.println("[LOG] error = " + error);
    }

    @Test
    void testMockAccountDao03() {
        when(accountService.getAccount("error")).thenCallRealMethod();
        Account error = null;
        try {
            error = accountService.getAccount("error");
            fail();
        } catch (NullPointerException e) {
            Assertions.assertNull(error);
        }

    }

    @Test
    void testMockAccountDao04() {
        Account accountByName = null;
        try {
            accountByName = accountDaoSpy.findAccountByName("error");
        } catch (UnsupportedOperationException e) {
            Assertions.assertNull(accountByName);
        }

    }
}