import java.sql.Connection;
import java.util.Objects;
import java.util.Scanner;
import java.sql.*;

public class UserOperations {
    private Connection connection;
    private int accountNumber;

    public UserOperations(int accountNumber, Connection connection) {
        this.connection = connection;
        this.accountNumber = accountNumber;
    }

    public Boolean checkStatus(int accountNumber) {
        try {
            String checkQuery = "SELECT status from new_user where acc_no=?";
            PreparedStatement selectStatement = connection.prepareStatement(checkQuery);
            selectStatement.setInt(1, accountNumber);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                String currentStatus = resultSet.getString("status");
                if (Objects.equals(currentStatus, "inactive")) {
                    System.out.println("Recipient's account has been closed hence you cannot transfer money");
                    return false;
                } else
                    return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Recipient account number is invalid");
        return false;
    }

    public void withdraw() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();

        String selectQuery = "SELECT balance FROM new_user WHERE acc_no = ?";
        String updateQuery = "UPDATE new_user SET balance = ? WHERE acc_no = ?";
        try {
            // Step 0: checking account status
            if (!checkStatus(accountNumber))
                return;

            // Step 1: Retrieve current balance
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setInt(1, accountNumber);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                double currentBalance = resultSet.getDouble("balance");
                // Check if sufficient balance
                if (currentBalance >= amount) {
                    double newBalance = currentBalance - amount;
                    // Step 2: Update balance in the database
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setDouble(1, newBalance);
                    updateStatement.setInt(2, accountNumber);
                    int rowsUpdated = updateStatement.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Withdrawal successful.");
                        System.out.println("New Balance: " + newBalance);
                        String insertTransactionQuery = "INSERT INTO transactiontable (acc_no,credit_amt,debit_amt,curr_bal,created_at) VALUES (?, 0, ?,?, NOW())";
                        try {
                            PreparedStatement preparedStatement = connection.prepareStatement(insertTransactionQuery);
                            preparedStatement.setInt(1, accountNumber);
                            preparedStatement.setDouble(2, amount);
                            preparedStatement.setDouble(3, newBalance);
                            preparedStatement.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Withdrawal failed. Please try again.");
                    }
                } else {
                    System.out.println("Insufficient balance.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void money_transfer() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter recipient's account number: ");
        int recipientAccountNumber = scanner.nextInt();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        String selectSenderQuery = "SELECT balance FROM new_user WHERE acc_no = ?";
        String selectRecipientQuery = "SELECT balance FROM new_user WHERE acc_no = ?";
        String updateSenderQuery = "UPDATE new_user SET balance = ? WHERE acc_no = ?";
        String updateRecipientQuery = "UPDATE new_user SET balance = ? WHERE acc_no = ?";

        try {

            if (!checkStatus(recipientAccountNumber))
                return;

            // Step 1: Retrieve current balances
            PreparedStatement selectSenderStatement = connection.prepareStatement(selectSenderQuery);
            selectSenderStatement.setInt(1, accountNumber);
            ResultSet senderResultSet = selectSenderStatement.executeQuery();

            PreparedStatement selectRecipientStatement = connection.prepareStatement(selectRecipientQuery);
            selectRecipientStatement.setInt(1, recipientAccountNumber);
            ResultSet recipientResultSet = selectRecipientStatement.executeQuery();

            if (senderResultSet.next() && recipientResultSet.next()) {
                double senderCurrentBalance = senderResultSet.getDouble("balance");
                double recipientCurrentBalance = recipientResultSet.getDouble("balance");

                // Check if sender has sufficient balance
                if (senderCurrentBalance >= amount) {
                    double senderNewBalance = senderCurrentBalance - amount;
                    double recipientNewBalance = recipientCurrentBalance + amount;
                    try {
                        // Update sender's balance
                        PreparedStatement updateSenderStatement = connection.prepareStatement(updateSenderQuery);
                        updateSenderStatement.setDouble(1, senderNewBalance);
                        updateSenderStatement.setInt(2, accountNumber);
                        int rowsUpdatedSender = updateSenderStatement.executeUpdate();

                        // Update recipient's balance
                        PreparedStatement updateRecipientStatement = connection.prepareStatement(updateRecipientQuery);
                        updateRecipientStatement.setDouble(1, recipientNewBalance);
                        updateRecipientStatement.setInt(2, recipientAccountNumber);
                        int rowsUpdatedRecipient = updateRecipientStatement.executeUpdate();
                        if (rowsUpdatedSender > 0 && rowsUpdatedRecipient > 0) {
                            System.out.println("Money transfer successful.");
                            System.out.println("Sender's New Balance: " + senderNewBalance);
                            String insertTransactionQuery = "INSERT INTO transactiontable (acc_no,credit_amt,debit_amt,curr_bal,created_at) VALUES (?, 0, ?,?, NOW())";
                            try {
                                PreparedStatement preparedStatement = connection.prepareStatement(insertTransactionQuery);
                                preparedStatement.setInt(1, accountNumber);
                                preparedStatement.setDouble(2, amount);
                                preparedStatement.setDouble(3, senderNewBalance);
                                preparedStatement.executeUpdate();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            String insertrecepientTransactionQuery = "INSERT INTO transactiontable (acc_no,credit_amt,debit_amt,curr_bal,created_at) VALUES (?, ?, 0,?, NOW())";
                            try {
                                PreparedStatement preparedStatement = connection.prepareStatement(insertrecepientTransactionQuery);
                                preparedStatement.setInt(1, recipientAccountNumber);
                                preparedStatement.setDouble(2, amount);
                                preparedStatement.setDouble(3, recipientNewBalance);
                                preparedStatement.executeUpdate();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Money transfer failed. Please try again.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Insufficient balance in sender's account.");
                }
            } else {
                System.out.println("Recipient account number not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void functions() throws SQLException {

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("1. Info");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Money Transfer");
            System.out.println("5. Change Password");
            System.out.println("6. Transaction History");
            System.out.println("7. Logout");
            System.out.print(" Enter your choice : ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    UserOperations_others ui = new UserOperations_others(accountNumber,connection);
                    ui.info();
                    break;
                case 2:
                    UserOperations_deposite ud = new UserOperations_deposite(accountNumber,connection);
                    ud.deposit();
                    break;
                case 3:
                    withdraw();
                    break;
                case 4:
                    money_transfer();
                    break;
                case 5:
                    UserOperations_others uo = new UserOperations_others(accountNumber,connection);
                    uo.change_password();
                    break;
                case 6:
                    UserOperations_others uoth = new UserOperations_others(accountNumber,connection);
                    uoth.transaction_history();
                    break;
                case 7:
                    return;
            }
        }
    }
}