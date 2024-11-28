import java.sql.*;
import java.util.Scanner;

public class User {
    private static Connection connection = null;

    public User(Connection conn) {
        connection = conn;
    }

    // DONE
    public static void user_login(Scanner scanner) {
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
                showUserAccountMenu(accountNumber);
            } else {
                System.out.println("Invalid Account Number or Password");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showUserAccountMenu(String accountNumber) {
        Scanner scanner = new Scanner(System.in);
        boolean logout = false;

        while (!logout) {
            System.out.println("\n--- Account Menu ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Transfer Funds");
            System.out.println("3. Transaction History");
            System.out.println("4. Apply Loan");
            System.out.println("5. Having any issue? Raise a ticket");
            System.out.println("6. Check Loan Status");
            System.out.println("7. Check Ticket Status");
            System.out.println("8. Logout");
            System.out.print("Enter your choice: ");

            String choice = scanner.next();
            switch (choice) {
                case "1" -> checkUserBalance(accountNumber);
                case "2" -> userTransferFunds(accountNumber, scanner);
                case "3" -> viewUserTransactionHistory(accountNumber);
                case "4" -> userLoanApply(accountNumber, scanner);
                case "5" -> raiseTicket(accountNumber, scanner);
                case "6" -> checkLoanStatus(accountNumber, scanner);
                case "7" -> checkTicketStatus(accountNumber, scanner);
                case "8" -> logout = true;
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    // done
    private static void checkTicketStatus(String accountNumber, Scanner sc) {
        try {
            String query = "SELECT * FROM tickets WHERE user_info = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, accountNumber);

            ResultSet rs = pstmt.executeQuery();
            System.out.println("\n--- Ticket Status ---");
            if (!rs.isBeforeFirst()) {
                System.out.println("No tickets found for your account.");
            } else {
                while (rs.next()) {
                    int ticketId = rs.getInt("ticket_id");
                    String ticketDesc = rs.getString("ticket_description");
                    String ticketStatus = rs.getString("ticket_status");
                    // String raisedDate = rs.getString("raised_date");

                    System.out.printf("\nTicket ID: %d | Description: %s | Status: %s ",
                            ticketId, ticketDesc, ticketStatus);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching ticket status: " + e.getMessage());
        }
    }

    // pending
    private static void checkLoanStatus(String accountNumber, Scanner sc) {
        try {
            String query = "SELECT * FROM loan WHERE userid = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, accountNumber);

            ResultSet rs = pstmt.executeQuery();
            System.out.println("\n--- Loan Application Status ---");
            if (!rs.isBeforeFirst()) {
                System.out.println("No loan applications found for your account.");
            } else {
                while (rs.next()) {
                    int loanId = rs.getInt("loanid");
                    // String loanType = rs.getString("loan_type");
                    double loanAmount = rs.getDouble("loanamount");
                    String loanStatus = rs.getString("loanstatus");
                    // String appliedDate = rs.getString("applied_date");

                    System.out.printf("Loan ID: %d  | Amount: $%.2f | Status: %s ",
                            loanId, loanAmount, loanStatus);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching loan status: " + e.getMessage());
        }
    }

    // done
    private static void raiseTicket(String accountNumber, Scanner sc) {
        try {
            System.out.println("\n--- Raise a Ticket ---");
            System.out.print("Enter a brief description of your issue: ");
            sc.nextLine(); // Consume leftover newline
            String description = sc.nextLine();

            String getMaxAccNumQuery = "SELECT MAX(ticket_id) as maxticket FROM tickets";
            PreparedStatement maxAccNumStmt = connection.prepareStatement(getMaxAccNumQuery);
            ResultSet rs = maxAccNumStmt.executeQuery();

            int startingPoint = 5548554; // Starting account number
            int ticketRef = startingPoint; // Default to starting point if no users exist

            if (rs.next()) {
                int maxAccNum = rs.getInt("maxticket");
                ticketRef = Math.max(maxAccNum + 1, startingPoint); // Ensure it starts from the defined starting point
            }
            String query = "INSERT INTO tickets (ticket_id, ticket_description, ticket_status, user_info) " +
                    "VALUES (?, ?, 'Pending',?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, ticketRef);
            pstmt.setString(2, description);
            pstmt.setString(3, accountNumber);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Your ticket has been raised successfully!");
                System.out.println("Ticket ID: " + ticketRef);
            } else {
                System.out.println("Failed to raise the ticket. Please try again.");
            }
            // Display the ticket ID

        } catch (SQLException e) {
            System.out.println("Error raising ticket: " + e.getMessage());
        }
    }

    // DONE
    private static void checkUserBalance(String accountNumber) {
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
    private static void userTransferFunds(String sourceAccount, Scanner scanner) {
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
            double amount = 0;
            while (true) {
                System.out.print("Enter transfer amount: ");
                if (scanner.hasNextDouble()) {

                    amount = scanner.nextDouble();
                    break;
                } else {
                    System.out.println("Invalid Transfer Amount");
                    scanner.next();

                }
            }

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
        } catch (Exception e) {
            System.out.println("\nInvalid Entry. Please try again!");
        }
    }

    // done
    private static void viewUserTransactionHistory(String accountNumber) {
        try {
            // Prompt the admin to enter the user ID for transaction history

            // SQL query to fetch transactions involving the specified user as sender or
            // receiver
            String query = "SELECT transaction_id, sender_id, receiver_id, amount, transaction_date " +
                    "FROM transactions " +
                    "WHERE sender_id = ? OR receiver_id = ? " +
                    "ORDER BY transaction_date DESC";

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, accountNumber);

            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- Transaction History ---");
            boolean hasTransactions = false;

            // Iterate through the results and display each transaction
            while (rs.next()) {
                hasTransactions = true;
                int transactionId = rs.getInt("transaction_id");
                int senderId = rs.getInt("sender_id");
                int receiverId = rs.getInt("receiver_id");
                double amount = rs.getDouble("amount");
                Timestamp transactionDate = rs.getTimestamp("transaction_date");

                // Display transaction details
                System.out.printf("Transaction ID: %d | Sender ID: %d | Receiver ID: %d | Amount: %.2f | Date: %s%n",
                        transactionId, senderId, receiverId, amount, transactionDate);
            }

            if (!hasTransactions) {
                System.out.println("No transactions found for User ID: " + accountNumber);
            }

            // Close resources
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Error fetching transaction history: " + e.getMessage());
        }
    }

    // done with exc

    private static void userLoanApply(String accountNumber, Scanner scanner) {
        try {
            while (true) {

                System.err.println("\nSelect loan type from below:");
                System.out.println("1. Personal with 10% interest rate");
                System.out.println("2. Car with 12% interest rate");
                System.out.println("3. Home with 15% interest rate");
                // System.out.println("4. with 16% interest rate");
                System.out.print("Enter your choice: ");
                String loanType = scanner.next();
                int rate = 0;
                switch (loanType) {
                    case "1":
                        rate = 10;
                        break;

                    case "2":
                        rate = 12;
                        break;

                    case "3":
                        rate = 15;
                        break;

                    default:
                        System.err.println("\nWrong Input!!! Select number from above catalog.");
                        continue;
                }

                double loanAmount = 0;
                while (true) {
                    System.out.print("Enter loan amount: ");
                    if (scanner.hasNextDouble()) {

                        loanAmount = scanner.nextDouble();
                        break;
                    } else {
                        System.out.println("Invalid Loan Amount");
                        scanner.next();

                    }
                }
                int loanDuration = 0;
                while (true) {
                    System.out.print("Enter loan duration (in months): ");
                    if (scanner.hasNextInt()) {
                        loanDuration = scanner.nextInt();
                        break;
                    } else {
                        System.out.println("Invalid Loan Duration");
                        scanner.next();

                    }
                }
                // user id- 5548555
                // pass - 123

                scanner.nextLine(); // Consume newline

                System.out.print("Enter loan description: ");
                String loanDescription = scanner.nextLine();

                // EMI calculation for simple interest
                double emi = 0;
                double interest = (loanAmount * rate * loanDuration) / 1200;

                emi = (loanAmount + interest) / loanDuration;

                String query = "INSERT into loan(loandescription,loanamount,loanduration,loanemi,loanstatus,userid) values ('"
                        + loanDescription + "'," + loanAmount + "," + loanDuration + "," + emi + ",'PENDING',"
                        + accountNumber + ")";

                PreparedStatement pstmt = connection.prepareStatement(query);

                ResultSet rs = pstmt.executeQuery();

                System.err.println("Thank you for apply. We'll get back to you for any update.");
                break;

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Something went wrong.");
            // TODO: handle exception
        }
    }

}
