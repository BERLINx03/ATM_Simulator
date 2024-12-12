CREATE TABLE IF NOT EXISTS users (
                                     id INTEGER PRIMARY KEY,
                                     card_number TEXT NOT NULL UNIQUE,
                                     hashed_pin TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
                                        id INTEGER PRIMARY KEY,
                                        user_id INTEGER NOT NULL,
                                        balance REAL NOT NULL DEFAULT 0,
                                        FOREIGN KEY (user_id) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS transactions (
                                            id INTEGER PRIMARY KEY,
                                            account_id INTEGER NOT NULL,
                                            type TEXT NOT NULL,
                                            amount REAL NOT NULL,
                                            timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                                            FOREIGN KEY (account_id) REFERENCES accounts(id)
    );
