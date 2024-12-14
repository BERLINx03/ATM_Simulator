package services;

import user.UserLocker;

import java.sql.SQLException;

public class AccountService {

    private final DatabaseManager database;
    private UserLocker lock;

    public AccountService() {
        this.database = DatabaseManager.getInstance();
    }

    public boolean login(String cardNumber, String pin) {

        String hashedPin = SecurityUtils.hashPin(pin);

        int userId =  database.login(cardNumber, hashedPin);
        if (userId == -1) {
            return false;
        }
        lock = new UserLocker(userId);
        return true;
    }
    public boolean deposit(int userId, double amount) throws SQLException {

        if (lock == null) {
            throw new IllegalStateException("User is not logged in or lock is not initialized.");
        }
        synchronized (lock) {
            int accountId = database.getAccountId(userId);
            if (accountId == -1) {
                return false; // Account not found
            }
            return database.depositMoney(userId, amount, accountId);
        }
    }
    public boolean withdraw(int userId,double amount) throws SQLException {
        if (lock == null) {
            throw new IllegalStateException("User is not logged in or lock is not initialized.");
        }

        synchronized (lock) {
            int accountId = database.getAccountId(userId);
            if (accountId == -1) {
                return false;
            }
            return database.withdrawMoney(userId, amount, accountId);
        }
    }
    public void logout(){
        lock = null;
    }
    public boolean transferFunds(String recipientCardNumber, double amount) throws SQLException {
        if (lock == null) {
            throw new IllegalStateException("User is not logged in or lock is not initialized.");
        }
        synchronized (lock) {
            int recipientId = database.getUserId(recipientCardNumber);
            int senderId = lock.userId;
            if (recipientId == -1) {
                return false;
            }
            return database.transferMoney(senderId, recipientId, amount);
        }
    }
    public double getBalance() {
        if (lock == null) {
            throw new IllegalStateException("User is not logged in or lock is not initialized.");
        }
        synchronized (lock) {
            int userId = lock.userId;
            return database.getBalance(userId);
        }
    }
    //hash
    //getAccount

}
