import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User implements ValidationInterface {

    private Connection connection;
    public Scanner sc;

    public User(Connection connection) {
        this.connection = connection;
        this.sc = new Scanner(System.in);
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

    public String login() throws SQLException {

        System.out.print("Enter Account Number: ");
        int accountNumber = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        int acc_No = 0;
        String acc_Pass = "";

        try {
            File file = new File("C:\\Users\\DELL\\Bank_Management\\AdminInfo.txt");
            Scanner obj = new Scanner(file);
            String acc = obj.nextLine();
            acc_Pass = obj.nextLine();
            acc_No = Integer.parseInt(acc);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }

        if(accountNumber == acc_No && Objects.equals(password, acc_Pass)){
            System.out.println("Admin is Logged In! ");
            Admin_Operations aop = new Admin_Operations(connection);
            aop.functions();
            return null;
        }


        password = doHashing(password);

        String loginQuery = "SELECT * FROM new_user WHERE acc_no = ? AND password = ?;";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(loginQuery);
            preparedStatement.setInt(1, accountNumber);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String userName = resultSet.getString("name");
                System.out.println("Welcome, " + userName + "! You have successfully logged in. \n");

                UserOperations usropt = new UserOperations(accountNumber,connection);
                usropt.functions();

                return null;
            } else {
                System.out.println("Invalid account number or password. Please try again.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return null;
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

}

