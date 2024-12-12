package services;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

public class AccountService {
    private final DatabaseManager dbManager;
    private final Logger logger;
    private final SecurityUtils securityUtils;
    
    // Use ReentrantLock for thread-safe operations
    private final ReentrantLock lock = new ReentrantLock();

    private int userId;
    private int accountId;

    public AccountService(DatabaseManager dbManager, Logger logger, SecurityUtils securityUtils) {
        this.dbManager = dbManager;
        this.logger = logger;
        this.securityUtils = securityUtils;
    }

    public boolean login(String cardNumber, String pin) {
        try {
            String hashedPin = securityUtils.hashPin(pin);
            boolean isValid = dbManager.validateCard(cardNumber, hashedPin);
            logger.log(isValid ? "Login successful" : "Login failed", cardNumber);
            if (isValid) {
                // TODO: You need to add a method to retrieve userId and accountId
                // this.userId = dbManager.getUserId(cardNumber);
                // this.accountId = dbManager.getAccountId(cardNumber);
            }
            return isValid;
        } catch (Exception e) {
            logger.logError("Login error", e);
            return false;
        }
    }

    public BigDecimal getBalance(String cardNumber) {
        try {
            lock.lock();
            double balance = dbManager.viewBalance(userId);
            logger.log("Balance inquiry", cardNumber);
            return BigDecimal.valueOf(balance);
        } finally {
            lock.unlock();
        }
    }

    public boolean withdraw(String cardNumber, BigDecimal amount) {
        try {
            lock.lock();
            boolean success = dbManager.withdrawMoney(userId, amount.doubleValue(), accountId);
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
            boolean success = dbManager.depositMoney(userId, amount.doubleValue(), accountId);
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
            // TODO: You need to add a method to get userId from card number
            // int toUserId = dbManager.getUserId(toCard);
            
            boolean success = dbManager.transferMoney(userId, toUserId, amount.doubleValue());
            if (success) {
                logger.log("Transfer successful", fromCard);
            }
            return success;
        } finally {
            lock.unlock();
        }
    }

    // New method to get transaction history
    public List<String> getTransactionHistory() {
        return dbManager.getTransactionHistory(userId);
    }
}
