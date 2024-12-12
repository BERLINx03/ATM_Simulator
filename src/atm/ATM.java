package main.atm;

import services.AccountService;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

public class ATM implements Runnable {
    private final String atmId;
    private final AccountService accountService;
    private final AtomicBoolean running;
    private String currentCardNumber;
    
    public ATM(String atmId, AccountService accountService) {
        this.atmId = atmId;
        this.accountService = accountService;
        this.running = new AtomicBoolean(true);
        this.currentCardNumber = null;
    }

    @Override
    public void run() {
        while (running.get()) {
            // ATM main loop - will be driven by GUI events
            try {
                Thread.sleep(100); // Prevent busy waiting
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public boolean login(String cardNumber, String pin) {
        if (accountService.login(cardNumber, pin)) {
            this.currentCardNumber = cardNumber;
            return true;
        }
        return false;
    }

    public void logout() {
        this.currentCardNumber = null;
    }

    public BigDecimal checkBalance() {
        if (currentCardNumber == null) {
            throw new IllegalStateException("Not logged in");
        }
        return accountService.getBalance(currentCardNumber);
    }

    public boolean withdraw(BigDecimal amount) {
        if (currentCardNumber == null) {
            throw new IllegalStateException("Not logged in");
        }
        return accountService.withdraw(currentCardNumber, amount);
    }

    public boolean deposit(BigDecimal amount) {
        if (currentCardNumber == null) {
            throw new IllegalStateException("Not logged in");
        }
        return accountService.deposit(currentCardNumber, amount);
    }

    public boolean transfer(String toCard, BigDecimal amount) {
        if (currentCardNumber == null) {
            throw new IllegalStateException("Not logged in");
        }
        return accountService.transfer(currentCardNumber, toCard, amount);
    }

    public void shutdown() {
        running.set(false);
    }

    public String getAtmId() {
        return atmId;
    }
}
