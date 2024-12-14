package atm;

import services.AccountService;

import java.sql.SQLException;

public class ATM implements Runnable {

    AccountService accountService;
    Operation currentOperation = Operation.LOGIN;

    public ATM(AccountService accountService) {
        this.accountService = accountService;
    }
    @Override
    public void run() {

        while (true) {
            switch (currentOperation) {
                case SIGNUP: {
                    String cardNumber = accountService.createUser("","",3.3);
                    // MessageBox el card
                    // button to login
                }
                case LOGIN: {
                    accountService.login("ATM", "ATM");
                    currentOperation = Operation.VIEW_OPERATION;
                }
                case LOGOUT: {
                    accountService.logout();
                    currentOperation = Operation.LOGIN;
                }
                case DEPOSIT: {
                    try {
                        accountService.deposit(-1,-1.0);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    currentOperation = Operation.VIEW_OPERATION;
                }
                case WITHDRAW: {
                    try {
                        accountService.withdraw(-1,-1.0);
                    } catch (SQLException e){
                        throw new RuntimeException(e);
                    }
                    currentOperation = Operation.VIEW_OPERATION;

                }
                case GET_BALANCE: {
                    accountService.getBalance();
                    currentOperation = Operation.VIEW_OPERATION;

                }
                case TRANSFER: {
                    try {
                        accountService.transferFunds("card",0.0);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    currentOperation = Operation.VIEW_OPERATION;
                }
                case VIEW_OPERATION: {
                    System.out.println("Current operation: " + currentOperation.toString());
                }
            }
        }
    }
}
