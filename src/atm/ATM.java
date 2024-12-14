package atm;

import services.AccountService;

import java.sql.SQLException;

public class ATM implements Runnable {

    AccountService accountService;
    Operation currentOperation = Operation.LOGIN;

    public ATM(AccountService accountService) {
        this.accountService = accountService;
    }

    public String createUser(String name, String pin, double balance) {
        return accountService.createUser(name, pin, balance);
    }

    public boolean login(String cardNumber,String pin) {
        return accountService.login(cardNumber, pin);
    }

    public void logout() {
        accountService.logout();
    }

    public boolean deposit(int userId, double amount) throws SQLException {
        return accountService.deposit(userId,amount);
    }

    public boolean withdraw(int userId, double amount) throws SQLException {
        return accountService.withdraw(userId,amount);
    }

    public boolean transferFunds(String recipientCardNumber, double amount) throws SQLException {
        return accountService.transferFunds(recipientCardNumber,amount);
    }

    public double getBalance() {
        return accountService.getBalance();
    }
    @Override
    public void run() {

    }

}
