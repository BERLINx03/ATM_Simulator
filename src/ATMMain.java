import atm.ATM;
import services.AccountService;
import services.DatabaseManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ATMMain {
    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager();

        // Create the AccountService using the database manager
        AccountService accountService = new AccountService(databaseManager);

        // Simulate ATM with an ID (e.g., 1)
        ATM atm = new ATM(1, accountService, databaseManager);

        // Run the ATM system
        new Thread(atm).start();
    }
}