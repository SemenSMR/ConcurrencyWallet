package org.example;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private BigDecimal balance;
    final Lock lock = new ReentrantLock();


    public BankAccount( BigDecimal balance) {

        this.balance = balance;
    }

    public void deposit(BigDecimal amount) {
        lock.lock();
        try {
            balance = balance.add(amount);
            System.out.println(Thread.currentThread().getName() + " deposit " + amount  +", new balance: " + balance);


        } finally {
            lock.unlock();
        }

    }

    public void withdraw(BigDecimal amount) {
        lock.lock();
        try {
            if (balance.compareTo(amount) >= 0) {
                balance = balance.subtract(amount);
                System.out.println(Thread.currentThread().getName() + " withdraw " + amount + ", new balance: " + balance);
            } else {
                System.out.println(Thread.currentThread().getName() + " insufficient funds for withdraw: " + amount);
            }
        } finally {
            lock.unlock();
        }
    }

    public BigDecimal getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }
}
