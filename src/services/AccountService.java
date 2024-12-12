package services;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

public class AccountService {
    private final DatabaseManager dbManager;
    private final Logger logger;
    private final SecurityUtils securityUtils;
    
    // Use ReentrantLock for thread-safe operations
    private final ReentrantLock lock = new ReentrantLock();

    public AccountService(DatabaseManager dbManager, Logger logger, SecurityUtils securityUtils) {
        this.dbManager = dbManager;
        this.logger = logger;
        this.securityUtils = securityUtils;
    }

    public boolean login(String cardNumber, String pin) {
        try {
            String hashedPin = securityUtils.hashPin(pin);
            boolean isValid = dbManager.validateCredentials(cardNumber, hashedPin);
            logger.log(isValid ? "Login successful" : "Login failed", cardNumber);
            return isValid;
        } catch (Exception e) {
            logger.logError("Login error", e);
            return false;
        }
    }

    public BigDecimal getBalance(String cardNumber) {
        try {
            lock.lock();
            BigDecimal balance = dbManager.getBalance(cardNumber);
            logger.log("Balance inquiry", cardNumber);
            return balance;
        } finally {
            lock.unlock();
        }
    }

    public boolean withdraw(String cardNumber, BigDecimal amount) {
        try {
            lock.lock();
            BigDecimal currentBalance = dbManager.getBalance(cardNumber);
            
            if (currentBalance.compareTo(amount) < 0) {
                logger.log("Insufficient funds", cardNumber);
                return false;
            }

            boolean success = dbManager.updateBalance(cardNumber, currentBalance.subtract(amount));
            if (success) {
                logger.log("Withdrawal successful", cardNumber);
            }
            return success;
        } finally {
            lock.unlock();
        }
    }

    public boolean deposit(String cardNumber, BigDecimal amount) {
        try {
            lock.lock();
            BigDecimal currentBalance = dbManager.getBalance(cardNumber);
            boolean success = dbManager.updateBalance(cardNumber, currentBalance.add(amount));
            if (success) {
                logger.log("Deposit successful", cardNumber);
            }
            return success;
        } finally {
            lock.unlock();
        }
    }

    public boolean transfer(String fromCard, String toCard, BigDecimal amount) {
        try {
            lock.lock();
            // First check if sender has sufficient funds
            BigDecimal senderBalance = dbManager.getBalance(fromCard);
            if (senderBalance.compareTo(amount) < 0) {
                logger.log("Transfer failed - insufficient funds", fromCard);
                return false;
            }

            // Perform the transfer
            boolean success = dbManager.transfer(fromCard, toCard, amount);
            if (success) {
                logger.log("Transfer successful", fromCard);
            }
            return success;
        } finally {
            lock.unlock();
        }
    }
}
