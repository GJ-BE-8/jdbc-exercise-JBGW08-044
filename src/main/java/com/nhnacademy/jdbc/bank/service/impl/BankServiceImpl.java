package com.nhnacademy.jdbc.bank.service.impl;

import com.nhnacademy.jdbc.bank.domain.Account;
import com.nhnacademy.jdbc.bank.exception.AccountAreadyExistException;
import com.nhnacademy.jdbc.bank.exception.AccountNotFoundException;
import com.nhnacademy.jdbc.bank.exception.BalanceNotEnoughException;
import com.nhnacademy.jdbc.bank.repository.AccountRepository;
import com.nhnacademy.jdbc.bank.repository.impl.AccountRepositoryImpl;
import com.nhnacademy.jdbc.bank.service.BankService;

import java.sql.Connection;
import java.util.Optional;

public class BankServiceImpl implements BankService {

    private final AccountRepository accountRepository;

    public BankServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account getAccount(Connection connection, long accountNumber){
        //todo#11 계좌-조회
        Optional<Account> account = accountRepository.findByAccountNumber(connection, accountNumber);
        if(account.isEmpty()){
            throw new AccountNotFoundException(accountNumber);
        }
        return account.get();
    }

    @Override
    public void createAccount(Connection connection, Account account){
        //todo#12 계좌-등록
        accountRepository.save(connection, account);
    }

    @Override
    public boolean depositAccount(Connection connection, long accountNumber, long amount){
        //todo#13 예금, 계좌가 존재하는지 체크 -> 예금실행 -> 성공 true, 실패 false;
        Optional<Account> account = accountRepository.findByAccountNumber(connection, accountNumber);
        if(account.isPresent()){
            int result = accountRepository.deposit(connection, accountNumber, amount);
            if(result == 0){
                throw new BalanceNotEnoughException(amount);
            }
        } else {
            throw new AccountNotFoundException(accountNumber);
        }
        return true;
    }

    @Override
    public boolean withdrawAccount(Connection connection, long accountNumber, long amount){
        //todo#14 출금, 계좌가 존재하는지 체크 ->  출금가능여부 체크 -> 출금실행, 성공 true, 실폐 false 반환
        Optional<Account> account = accountRepository.findByAccountNumber(connection, accountNumber);
        if(account.isPresent()){
            int result = accountRepository.withdraw(connection, accountNumber, amount);
            if(result == 0){
                throw new BalanceNotEnoughException(amount);
            }
        } else {
            throw new AccountNotFoundException(accountNumber);
        }
        return false;
    }

    @Override
    public void transferAmount(Connection connection, long accountNumberFrom, long accountNumberTo, long amount){
        //todo#15 계좌 이체 accountNumberFrom -> accountNumberTo 으로 amount만큼 이체
        Optional<Account> accountFrom = accountRepository.findByAccountNumber(connection, accountNumberFrom);
        Optional<Account> accountTo = accountRepository.findByAccountNumber(connection, accountNumberTo);
        if(accountFrom.isEmpty()){
            throw new AccountNotFoundException(accountNumberTo);
        } else if(accountTo.isEmpty()){
            this.depositAccount(connection, accountNumberTo, amount);
        } else {
            this.withdrawAccount(connection, accountNumberFrom, amount);
            this.depositAccount(connection, accountNumberTo, amount);
        }
    }

    @Override
    public boolean isExistAccount(Connection connection, long accountNumber){
        //todo#16 Account가 존재하면 true , 존재하지 않다면 false
        Optional<Account> account = accountRepository.findByAccountNumber(connection, accountNumber);
        if(account.isPresent()){
            return true;
        } else{
            return false;
        }
    }

    @Override
    public void dropAccount(Connection connection, long accountNumber) {
        //todo#17 account 삭제
        Optional<Account> account = accountRepository.findByAccountNumber(connection, accountNumber);
        if(account.isPresent()){
            accountRepository.deleteByAccountNumber(connection, accountNumber);
        } else {
            throw new AccountNotFoundException(accountNumber);
        }
    }

}