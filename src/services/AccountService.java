package services;

import user.UserLocker;

import java.sql.SQLException;

public class AccountService {

    private final DatabaseManager database;
    UserLocker lock;

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
        lock(lock){
            int accountId = database.getAccountId(userId);
            if (accountId == -1) {
                return false;
            }

            database.depositMoney(userId, amount, accountId);
        }
        return true;
    }
    //login
    //withdraw
    //deposit
    //transfer
    //logout
    //hash
    //getAccount

}
