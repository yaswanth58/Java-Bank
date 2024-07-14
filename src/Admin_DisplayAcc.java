import java.sql.*;
import java.util.Scanner;

public class Admin_DisplayAcc {
    private Connection connection;

    public Admin_DisplayAcc(Connection connection) {
        this.connection = connection;
    }
    public void displayAllAccounts() throws SQLException {
        String query2 = "SELECT acc_no, name, father_name, email, phone_number, address, balance, status FROM new_user";


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
        di.printTable(query2);



    }
}
