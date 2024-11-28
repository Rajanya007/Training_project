import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Employee ko sb details view krne ke rights ni dene hai aur user table mai active loan ki field lgani hai :)
// exceptions if a user enter string instead of number and some ui fixes needed urgent
// admin side ka kaam pending aa thoda
// jo deposit withdraw mai username id ka field int dia tha wo v fix krna thoda
public class Employee {
    private static Connection connection = null;

    public Employee(Connection conn) {
        connection = conn;
    }

    public static void login(Scanner scanner) {
        try {
            System.out.print("Enter Employee Id: ");
            String accountNumber = scanner.next();
            System.out.print("Enter Password: ");
            String pin = scanner.next();

            String query = "SELECT * FROM admin_table WHERE admin_name = '" + accountNumber
                    + "' AND admin_password = '" + pin + "'";
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            boolean resBool = rs.next();

            if (resBool) {
                String customerName = rs.getString("admin_name");
                System.out.println("Login Successful. Welcome, " + customerName);
                showAccountMenu(accountNumber);
            } else {
                System.out.println("Invalid Account Number or PIN");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printHeader(String title) {
        System.out.println("\033[1;36m=========================================");
        System.out.println("           " + title);
        System.out.println("=========================================\033[0m");
    }

    private static void showAccountMenu(String accountNumber) {
        Scanner scanner = new Scanner(System.in);
        boolean logout = false;

        while (!logout) {
            System.out.println("\n--- Employee Account Menu ---");
            System.out.println("1. Register User");
            System.out.println("2. Check User Data");
            System.out.println("3. Verify Loan");
            System.out.println("4. Deposit Money");
            System.out.println("5. Withdraw Money");
            System.out.println("6. Transfer Funds");
            System.out.println("7. Transaction History");
            System.out.println("8. Clear Pending Tickets");
            System.out.println("9. View Payslip");
            System.out.println("10. Logout");
            System.out.print("Enter your choice: ");

            String choice = scanner.next();

            switch (choice) {
                case "1" -> registerUser(scanner);
                case "2" -> checkUserData(accountNumber, scanner);
                case "3" -> loanApproval(scanner);
                // case "4" -> updateKyc(accountNumber, scanner);
                case "4" -> deposit(scanner);
                case "5" -> withdraw(scanner);
                case "6" -> transferFunds(scanner);
                case "7" -> viewTransactionHistory(scanner);
                case "8" -> clearPendingTickets(scanner);
                case "9" -> viewPayslip(scanner);
                case "10" -> logout = true;
                default -> System.out.println("Invalid option. Try again.");
            }

        }
    }

    private static void viewPayslip(Scanner scanner) {
        // Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter Employee ID: ");
            int employeeId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter Payslip Month (e.g., 'October 2024'): ");
            String month = scanner.nextLine();

            String query = "SELECT * FROM payslips WHERE employee_id = ? AND month = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, employeeId);
            statement.setString(2, month);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Payslip Details:");
                System.out.println("Employee ID: " + resultSet.getInt("employee_id"));
                System.out.println("Employee Name: " + resultSet.getString("employee_name"));
                System.out.println("Month: " + resultSet.getString("month"));
                System.out.println("Basic Salary: " + resultSet.getDouble("basic_salary"));
                System.out.println("Allowances: " + resultSet.getDouble("allowances"));
                System.out.println("Deductions: " + resultSet.getDouble("deductions"));
                System.out.println("Net Salary: " + resultSet.getDouble("net_salary"));
                System.out.println("Issue Date: " + resultSet.getDate("issue_date"));
            } else {
                System.out.println("No payslip found for the given Employee ID and Month.");
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println("Error retrieving payslip: " + e.getMessage());
        }
    }

    private static void registerUser(Scanner sc) {
        try {
            printHeader("Register New User");

            // Prompt for user details
            System.out.print("\033[1;33m>>\033[0m Enter Full Name: ");
            sc.nextLine(); // Consume leftover newline
            String userName = sc.nextLine();

            System.out.print("\033[1;33m>>\033[0m Enter Address: ");
            String userAddress = sc.nextLine();

            System.out.print("\033[1;33m>>\033[0m Enter Contact Number (10 digits): ");
            String userPhone = sc.nextLine();
            while (!userPhone.matches("\\d{10}")) {
                System.out.print("\033[1;31m[Error]\033[0m Invalid number. Re-enter Contact Number: ");
                userPhone = sc.nextLine();
            }

            System.out.print("\033[1;33m>>\033[0m Enter Aadhaar Number (12 digits): ");
            String aadharNum = sc.nextLine();
            while (!aadharNum.matches("\\d{12}")) {
                System.out.print("\033[1;31m[Error]\033[0m Invalid Aadhaar. Re-enter Aadhaar Number: ");
                aadharNum = sc.nextLine();
            }

            System.out.print("\033[1;33m>>\033[0m Enter PAN Number: ");
            String panNum = sc.nextLine();

            System.out.print("\033[1;33m>>\033[0m Create Password: ");
            String userPass = sc.nextLine();

            System.out.print("\033[1;33m>>\033[0m Enter Initial Deposit Amount: ");
            double userBal = sc.nextDouble();

            // Validate initial deposit
            if (userBal < 500) {
                System.out.println("\033[1;31m[Error]\033[0m Minimum deposit amount is ₹500.");
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
                userAccNum = Math.max(maxAccNum + 1, startingPoint);
            }

            // Insert the user details into the database
            String insertQuery = "INSERT INTO users (USER_ACCNUM, USER_NAME, USER_BAL, USER_RAISEDTICK, " +
                    "USER_ADDRESS, USER_PHONE, ADHAR_NUM, PAN_NUM, USER_PASS) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            pstmt.setLong(1, userAccNum);
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
                System.out.println("\033[1;32m[Success]\033[0m User registration successful!");
                System.out.println("\033[1;34m[Info]\033[0m Generated Account Number: " + userAccNum);
            } else {
                System.out.println("\033[1;31m[Error]\033[0m Failed to register the user.");
            }

        } catch (SQLException e) {
            System.out.println("\033[1;31m[Database Error]\033[0m " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\033[1;31m[Unexpected Error]\033[0m " + e.getMessage());
        }
    }

    private static void loanApproval(Scanner sc) {
        try {
            // Fetch all pending tickets
            String fetchQuery = "SELECT * FROM loan WHERE loanstatus = 'PENDING'";
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
                    String clearQuery = "UPDATE loan SET loanstatus='VERIFIED' WHERE loanid=" + loanId;
                    PreparedStatement clearStmt = connection.prepareStatement(clearQuery);
                    // clearStmt.setInt(1, loanId);

                    int rowsAffected = clearStmt.executeUpdate();
                    if (rowsAffected > 0) {

                        System.out.println("Loan ID " + loanId + " has been verified.");
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
                System.out.println("\nRequired Details");
                String userAccNum = rs.getString("user_accNum");
                String username = rs.getString("user_name");
                Double bal = rs.getDouble("user_bal");
                Integer pendingTickets = rs.getInt("user_raisedTick");
                String address = rs.getString("user_address");
                Integer phone = rs.getInt("user_phone");
                Integer adhar_num = rs.getInt("adhar_num");
                Integer pan_num = rs.getInt("pan_num");
                System.out.println("User Account Number: " + userAccNum);
                System.out.println("User Name: " + username);
                System.out.println("User Address: " + address);
                System.out.println("User Phone Number: " + phone);
                System.out.println("User Balance: " + bal);
                System.out.println("Tickets Pending for User: " + pendingTickets);
            } else {
                System.out.println("Account does not exist. Please try again!");
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

    private static void deposit(Scanner scanner) {
        try {
            System.out.print("Enter Account Number of the User: ");
            int userAcc = scanner.nextInt();
            String checkquery = "SELECT * FROM users where user_accnum=?";
            PreparedStatement checkingPreparedStatement = connection.prepareStatement(checkquery);
            checkingPreparedStatement.setInt(1, userAcc);

            int testrows = checkingPreparedStatement.executeUpdate();
            if (testrows > 0) {
                System.out.print("Enter deposit amount: ");
                double amount = scanner.nextDouble();

                String updateQuery = "UPDATE users SET user_bal = user_bal + ? WHERE user_accnum = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setDouble(1, amount);
                updateStmt.setInt(2, userAcc);
                updateStmt.executeUpdate();

                String insertTransactionQuery = "INSERT INTO transactions (sender_id, receiver_id, amount,trans_type) VALUES (?,?,?, 'DEPOSIT')";
                PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery);
                transactionStmt.setInt(1, userAcc);
                transactionStmt.setInt(2, userAcc);
                transactionStmt.setDouble(3, amount);
                transactionStmt.executeUpdate();

                System.out.println("\nDeposit successful!");
            } else {
                System.out.println("\nAccount Number does not exist");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void withdraw(Scanner scanner) {
        try {
            System.out.print("\033[1;33m>>\033[0m Enter Account Number of the User: ");
            int userAcc = scanner.nextInt();

            // Validate account existence
            String checkQuery = "SELECT user_bal FROM users WHERE user_accnum = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setInt(1, userAcc);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("user_bal"); // Retrieve current balance

                System.out.print("\033[1;33m>>\033[0m Enter withdrawal amount: ");
                double amount = scanner.nextDouble();

                // Validate sufficient funds
                if (amount > 0 && currentBalance >= amount) {
                    // Update the user's balance
                    String updateQuery = "UPDATE users SET user_bal = user_bal - ? WHERE user_accnum = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                    updateStmt.setDouble(1, amount);
                    updateStmt.setInt(2, userAcc);
                    updateStmt.executeUpdate();

                    // Insert transaction record
                    String insertTransactionQuery = "INSERT INTO transactions (sender_id, receiver_id, amount, trans_type) VALUES (?, ?, ?, 'WITHDRAWAL')";
                    PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery);
                    transactionStmt.setInt(1, userAcc); // Sender ID
                    transactionStmt.setInt(2, userAcc); // Receiver ID (self for withdrawal)
                    transactionStmt.setDouble(3, amount);
                    transactionStmt.executeUpdate();

                    System.out
                            .println("\033[1;32m[Success]\033[0m Withdrawal successful! Amount Withdrawn: $" + amount);
                } else if (amount <= 0) {
                    System.out.println("\033[1;31m[Error]\033[0m Withdrawal amount must be greater than zero.");
                } else {
                    System.out.println(
                            "\033[1;31m[Error]\033[0m Insufficient funds! Current Balance: $" + currentBalance);
                }
            } else {
                System.out.println("\033[1;31m[Error]\033[0m Account Number does not exist.");
            }
        } catch (SQLException e) {
            System.out.println("\033[1;31m[Database Error]\033[0m " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\033[1;31m[Unexpected Error]\033[0m " + e.getMessage());
        }
    }

    private static void transferFunds(Scanner scanner) {
        try {
            System.out.print("Enter source Account Number of the User: ");
            int userAcc = scanner.nextInt();
            String checkquery = "SELECT * FROM users where user_accnum=?";
            PreparedStatement checkingPreparedStatement = connection.prepareStatement(checkquery);
            checkingPreparedStatement.setInt(1, userAcc);

            int testrows = checkingPreparedStatement.executeUpdate();
            if (testrows > 0) {
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
                checkStmt.setInt(1, userAcc);
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
                        deductStmt.setInt(2, userAcc);
                        deductStmt.executeUpdate();

                        String creditQuery = "UPDATE users SET user_bal = user_bal + ? WHERE user_accnum = ?";
                        PreparedStatement creditStmt = connection.prepareStatement(creditQuery);
                        creditStmt.setDouble(1, amount);
                        creditStmt.setString(2, destinationAccount);
                        creditStmt.executeUpdate();

                        String insertTransactionQuery = "INSERT INTO transactions (sender_id, receiver_id, amount,trans_type) VALUES (?,?,?, 'TRANSFER')";
                        PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery);
                        transactionStmt.setInt(1, userAcc);
                        transactionStmt.setString(2, destinationAccount);
                        transactionStmt.setDouble(3, amount);
                        transactionStmt.executeUpdate();

                        connection.commit();
                        System.out.println("\nTransfer successful!");
                    } else {
                        System.out.println("\nInsufficient funds!");
                    }
                } else {
                    System.out.println("\nInvalid Password!");
                }

                connection.setAutoCommit(true);
            } else {
                System.out.println("\n Source Account Number does not exist");
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