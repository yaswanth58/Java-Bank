import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Admin_Update{
    private Connection connection;
    private Scanner sc;

    public Admin_Update(Connection connection) {
        this.connection = connection;
        this.sc = new Scanner(System.in);
    }
    public void changeInfo(String updateQuery, String updated, int acc_num) {
        try {
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setString(1, updated);
            updateStatement.setInt(2, acc_num);
            int rowsAffected = updateStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Changes Updated Successfully !");
            } else {
                System.out.println("Try Again.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update() {
//        Scanner sc = new Scanner(System.in);
        System.out.println("Enter account number: ");
        int acc_num = sc.nextInt();
        System.out.println("Choose what to update -- ");
        System.out.println("1. Phone Number ");
        System.out.println("2. Address  ");
        System.out.println("3. Email  ");
        System.out.println("Enter your choice: ");
        int choice1 = sc.nextInt();

        switch (choice1) {
            case 1:
                System.out.println("Enter new number:  ");
                Scanner obj = new Scanner(System.in);
                String updated = obj.nextLine();
                String updateQuery = "UPDATE new_user SET phone_number = ? WHERE acc_no = ?";

                changeInfo(updateQuery, updated, acc_num);
                break;
            case 2:
                System.out.println("Enter new address:  ");
                Scanner obj2 = new Scanner(System.in);
                String updated2 = obj2.nextLine();
                String updateQuery2 = "UPDATE new_user SET address = ? WHERE acc_no = ?";

                changeInfo(updateQuery2, updated2, acc_num);
                break;
            case 3:
                System.out.println("Enter new Email:  ");
                Scanner obj3 = new Scanner(System.in);
                String updated3 = obj3.nextLine();

                Validation vl = new Validation();
                if (!vl.isValidEmail(updated3)) {
                    System.out.println("Email is not valid");
                    return;
                }

                String updateQuery3 = "UPDATE new_user SET email = ? WHERE acc_no = ?";
                changeInfo(updateQuery3, updated3, acc_num);
                break;
        }
    }
}
