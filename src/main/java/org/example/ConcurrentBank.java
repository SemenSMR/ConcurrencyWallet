package org.example;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentBank {
    private final Map<Integer, BankAccount> accountMap = new HashMap<>();
    private final Lock lock = new ReentrantLock();
    private int nextAccountId = 1;

    public BankAccount createAccount(BigDecimal initialBalance) {
        lock.lock();
        try {

            BankAccount account = new BankAccount(initialBalance);
            accountMap.put(nextAccountId++, account);

            return account;
        } finally {
            lock.unlock();
        }
    }

    public void transfer(BankAccount fromAccount, BankAccount toAccount, BigDecimal amount) {

        BankAccount firstLock = fromAccount.hashCode() < toAccount.hashCode() ? fromAccount : toAccount;
        BankAccount secondLock = fromAccount.hashCode() < toAccount.hashCode() ? toAccount : fromAccount;
        firstLock.lock.lock();
        try {
            secondLock.lock.lock();
            try {
                if (fromAccount.getBalance().compareTo(amount) >= 0) {
                    fromAccount.withdraw(amount);
                    toAccount.deposit(amount);
                    System.out.println(Thread.currentThread().getName() + " transferred " + amount);

                } else {
                    System.out.println(Thread.currentThread().getName() + " insufficient funds for transfer.");
                }
            } finally {
                secondLock.lock.unlock();
            }
        } finally {
            firstLock.lock.unlock();
        }
    }

    public BigDecimal getTotalBalance() {
        lock.lock();
        try {
            BigDecimal total = BigDecimal.ZERO;
            for (BankAccount account : accountMap.values()) {
                total = total.add(account.getBalance());
            }
            return total;
        } finally {
            lock.unlock();
        }
    }
}