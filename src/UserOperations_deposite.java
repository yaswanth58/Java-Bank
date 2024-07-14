import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserOperations_deposite {
    private Connection connection;
    private int accountNumber;

    public UserOperations_deposite(int accountNumber, Connection connection) {
        this.connection = connection;
        this.accountNumber = accountNumber;
    }
    public void deposit() {
        System.out.print("Enter amount to deposit: ");
        Scanner scanner = new Scanner(System.in);
        double amount = scanner.nextDouble();
        String selectQuery = "SELECT balance FROM new_user WHERE acc_no = ?";
        String updateQuery = "UPDATE new_user SET balance = ? WHERE acc_no = ?";
        try {
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setInt(1, accountNumber);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                double currentBalance = resultSet.getDouble("balance");
                double newBalance = currentBalance + amount;

                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setDouble(1, newBalance);
                updateStatement.setInt(2, accountNumber);
                int rowsUpdated = updateStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Deposit successful.");
                    System.out.println("New Balance: " + newBalance);
                    String insertTransactionQuery = "INSERT INTO transactiontable (acc_no,credit_amt,debit_amt,curr_bal,created_at) VALUES (?, ?, 0,?, NOW())";
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
                    System.out.println("Deposit failed. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
