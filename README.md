# Banking System Project

This Banking Management System is a console-based application developed in Java. The application allows users to create accounts, deposit and withdraw money, and view their account balance. And as well as allows admin to make changes in status of user's account. 

## Features for users : 

- **Create Account**: Users can create a new bank account.
- **Deposit Money**: Users can deposit money into their account.
- **Withdraw Money**: Users can withdraw money from their account.
- **View Balance**: Users can check their account balance.
- **Transfer Funds**: Users can transfer funds between accounts.
- **Transaction History**: Users can view the history of their transactions.

## Features for Admin :
- **Update**: Admin can Update user account.
- **close**: Admin can close user account.
- **Account information**: Admin can view user account information.
- **Transaction History**: Admin can view the history of users.

### Software Requirements : 

1. Java Development Kit (JDK) 8 :
   - For **Windows/Mac** : Download from Oracle's official site and follow the installation instructions.
   - For **Linux** : Install via your package manager.
2. Any Java IDE (IntelliJ IDEA, Eclipse, NetBeans, etc.) :
   - [IntelliJ IDEA](https://www.jetbrains.com/idea/download/?section=windows)
   - [Eclipse](https://www.eclipse.org/downloads/download.php?file=/oomph/epp/2024-06/R/eclipse-inst-jre-win64.exe)
   - [NetBeans](https://netbeans.apache.org/front/main/download/index.html)
3. My Sql workbench for database.
   - [MySQL](https://downloads.mysql.com/archives/installer/)
4. JDBC/ My SQL connector :
   - Install My SQL connector/J(JDBC Driver) from [MySQL Website](https://dev.mysql.com/downloads/installer/)
5. Git for cloning the repository.
   - Install from Git's latest version.
6. Add MySQL connector/J to the project structure in IntelliJ IDEA :
   - Navigate through project Structure -> Modules -> Dependencies
   - Add a downloaded MySQL connector .jar file

### Installation

1. **Clone the repository:**
    ```sh
    git clone https://github.com/yaswanth58/Java-Bank.git
    ```
2. **Navigate to the project directory:**
    ```sh
    cd Bank_management
    ```
3. **Open the project in your preferred IDE.**

### Database Connection

1. Create a new database "bank" and update the DB_Details file with your own database credentials:
     - db.url=jdbc:mysql://localhost:3306/bank
     - db.username: your_username
     - db.password: your_password
      
### Running the Application

1. **Compile the application:**
    ```sh
    javac main.java
    ```
2. **Run the application:**
    ```sh
    java main
    ```

### Project Structure

- **src/**: Contains the main source code of the application.
- **db_connection/** : Update db_details file with your own credentials.



### Contributing

Contributions are welcome! Please fork this repository and submit a pull request for any changes or improvements.

### License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


Thank you for checking out my Banking System Project! I hope you find it useful and educational.
