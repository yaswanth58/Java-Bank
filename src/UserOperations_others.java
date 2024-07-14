import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserOperations_others implements ValidationInterface , Operations {
    private Connection connection;
    private int accountNumber;

    public UserOperations_others(int accountNumber, Connection connection) {
        this.connection = connection;
        this.accountNumber = accountNumber;
    }



    @Override
    public boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    @Override
    public String doHashing(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(password.getBytes());
            byte[] resultByteArray = messageDigest.digest();
            StringBuilder sb = new StringBuilder();

            for (byte b : resultByteArray) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void change_password() {
        Scanner scanner = new Scanner(System.in);
        Validation vd = new Validation();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your current password: ");
        String currentPassword = scanner.nextLine();
        currentPassword = doHashing(currentPassword);

        System.out.print("Enter your new password: ");
        String newPassword = scanner.nextLine();

        System.out.print("Enter confirm password: ");
        String confirmPassword = scanner.nextLine();

        if (!Objects.equals(newPassword, confirmPassword)) {
            System.out.println("Both passwords are not matching. Try again.");
            return;
        }
        newPassword = doHashing(newPassword);

        // Query to update password
        String updatePasswordQuery = "UPDATE new_user SET password = ? WHERE email = ? AND password = ?";
        try {
            PreparedStatement updateStatement = connection.prepareStatement(updatePasswordQuery);
            updateStatement.setString(1, newPassword);
            updateStatement.setString(2, email);
            updateStatement.setString(3, currentPassword);
            int rowsUpdated = updateStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Password changed successfully.");
            } else {
                System.out.println("Failed to change password. Please check your email and current password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void info() {
        String info_query = "SELECT * FROM new_user WHERE acc_no = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(info_query);
            preparedStatement.setInt(1, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String fathersName = resultSet.getString("father_name");
                String email = resultSet.getString("email");
                String phoneNumber = resultSet.getString("phone_number");
                String address = resultSet.getString("address");
                int accno = resultSet.getInt("acc_no");
                double balance = resultSet.getDouble("balance");
                String accstatus = resultSet.getString("status");
                System.out.println("Account Number : " + accno);
                System.out.println("Name: " + name);
                System.out.println("Father's Name : " + fathersName);
                System.out.println("Email: " + email);
                System.out.println("Phone Number: " + phoneNumber);
                System.out.println("Address: " + address);
                System.out.println("Balance: " + balance);
                System.out.println("Account Status : " + accstatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void transaction_history() throws SQLException {
        String query1 = "SELECT trans_id, credit_amt, debit_amt, curr_bal, created_at " +
                "FROM transactiontable WHERE acc_no ="+accountNumber;


        DisplayInterface di=(q1 -> {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(q1);

            // Get metadata to retrieve column names and count
            int columnCount = resultSet.getMetaData().getColumnCount();

            // Print table header
            System.out.println("Accounts Data ");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-15s", resultSet.getMetaData().getColumnName(i));
            }
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            // Print table rows
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.printf("%-15s", resultSet.getString(i));
                }
                System.out.println();
            }
        });
        di.printTable(query1);

    }
}
