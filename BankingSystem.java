import java.sql.*;
import java.util.Scanner;

public class BankingSystem {
    private static Connection connection = null;
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:ORCL";
    private static final String USER = "system";
    private static final String PASS = "Test@123";

    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Scanner scanner = new Scanner(System.in);
            boolean exit = false;

            while (!exit) {
                displayMainMenu();
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> login(scanner);
                    case 2 -> registerAccount(scanner);
                    case 3 -> exit = true;
                    default -> System.out.println("Invalid option. Try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n--- Online Banking System ---");
        System.out.println("1. Login");
        System.out.println("2. Register New Account");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }

    // DONE
    private static void login(Scanner scanner) {
        try {
            System.out.print("Enter Account Number: ");
            String accountNumber = scanner.next();
            System.out.print("Enter Password: ");
            String pass = scanner.next();

            String query = "SELECT * FROM users WHERE user_accnum = '" + accountNumber + "' AND user_pass = '" + pass
                    + "'";
            PreparedStatement pstmt = connection.prepareStatement(query);

            ResultSet rs = pstmt.executeQuery();
            Boolean resBool = rs.next();
            if (resBool) {
                String customerName = rs.getString("user_name");
                System.out.println("\nLogin Successful. Welcome, " + customerName);
                showAccountMenu(accountNumber);
            } else {
                System.out.println("Invalid Account Number or Password");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void registerAccount(Scanner scanner) {
        try {
            System.out.print("Enter Full Name: ");
            scanner.nextLine();
            String customerName = scanner.nextLine();
            System.out.print("Create Password: ");
            String pass = scanner.next();
            System.out.print("Initial Deposit Amount: ");
            double balance = scanner.nextDouble();

            String query = "INSERT INTO accounts (customer_name, pass, balance) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, customerName);
            pstmt.setString(2, pass);
            pstmt.setDouble(3, balance);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    String accountNumber = rs.getString(1);
                    System.out.println("Account Created Successfully!");
                    System.out.println("Your Account Number is: " + accountNumber);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showAccountMenu(String accountNumber) {
        Scanner scanner = new Scanner(System.in);
        boolean logout = false;

        while (!logout) {
            System.out.println("\n--- Account Menu ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer Funds");
            System.out.println("5. Transaction History");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> checkBalance(accountNumber);
                case 2 -> deposit(accountNumber, scanner);
                case 3 -> withdraw(accountNumber, scanner);
                case 4 -> transferFunds(accountNumber, scanner);
                case 5 -> viewTransactionHistory(accountNumber);
                case 6 -> logout = true;
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    // DONE
    private static void checkBalance(String accountNumber) {
        try {
            String query = "SELECT user_bal FROM users WHERE user_accnum = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            Boolean boolRs = rs.next();
            if (boolRs) {
                double balance = rs.getDouble("user_bal");
                System.out.printf("\nCurrent Balance: $%.2f%n", balance);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // DONE
    private static void deposit(String accountNumber, Scanner scanner) {
        try {
            System.out.print("Enter deposit amount: ");
            double amount = scanner.nextDouble();

            String updateQuery = "UPDATE users SET user_bal = user_bal + ? WHERE user_accnum = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setDouble(1, amount);
            updateStmt.setString(2, accountNumber);
            updateStmt.executeUpdate();

            String insertTransactionQuery = "INSERT INTO transactions (sender_id, receiver_id, amount,trans_type) VALUES (?,?,?, 'DEPOSIT')";
            PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery);
            transactionStmt.setString(1, accountNumber);
            transactionStmt.setString(2, accountNumber);
            transactionStmt.setDouble(3, amount);
            transactionStmt.executeUpdate();

            System.out.println("\nDeposit successful!");
        } catch (SQLException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    // DONE
    private static void withdraw(String accountNumber, Scanner scanner) {
        try {
            System.out.print("Enter withdrawal amount: ");
            double amount = scanner.nextDouble();

            String checkBalanceQuery = "SELECT user_bal FROM users WHERE user_accnum = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkBalanceQuery);
            checkStmt.setString(1, accountNumber);
            ResultSet rs = checkStmt.executeQuery();
            Boolean booRs = rs.next();
            if (booRs) {
                double currentBalance = rs.getDouble("user_bal");
                if (currentBalance >= amount) {
                    String updateQuery = "UPDATE users SET user_bal = user_bal - ? WHERE user_accnum = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                    updateStmt.setDouble(1, amount);
                    updateStmt.setString(2, accountNumber);
                    updateStmt.executeUpdate();

                    String insertTransactionQuery = "INSERT INTO transactions (sender_id, receiver_id, amount,trans_type) VALUES (?,?,?, 'WITHDRAWAL')";
                    PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery);
                    transactionStmt.setString(1, accountNumber);
                    transactionStmt.setString(2, accountNumber);
                    transactionStmt.setDouble(3, amount);
                    transactionStmt.executeUpdate();

                    System.out.println("\nWithdrawal successful!");
                } else {
                    System.out.println("\nInsufficient funds!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // DONE
    private static void transferFunds(String sourceAccount, Scanner scanner) {
        try {
            System.out.print("Enter destination account number: ");
            String destinationAccount = scanner.next();

            String checkDestinationQuery = "SELECT * FROM users WHERE user_accnum = ?";
            PreparedStatement checkDestStmt = connection.prepareStatement(checkDestinationQuery);
            checkDestStmt.setString(1, destinationAccount);
            ResultSet destRs = checkDestStmt.executeQuery();
            Boolean checke = destRs.next();
            if (!checke) {
                System.out.println("\nDestination account does not exist. Transfer aborted.");
                return;
            }
            System.out.print("Enter transfer amount: ");
            double amount = scanner.nextDouble();
            System.out.print("Enter your password: ");
            String pass = scanner.next();

            String checkBalanceQuery = "SELECT * FROM users WHERE user_accnum = ? and user_pass= ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkBalanceQuery);
            checkStmt.setString(1, sourceAccount);
            checkStmt.setString(2, pass);
            ResultSet rs = checkStmt.executeQuery();
            Boolean ifUserPassOk = rs.next();
            if (ifUserPassOk) {
                double currentBalance = rs.getDouble("user_bal");
                if (currentBalance >= amount) {
                    connection.setAutoCommit(false);

                    String deductQuery = "UPDATE users SET user_bal = user_bal - ? WHERE user_accnum = ?";
                    PreparedStatement deductStmt = connection.prepareStatement(deductQuery);
                    deductStmt.setDouble(1, amount);
                    deductStmt.setString(2, sourceAccount);
                    deductStmt.executeUpdate();

                    String creditQuery = "UPDATE users SET user_bal = user_bal + ? WHERE user_accnum = ?";
                    PreparedStatement creditStmt = connection.prepareStatement(creditQuery);
                    creditStmt.setDouble(1, amount);
                    creditStmt.setString(2, destinationAccount);
                    creditStmt.executeUpdate();

                    String insertTransactionQuery = "INSERT INTO transactions (sender_id, receiver_id, amount,trans_type) VALUES (?,?,?, 'TRANSFER')";
                    PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery);
                    transactionStmt.setString(1, sourceAccount);
                    transactionStmt.setString(2, destinationAccount);
                    transactionStmt.setDouble(3, amount);
                    transactionStmt.executeUpdate();

                    connection.commit();
                    System.out.println("\nTransfer successful!");
                } else {
                    System.out.println("\nInsufficient funds!");
                }
            } else {
                System.out.println("\nSource account does not exist OR Invalid Password!");
            }

            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("\nError rolling back transaction: " + ex.getMessage());
            }
            System.out.println("\nError: " + e.getMessage());
        }
    }

    // private static void transferFunds(String sourceAccount, Scanner scanner) {
    // try {
    // System.out.print("Enter destination account number: ");
    // String destinationAccount = scanner.next();
    // System.out.print("Enter transfer amount: ");
    // double amount = scanner.nextDouble();

    // String checkBalanceQuery = "SELECT balance FROM accounts WHERE account_number
    // = ?";
    // PreparedStatement checkStmt = connection.prepareStatement(checkBalanceQuery);
    // checkStmt.setString(1, sourceAccount);
    // ResultSet rs = checkStmt.executeQuery();

    // if (rs.next()) {
    // double currentBalance = rs.getDouble("balance");
    // if (currentBalance >= amount) {
    // connection.setAutoCommit(false);

    // String deductQuery = "UPDATE accounts SET balance = balance - ? WHERE
    // account_number = ?";
    // PreparedStatement deductStmt = connection.prepareStatement(deductQuery);
    // deductStmt.setDouble(1, amount);
    // deductStmt.setString(2, sourceAccount);
    // deductStmt.executeUpdate();

    // String creditQuery = "UPDATE accounts SET balance = balance + ? WHERE
    // account_number = ?";
    // PreparedStatement creditStmt = connection.prepareStatement(creditQuery);
    // creditStmt.setDouble(1, amount);
    // creditStmt.setString(2, destinationAccount);
    // creditStmt.executeUpdate();

    // String insertTransactionQuery = "INSERT INTO transactions (account_number,
    // type, amount, destination_account) VALUES (?, 'TRANSFER', ?, ?)";
    // PreparedStatement transactionStmt =
    // connection.prepareStatement(insertTransactionQuery);
    // transactionStmt.setString(1, sourceAccount);
    // transactionStmt.setDouble(2, amount);
    // transactionStmt.setString(3, destinationAccount);
    // transactionStmt.executeUpdate();

    // connection.commit();
    // System.out.println("Transfer successful!");
    // } else {
    // System.out.println("Insufficient funds!");
    // }
    // }

    // connection.setAutoCommit(true);
    // } catch (SQLException e) {
    // try {
    // connection.rollback();
    // } catch (SQLException ex) {
    // System.out.println("Error rolling back transaction: " + ex.getMessage());
    // }
    // System.out.println("Error: " + e.getMessage());
    // }
    // }

    private static void viewTransactionHistory(String accountNumber) {
        try {
            String query = "SELECT type, amount, transaction_date FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC LIMIT 10";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- Transaction History ---");
            while (rs.next()) {
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                Timestamp transactionDate = rs.getTimestamp("transaction_date");

                System.out.printf("%s: $%.2f on %s%n", type, amount, transactionDate);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}