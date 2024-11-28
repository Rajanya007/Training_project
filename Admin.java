import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Admin {
    private static Connection connection = null;

    public Admin(Connection conn) {
        connection = conn;
    }

    public static void adminLogin(Scanner scanner) {
        try {
            System.out.print("Enter Admin Id: ");
            String accountNumber = scanner.next();
            System.out.print("Enter PIN: ");
            String pin = scanner.next();

            String query = "SELECT * FROM admin_table WHERE admin_name = '" + accountNumber
                    + "' AND admin_password = '" + pin + "'";
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            boolean resBool = rs.next();

            if (resBool) {
                String customerName = rs.getString("admin_name");
                System.out.println("Login Successful. Welcome, " + customerName);
                showAdminAccountMenu(accountNumber);
            } else {
                System.out.println("Invalid Account Number or PIN");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void registerUser(Scanner sc) {
        try {
            System.out.println("\n--- Register New User ---");

            // Prompt for user details
            System.out.print("Enter Full Name: ");
            sc.nextLine(); // Consume leftover newline
            String userName = sc.nextLine();

            System.out.print("Enter Address: ");
            String userAddress = sc.nextLine();

            System.out.print("Enter Contact Number: ");
            String userPhone = sc.nextLine();

            System.out.print("Enter Aadhaar Number: ");
            String aadharNum = sc.nextLine();

            System.out.print("Enter PAN Number: ");
            String panNum = sc.nextLine();

            System.out.print("Create Password: ");
            String userPass = sc.nextLine();

            System.out.print("Enter Initial Deposit Amount: ");
            double userBal = sc.nextDouble();

            // Validate initial deposit
            if (userBal < 500) {
                System.out.println("Error: Minimum deposit amount is ₹500.");
                return;
            }

            // Fetch the current maximum account number from the database
            String getMaxAccNumQuery = "SELECT MAX(USER_ACCNUM) AS MAX_ACCNUM FROM users";
            PreparedStatement maxAccNumStmt = connection.prepareStatement(getMaxAccNumQuery);
            ResultSet rs = maxAccNumStmt.executeQuery();

            long startingPoint = 5548554; // Starting account number
            long userAccNum = startingPoint; // Default to starting point if no users exist

            if (rs.next()) {
                long maxAccNum = rs.getLong("MAX_ACCNUM");
                userAccNum = Math.max(maxAccNum + 1, startingPoint); // Ensure it starts from the defined starting point
            }

            // Insert the user details into the database
            String insertQuery = "INSERT INTO users (USER_ACCNUM, USER_NAME, USER_BAL, USER_RAISEDTICK, " +
                    "USER_ADDRESS, USER_PHONE, ADHAR_NUM, PAN_NUM, USER_PASS) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            pstmt.setLong(1, userAccNum); // Use the calculated account number
            pstmt.setString(2, userName);
            pstmt.setDouble(3, userBal);
            pstmt.setInt(4, 0); // Initially no raised tickets
            pstmt.setString(5, userAddress);
            pstmt.setString(6, userPhone);
            pstmt.setString(7, aadharNum);
            pstmt.setString(8, panNum);
            pstmt.setString(9, userPass);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User registration successful!");
                System.out.println("Generated Account Number: " + userAccNum);
            } else {
                System.out.println("Error: Failed to register the user.");
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
        }
    }

    private static void showAdminAccountMenu(String accountNumber) {
        Scanner scanner = new Scanner(System.in);
        boolean logout = false;

        while (!logout) {
            System.out.println("\n--- Admin Account Menu ---");
            System.out.println("1. Check  Complete User Data");
            System.out.println("2. Approve Loan");
            System.out.println("3. Update KYC details");
            System.out.println("4. Transfer Funds");
            System.out.println("5. Transaction History");
            System.out.println("6. Clear Pending Tickets");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");

            String choice = scanner.next();

            switch (choice) {
                case "1" -> checkUserData(accountNumber, scanner);
                case "2" -> approveLoan(scanner);
                case "3" -> updateKyc(accountNumber, scanner);
                case "4" -> transferFunds(accountNumber, scanner);
                case "5" -> viewTransactionHistory(scanner);
                case "6" -> clearPendingTickets(scanner);
                case "7" -> logout = true;
                default -> System.out.println("Invalid option. Try again.");
            }

        }
    }

    private static void approveLoan(Scanner sc) {
        try {
            // Fetch all pending tickets
            String fetchQuery = "SELECT * FROM loan WHERE loanstatus = 'VERIFIED'";
            PreparedStatement fetchStmt = connection.prepareStatement(fetchQuery);
            ResultSet rs = fetchStmt.executeQuery();

            System.out.println("\n--- Pending Loan ---");
            List<Integer> loanList = new ArrayList<>();
            while (rs.next()) {
                int loanId = rs.getInt("loanId");
                String loanDescription = rs.getString("loanDescription");
                loanList.add(loanId);
                System.out.println("Loan ID: " + loanId + " | Description: " + loanDescription); // to be continues
            }

            if (loanList.isEmpty()) {
                System.out.println("No pending loan found.");
            } else {
                // Ask user to decide which loan(s) to clear
                System.out.print("\nEnter the loan ID(s) to clear (comma-separated, or type 'all' to clear all): ");
                sc.nextLine(); // Clear scanner buffer
                String input = sc.nextLine();

                List<Integer> loansToClear = new ArrayList<>();
                if (input.equalsIgnoreCase("all")) {
                    loansToClear.addAll(loanList);
                } else {
                    // Parse user input for specific loan IDs
                    String[] inputIds = input.split(",");
                    for (String id : inputIds) {
                        try {
                            int loanId = Integer.parseInt(id.trim());
                            if (loanList.contains(loanId)) {
                                loansToClear.add(loanId);
                            } else {
                                System.out.println("Invalid Loan ID: " + loanId);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input: " + id + " is not a valid number.");
                        }
                    }
                }

                // Clear selected loan
                for (int loanId : loansToClear) {
                    String clearQuery = "UPDATE loan SET loanstatus='APPROVED' WHERE loanid=" + loanId;
                    PreparedStatement clearStmt = connection.prepareStatement(clearQuery);
                    // clearStmt.setInt(1, loanId);

                    int rowsAffected = clearStmt.executeUpdate();
                    if (rowsAffected > 0) {

                        System.out.println("Loan ID " + loanId + " has been approved.");
                    } else {
                        System.out.println("Failed to verify Loan ID " + loanId + ".");
                    }
                    clearStmt.close();
                }
            }

            rs.close();
            fetchStmt.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void checkUserData(String accountNumber, Scanner sc) {
        try {
            System.out.print("Enter Account Number of the User: ");
            int userAcc = sc.nextInt();
            String query = "SELECT * FROM users WHERE user_accnum = " + userAcc;
            PreparedStatement pstmt = connection.prepareStatement(query);
            // pstmt.setString(1, userAcc);
            ResultSet rs = pstmt.executeQuery();
            boolean userqbool = rs.next();
            if (userqbool) {
                System.out.println("Required Details");
                String userAccNum = rs.getString("user_accNum");
                String username = rs.getString("user_name");
                Double bal = rs.getDouble("user_bal");
                Integer pendingTickets = rs.getInt("user_raisedTick");
                Boolean isKycDone = rs.getBoolean("isKycDone");
                System.out.println("User Account Number: " + userAccNum);
                System.out.println("User Name: " + username);
                System.out.println("User Account Balance: " + bal);
                System.out.println("User Account Number: " + userAccNum);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deposit(String accountNumber, Scanner scanner) {
        try {
            System.out.print("Enter deposit amount: ");
            double amount = scanner.nextDouble();

            String updateQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setDouble(1, amount);
            updateStmt.setString(2, accountNumber);
            updateStmt.executeUpdate();

            String insertTransactionQuery = "INSERT INTO transactions (account_number, type, amount) VALUES (?, 'DEPOSIT', ?)";
            PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery);
            transactionStmt.setString(1, accountNumber);
            transactionStmt.setDouble(2, amount);
            transactionStmt.executeUpdate();

            System.out.println("Deposit successful!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateKyc(String accountNumber, Scanner scanner) {
        try {
            System.out.print("Enter account number of a user: ");
            int userAcc = scanner.nextInt();
            String query = "SELECT * FROM users WHERE user_accnum = " + userAcc;
            PreparedStatement updatestmt = connection.prepareStatement(query);
            // checkStmt.setString(1, accountNumber);
            ResultSet rsp = updatestmt.executeQuery();
            Boolean boolRes = rsp.next();
            if (boolRes) {
                Integer kycDetail = rsp.getInt("isKycDone");
                if (kycDetail == 0) {
                    String queryUpdate = "Update users SET isKycDone = 1 WHERE user_accnum = " + userAcc;
                    PreparedStatement stmt = connection.prepareStatement(queryUpdate);
                    // checkStmt.setString(1, accountNumber);
                    ResultSet res = stmt.executeQuery();
                    Boolean boolResp = res.next();
                    if (boolResp) {
                        System.out.println("KYC updated");
                    } else {
                        System.out.println("Error");
                    }

                } else {
                    System.out.println("KYC for this account is already done!!");
                }

            } else {
                System.out.println("Incorrect account number. Please try again! ");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    private static void viewTransactionHistory(Scanner sc) {
        try {
            // Prompt the admin to enter the user ID for transaction history
            System.out.print("Enter the User ID to view transaction history: ");
            int userId = sc.nextInt();

            // SQL query to fetch transactions involving the specified user as sender or
            // receiver
            String query = "SELECT transaction_id, sender_id, receiver_id, amount, transaction_date " +
                    "FROM transactions " +
                    "WHERE sender_id = ? OR receiver_id = ? " +
                    "ORDER BY transaction_date DESC";

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);

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
                System.out.println("No transactions found for User ID: " + userId);
            }

            // Close resources
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Error fetching transaction history: " + e.getMessage());
        }
    }

    private static void clearPendingTickets(Scanner sc) {
        try {
            // Fetch all pending tickets
            String fetchQuery = "SELECT ticket_id, ticket_description FROM tickets WHERE ticket_status = 'Pending'";
            PreparedStatement fetchStmt = connection.prepareStatement(fetchQuery);
            ResultSet rs = fetchStmt.executeQuery();

            System.out.println("\n--- Pending Tickets ---");
            List<Integer> ticketIds = new ArrayList<>();
            while (rs.next()) {
                int ticketId = rs.getInt("ticket_id");
                String ticketDescription = rs.getString("ticket_description");
                ticketIds.add(ticketId);
                System.out.println("Ticket ID: " + ticketId + " | Description: " + ticketDescription);
            }

            if (ticketIds.isEmpty()) {
                System.out.println("No pending tickets found.");
            } else {
                // Ask user to decide which ticket(s) to clear
                System.out.print("\nEnter the Ticket ID(s) to clear (comma-separated, or type 'all' to clear all): ");
                sc.nextLine(); // Clear scanner buffer
                String input = sc.nextLine();

                List<Integer> ticketsToClear = new ArrayList<>();
                if (input.equalsIgnoreCase("all")) {
                    ticketsToClear.addAll(ticketIds);
                } else {
                    // Parse user input for specific Ticket IDs
                    String[] inputIds = input.split(",");
                    for (String id : inputIds) {
                        try {
                            int ticketId = Integer.parseInt(id.trim());
                            if (ticketIds.contains(ticketId)) {
                                ticketsToClear.add(ticketId);
                            } else {
                                System.out.println("Invalid Ticket ID: " + ticketId);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input: " + id + " is not a valid number.");
                        }
                    }
                }

                // Clear selected tickets
                for (int ticketId : ticketsToClear) {
                    String clearQuery = "UPDATE tickets SET ticket_status = 'Resolved' WHERE ticket_id = ?";
                    PreparedStatement clearStmt = connection.prepareStatement(clearQuery);
                    clearStmt.setInt(1, ticketId);

                    int rowsAffected = clearStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Ticket ID " + ticketId + " has been cleared.");
                    } else {
                        System.out.println("Failed to clear Ticket ID " + ticketId + ".");
                    }
                    clearStmt.close();
                }
            }

            rs.close();
            fetchStmt.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void withdraw(String accountNumber, Scanner scanner) {
        try {
            System.out.print("Enter withdrawal amount: ");
            double amount = scanner.nextDouble();

            String checkBalanceQuery = "SELECT balance FROM accounts WHERE account_number = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkBalanceQuery);
            checkStmt.setString(1, accountNumber);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                if (currentBalance >= amount) {
                    String updateQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                    updateStmt.setDouble(1, amount);
                    updateStmt.setString(2, accountNumber);
                    updateStmt.executeUpdate();

                    String insertTransactionQuery = "INSERT INTO transactions (account_number, type, amount) VALUES (?, 'WITHDRAWAL', ?)";
                    PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery);
                    transactionStmt.setString(1, accountNumber);
                    transactionStmt.setDouble(2, amount);
                    transactionStmt.executeUpdate();

                    System.out.println("Withdrawal successful!");
                } else {
                    System.out.println("Insufficient funds!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void transferFunds(String sourceAccount, Scanner scanner) {
        try {
            System.out.print("Enter destination account number: ");
            String destinationAccount = scanner.next();
            System.out.print("Enter transfer amount: ");
            double amount = scanner.nextDouble();

            String checkBalanceQuery = "SELECT balance FROM accounts WHERE account_number = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkBalanceQuery);
            checkStmt.setString(1, sourceAccount);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                if (currentBalance >= amount) {
                    connection.setAutoCommit(false);

                    String deductQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                    PreparedStatement deductStmt = connection.prepareStatement(deductQuery);
                    deductStmt.setDouble(1, amount);
                    deductStmt.setString(2, sourceAccount);
                    deductStmt.executeUpdate();

                    String creditQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                    PreparedStatement creditStmt = connection.prepareStatement(creditQuery);
                    creditStmt.setDouble(1, amount);
                    creditStmt.setString(2, destinationAccount);
                    creditStmt.executeUpdate();

                    String insertTransactionQuery = "INSERT INTO transactions (account_number, type, amount, destination_account) VALUES (?, 'TRANSFER', ?, ?)";
                    PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery);
                    transactionStmt.setString(1, sourceAccount);
                    transactionStmt.setDouble(2, amount);
                    transactionStmt.setString(3, destinationAccount);
                    transactionStmt.executeUpdate();

                    connection.commit();
                    System.out.println("Transfer successful!");
                } else {
                    System.out.println("Insufficient funds!");
                }
            }

            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.out.println("Error: " + e.getMessage());
        }
    }
}