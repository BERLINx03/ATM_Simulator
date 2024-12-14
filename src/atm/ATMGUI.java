package atm;

import javax.swing.*;
        import java.awt.*;
        import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import services.AccountService;

public class ATMGUI implements Runnable {

    private final AccountService accountService;
    private JFrame frame;
    private JTextField cardNumberField;
    private JPasswordField pinField;
    private JTextArea statusArea;

    public ATMGUI(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void run() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("ATM Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2));

        JLabel cardNumberLabel = new JLabel("Card Number:");
        cardNumberField = new JTextField();
        JLabel pinLabel = new JLabel("PIN:");
        pinField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton logoutButton = new JButton("Logout");
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton transferButton = new JButton("Transfer");
        JButton balanceButton = new JButton("Check Balance");

        statusArea = new JTextArea();
        statusArea.setEditable(false);

        panel.add(cardNumberLabel);
        panel.add(cardNumberField);
        panel.add(pinLabel);
        panel.add(pinField);
        panel.add(loginButton);
        panel.add(logoutButton);
        panel.add(depositButton);
        panel.add(withdrawButton);
        panel.add(transferButton);
        panel.add(balanceButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.add(new JScrollPane(statusArea), BorderLayout.SOUTH);

        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cardNumber = cardNumberField.getText();
                String pin = new String(pinField.getPassword());
                if (accountService.login(cardNumber, pin)) {
                    statusArea.append("Login successful!\n");
                } else {
                    statusArea.append("Login failed!\n");
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accountService.logout();
                statusArea.append("Logged out successfully!\n");
            }
        });

        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String amountStr = JOptionPane.showInputDialog(frame, "Enter amount to deposit:");
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (accountService.deposit(0, amount)) {
                        statusArea.append("Deposit successful!\n");
                    } else {
                        statusArea.append("Deposit failed!\n");
                    }
                } catch (Exception ex) {
                    statusArea.append("Invalid amount!\n");
                }
            }
        });

        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String amountStr = JOptionPane.showInputDialog(frame, "Enter amount to withdraw:");
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (accountService.withdraw(0, amount)) {
                        statusArea.append("Withdrawal successful!\n");
                    } else {
                        statusArea.append("Withdrawal failed!\n");
                    }
                } catch (Exception ex) {
                    statusArea.append("Invalid amount!\n");
                }
            }
        });

        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String recipientCard = JOptionPane.showInputDialog(frame, "Enter recipient's card number:");
                String amountStr = JOptionPane.showInputDialog(frame, "Enter amount to transfer:");
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (accountService.transferFunds(recipientCard, amount)) {
                        statusArea.append("Transfer successful!\n");
                    } else {
                        statusArea.append("Transfer failed!\n");
                    }
                } catch (Exception ex) {
                    statusArea.append("Invalid input!\n");
                }
            }
        });

        balanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double balance = accountService.getBalance();
                    statusArea.append("Current balance: " + balance + "\n");
                } catch (Exception ex) {
                    statusArea.append("Unable to retrieve balance!\n");
                }
            }
        });

        frame.setVisible(true);
    }

}
