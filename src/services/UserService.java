package services;

public class UserService {

    DatabaseManager database;

    private UserService() {
        this.database = DatabaseManager.getInstance();
    }

    public void createUser(String name, String pin, double balance) {
        String hashedPin = SecurityUtils.hashPin(pin);
        database.createUser(name, hashedPin, balance);
    }
}
