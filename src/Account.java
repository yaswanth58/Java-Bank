

import java.sql.Connection;
import java.util.Scanner;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Account  implements ValidationInterface{
    private Connection connection;
    private Scanner sc;
    public Account(Connection connection) {
        this.connection = connection;
        this.sc = new Scanner(System.in);
    }
    Validation vd = new Validation();

    public void openAccount() {
        System.out.print("Enter Full Name: ");
        String userName = sc.nextLine();
        System.out.print("Enter Father's Name: ");
        String fatherName = sc.nextLine();
        System.out.print("Enter Email: ");
        String userEmail = sc.nextLine();

        while (!vd.isValidEmail(userEmail)) {
            System.out.println("Invalid email format!");
            System.out.print("Enter Email: ");

            userEmail = sc.nextLine();
        }

        System.out.print("Enter Aadhar Number: ");
        String userAadhar = sc.nextLine();

        while (!vd.isValidAadhar(userAadhar)) {
            System.out.println("Invalid Aadhar number format! must be 12 digit number ");
            System.out.print("Enter Aadhar: ");
            userAadhar = sc.nextLine();
        }

        System.out.print("Enter Phone Number: ");
        String phoneNumber = sc.nextLine();

        while (!vd.isValidPhoneNumber(phoneNumber)) {
            System.out.println("Invalid phone number format! must be 10 digit number");
            System.out.print("Enter phone: ");
            phoneNumber = sc.nextLine();
        }

        System.out.print("Enter Address: ");
        String address = sc.nextLine();

        System.out.print("Enter Password: ");
        String userPassword = sc.nextLine();

        while(!isValidPassword(userPassword)) {
            System.out.println("Invalid password format! Password must be at least 8 characters long, contain one uppercase letter, one lowercase letter, one number, and one special character.");
            System.out.print("Enter Password: ");
            userPassword = sc.nextLine();
        }
        userPassword = doHashing(userPassword);

        System.out.print("Enter Initial Balance only Deposits are allowed : ");
        Double initial_balance = sc.nextDouble();
        while (initial_balance < 0) {
            System.out.print("Enter Initial Balance only Deposits are allowed : ");
            initial_balance = sc.nextDouble();
        }

        try {
            // Check for duplicate user by Aadhar number
            String checkDuplicateQuery = "SELECT check_duplicateUser(?)";
            CallableStatement checkStmt = connection.prepareCall(checkDuplicateQuery);
            checkStmt.setString(1, userAadhar);

            int account_number=0;
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int accNo = rs.getInt(1);
                if (accNo != -1) {
                    account_number =accNo;
                    System.out.println("A/C can not be open, User already exists for this Aadhar number.");
                    return;
                }
            }

            String registerQuery = "INSERT INTO new_user (father_name, name, email, aadhar, phone_number, address, password, balance, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'active')";
            PreparedStatement preparedStatement = connection.prepareStatement(registerQuery);
            preparedStatement.setString(1, fatherName);
            preparedStatement.setString(2, userName);
            preparedStatement.setString(3, userEmail);
            preparedStatement.setString(4, userAadhar);
            preparedStatement.setString(5, phoneNumber);
            preparedStatement.setString(6, address);
            preparedStatement.setString(7, userPassword);
            preparedStatement.setDouble(8, initial_balance);
            int affectedRows = preparedStatement.executeUpdate();
            System.out.println("affected rows"+affectedRows);


            if (affectedRows > 0) {
                String acNoQuery = "SELECT acc_no from new_user where aadhar = ?";
                PreparedStatement preparedStatementac = connection.prepareStatement(acNoQuery);
                preparedStatementac.setString(1, userAadhar);
                ResultSet resultSet = preparedStatementac.executeQuery();

                if (resultSet.next()) {
                    account_number= resultSet.getInt("acc_no");
                } else {
                    System.out.println("No user found with the given Aadhar number.");

                }

                String insertTransactionQuery = "INSERT INTO transactiontable (acc_no,credit_amt,debit_amt,curr_bal,created_at) VALUES (?, ?, 0, ?, NOW())";
                try {
                    PreparedStatement preparedStatement2 = connection.prepareStatement(insertTransactionQuery);
                    preparedStatement2.setInt(1, account_number);
                    preparedStatement2.setDouble(2, initial_balance);
                    preparedStatement2.setDouble(3, initial_balance);
                    preparedStatement2.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                System.out.println("Registration Successful....");
                System.out.println("Hello "+userName+"! Your account has been oppened succesfully with a/c no "+account_number+" and your current balance is "+initial_balance);

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            System.out.println(e);
        }
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
}