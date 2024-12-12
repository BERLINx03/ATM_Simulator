# ATM Simulator Project - README

## **Project Overview**
This project is an ATM simulator designed to practice multithreading concepts in Java. It provides functionality for:
- Logging in using a card number and PIN.
- Viewing account balance.
- Depositing money.
- Withdrawing money.
- Transferring money to another user via their card number.
- Handling concurrent ATM operations.

## **Directory Structure**
```
ATM-Simulator/
├── src/
│   ├── main/
│   │   ├── ATMMain.java
│   │   ├── atm/
│   │   │   ├── ATM.java
│   │   │   ├── ATMGUI.java
│   │   ├── services/
│   │   │   ├── AccountService.java
│   │   │   ├── DatabaseManager.java
│   │   │   ├── Logger.java
│   │   │   ├── SecurityUtils.java
|   |   |   ├── SessionManager.java
│   │   ├── tests/
│   │   │   ├── ConcurrencyTest.java
├── database/
│   ├── schema.sql
│   ├── atm_simulator.db
├── logs/
│   ├── transactions.log
├── README.md
```

---

## **Responsibilities of Each File**

### **ATMMain Application**
- **`ATMMain.java`**
    - Entry point of the application.
    - Initializes the GUI and spawns ATM threads.
    - Ensures all components are started properly.

### **ATM Logic and GUI**
- **`ATM.java`**
    - Represents an individual ATM.
    - Handles user interactions, such as login, balance inquiry, deposits, withdrawals, and transfers.
    - Runs on its own thread to simulate independent ATMs.

- **`ATMGUI.java`**
    - Manages the graphical user interface for user interaction.
    - Runs on a separate thread.
    - Passes user input to the respective ATM thread.

### **Services**
- **`AccountService.java`**
    - Centralized business logic for ATM operations.
    - Implements methods for:
        - User login.
        - Balance inquiries.
        - Deposits and withdrawals.
        - Money transfers.
    - Calls `DatabaseManager` for database interactions.

- **`DatabaseManager.java`**
    - Manages database connections and queries.
    - Handles:
        - Validating login credentials.
        - Fetching and updating account balances.
        - Logging all transactions.

- **`Logger.java`**
    - Handles logging for all transactions and system events.
    - Writes logs to `logs/transactions.log` for auditing and debugging.

- **`SecurityUtils.java`**
    - Provides utility methods for:
        - Hashing user PINs.
        - Masking sensitive data like card numbers.

### **Testing**
- **`ConcurrencyTest.java`**
    - Simulates multiple ATM threads accessing shared accounts.
    - Validates thread safety and consistency of the application.

### **Database**
- **`schema.sql`**
    - Defines the structure of the SQLite database.
    - Includes tables for users, accounts, and transactions.

- **`atm_simulator.db`**
    - SQLite database file initialized with the schema.

### **Logs**
- **`transactions.log`**
    - Contains logs of all transactions and system events for debugging and audits.

---

## **Implementation Instructions**

### **1. ATMMain.java**
- Initialize the database and GUI.
- Start multiple ATM threads.
- Ensure proper synchronization between threads.

### **2. ATM.java**
- Implement multithreading for ATM operations.
- Synchronize operations on shared accounts.
- Communicate with `AccountService` for all user actions.

### **3. ATMGUI.java**
- Create a user-friendly graphical interface.
- Collect user inputs like card number, PIN, and transaction details.
- Forward inputs to the respective ATM thread.

### **4. AccountService.java**
- Write methods for:
    - Login authentication.
    - Fetching account balance.
    - Processing deposits and withdrawals.
    - Handling money transfers.
- Ensure thread safety when accessing `DatabaseManager`.

### **5. DatabaseManager.java**
- Implement CRUD operations for:
    - Validating user credentials.
    - Fetching and updating balances.
    - Logging transactions.
- Use prepared statements for secure database access.

### **6. Logger.java**
- Write logs for:
    - Successful and failed transactions.
    - ATM startup and shutdown events.
    - Security-related events (e.g., failed login attempts).

### **7. SecurityUtils.java**
- Implement:
    - PIN hashing using a secure hashing algorithm (e.g., SHA-256).
    - Masking card numbers for secure display.

### **8. ConcurrencyTest.java**
- Simulate scenarios where multiple ATMs perform concurrent operations.
- Test for:
    - Deadlocks.
    - Race conditions.
    - Data consistency.

### **9. schema.sql**
- Define tables:
    - `users` (id, card_number, hashed_pin).
    - `accounts` (id, user_id, balance).
    - `transactions` (id, account_id, type, amount, timestamp).

---

## **How to Get Started**
1. Set up the SQLite database by running `schema.sql`.
2. Implement the classes following the responsibilities outlined above.
3. Start the application from `ATMMain.java`.
4. Use `ConcurrencyTest` to validate thread safety.
5. Check logs in `logs/transactions.log` for debugging and audits.


