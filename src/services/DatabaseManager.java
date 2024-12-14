package services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:database/atm_simulator.db";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public String createUser(String name, String hashedPin, double balance) {
        String insertUserQuery = "INSERT INTO users (name, card_number, hashed_pin) VALUES (?, ?, ?)";
        String insertAccountQuery = "INSERT INTO accounts (user_id, balance) VALUES (?, ?)";
        String cardNumber = generateUniqueCardNumber();

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Start a transaction

            try (PreparedStatement userStmt = conn.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement accountStmt = conn.prepareStatement(insertAccountQuery)) {

                // Insert user
                userStmt.setString(1, name);
                userStmt.setString(2, cardNumber);
                userStmt.setString(3, hashedPin);
                userStmt.executeUpdate();

                // Retrieve generated user ID
                ResultSet userKeys = userStmt.getGeneratedKeys();
                if (userKeys.next()) {
                    int userId = userKeys.getInt(1);

                    // Insert account
                    accountStmt.setInt(1, userId);
                    accountStmt.setDouble(2, balance);
                    accountStmt.executeUpdate();

                    conn.commit(); // Commit the transaction
                    return cardNumber; // Return the generated card number
                } else {
                    conn.rollback();
                    System.err.println("Failed to retrieve generated user ID.");
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error while creating user and account: " + e.getMessage());
        }

        return null;
    }


    public static boolean validateCard(String cardNumber, String hashedPin) {

        String sql = "SELECT * FROM users WHERE card_number = ? AND hashed_pin = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            /**
             * sets the value of the first placeholder(bind variable - '?') to the string sent next
             * */
            statement.setString(1, cardNumber);
            statement.setString(2, hashedPin);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Error while validating the card: " + e.getMessage());
            return false;
        }
    }

    public boolean depositMoney(int userId, double amount, int accountId) {

        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }

        String checkUserQuery = "SELECT COUNT(*) FROM accounts WHERE user_id = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement checkUserExistance = conn.prepareStatement(checkUserQuery);
        ) {

            checkUserExistance.setInt(1, userId);

            ResultSet rs = checkUserExistance.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                System.err.println("User:" + userId + " doesn't exists.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error while deposit money: " + e.getMessage());
        }


        String sql = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, userId);

            int rowAffected = stmt.executeUpdate();

            if(rowAffected > 0){
                System.out.println(amount + " has been deposited successfully.");
                logTransaction(accountId,"deposit", amount);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error while depositing money: " + e.getMessage());
        }
        return false;
    }

    public boolean withdrawMoney(int userId, double amount, int accountId) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }

        String checkBalanceQuery = "SELECT balance FROM accounts WHERE user_id = ?";
        String updateBalanceQuery = "UPDATE accounts SET balance = balance - ? WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement checkBalanceStmt = connection.prepareStatement(checkBalanceQuery);
             PreparedStatement updateBalanceStmt = connection.prepareStatement(updateBalanceQuery)){

            checkBalanceStmt.setInt(1,userId);
            try(ResultSet rs = checkBalanceStmt.executeQuery()){
                if(rs.next()){
                    double currentBalance = rs.getDouble("balance");
                    if( currentBalance < amount){
                        System.err.println("User:" + userId + " doesn't have enough money.");
                        return false;
                    }
                } else {
                    System.err.println("User:" + userId + " not found.");
                    return false;
                }
            }

            updateBalanceStmt.setDouble(1,amount);
            updateBalanceStmt.setInt(2,userId);
            int rowAffected = updateBalanceStmt.executeUpdate();
            if(rowAffected > 0){
                logTransaction(accountId,"withdraw", amount);
                System.out.println("User:" + userId + " has withdrew " + amount + "$.");
                return true;
            }

        }catch (SQLException e) {
            System.err.println("Error while withdrawing money: " + e.getMessage());
        }
        return false;
    }

    public boolean transferMoney(int fromUserId, int toUserId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
        String checkBalanceQuery = "SELECT balance FROM accounts WHERE user_id = ?";
        String updateBalanceQuery = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";
        String checkRecipientQuery = "SELECT 1 FROM accounts WHERE user_id = ?";
        /**
         * a transaction is a sequence of operations that are executed as a single unit.
         * It ensures that either all operations within the transaction are successfully completed (committed),
         * or none of the operations are applied (rolled back). This guarantees data integrity.
         * */
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // transction

            try (
                    PreparedStatement checkBalanceStmt = conn.prepareStatement(checkBalanceQuery);
                    PreparedStatement updateBalanceStmt = conn.prepareStatement(updateBalanceQuery);
                    PreparedStatement checkRecipientStmt = conn.prepareStatement(checkRecipientQuery);
            ) {
                checkBalanceStmt.setInt(1,fromUserId);
                try(ResultSet rs = checkBalanceStmt.executeQuery()){
                    if(rs.next()){
                        double currentBalance = rs.getDouble("balance");
                        if(currentBalance < amount){
                            throw new SQLException("User:" + fromUserId + " doesn't have enough money.");
                        }
                    } else {
                        throw new SQLException("User:" + fromUserId + " not found.");
                    }
                }

                checkRecipientStmt.setInt(1, toUserId);
                try (ResultSet rs = checkRecipientStmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Recipient user:" + toUserId + " not found.");
                    }
                }

                updateBalanceStmt.setDouble(1, -amount);
                updateBalanceStmt.setInt(2, fromUserId);
                updateBalanceStmt.executeUpdate();

                updateBalanceStmt.setDouble(1, amount);
                updateBalanceStmt.setInt(2, toUserId);
                updateBalanceStmt.executeUpdate();

                logTransaction(fromUserId,"transfer out ", amount);

                logTransaction(toUserId,"transfer in ", amount);

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Error during fund transfer: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            System.err.println("Error while transferring money: " + e.getMessage());
            return false;
        }

    }

    public double getBalance(String cardNumber) {
        String query = "SELECT a.balance FROM accounts a " +
                "JOIN users u ON a.user_id = u.id " +
                "WHERE u.card_number = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, cardNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving balance: " + e.getMessage());
        }

        return -1;
    }

    public boolean updateBalance(String cardNumber, double newBalance) {
        String query = "UPDATE accounts SET balance = ? " +
                "WHERE user_id = (SELECT id FROM users WHERE card_number = ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDouble(1, newBalance);
            stmt.setString(2, cardNumber);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error while updating balance: " + e.getMessage());
        }

        return false;
    }

    public boolean logTransaction(int accountId, String type, double amount) {
        String insertTransactionQuery = "INSERT INTO transactions (account_id, type, amount) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(insertTransactionQuery)){

            stmt.setInt(1, accountId);
            stmt.setString(2, type);
            stmt.setDouble(3, amount);
            int rowAffected = stmt.executeUpdate();
            if(rowAffected > 0){
                System.out.println("Transaction " + accountId + " has been logged.");
                return true;
            }
        } catch (SQLException e){
            System.err.println("Error while logging transaction: " + e.getMessage());
        }
        return false;
    }

    public List<String> getTransactionHistory(int userId) {
        List<String> transactionHistory = new ArrayList<>();
        String query = "SELECT t.type, t.amount, t.timestamp " +
                "FROM transactions t " +
                "JOIN accounts a ON t.account_id = a.id " +
                "WHERE a.user_id = ? " +
                "ORDER BY t.timestamp DESC";

        try(Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("type");
                    double amount = rs.getDouble("amount");
                    String timestamp = rs.getString("timestamp");
                    transactionHistory.add(type + ": " + amount + " on " + timestamp);
                }
            }
        }catch (SQLException e){
            System.err.println("Error while getting transaction history: " + e.getMessage());

        }
        return transactionHistory;
    }

    ///////////////////////////////////////////
    /// Helper functions
    private String generateUniqueCardNumber() {
        String cardNumber = null;
        boolean isUnique = false;

        String checkDuplicateSql = "SELECT COUNT(*) FROM accounts WHERE card_number = ?";

        try (Connection conn = getConnection()) {
            while (!isUnique) {
                cardNumber = generateRandomCardNumber();
                try (PreparedStatement stmt = conn.prepareStatement(checkDuplicateSql)) {
                    stmt.setString(1, cardNumber);
                    try (ResultSet rs = stmt.executeQuery()) {
                        // get the first column current row
                        if (rs.next() && rs.getInt(1) == 0) {
                            isUnique = true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while generating unique card number: " + e.getMessage());
        }
        return cardNumber;
    }
    private String generateRandomCardNumber() {
        StringBuilder cardNumber = new StringBuilder();

        for (int i = 0; i < 15; i++) {
            cardNumber.append((int) (Math.random() * 10));
        }

        cardNumber.append(calculateLuhnChecksum(cardNumber.toString()));
        return cardNumber.toString();
    }
    // Luhn Algorithm to calculate the checksum
    private int calculateLuhnChecksum(String number) {
        int sum = 0;
        boolean alternate = false;

        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            alternate = !alternate;
        }
        return (10 - (sum % 10)) % 10;
    }
}
