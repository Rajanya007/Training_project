
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

    // public static void login(Scanner scanner) {
    // try {
    // System.out.print("Enter Employee Id: ");
    // String accountNumber = scanner.next();
    // System.out.print("Enter Password: ");
    // String pin = scanner.next();
    // String query = "SELECT * FROM admin_table WHERE admin_name = '" +
    // accountNumber
    // + "' AND admin_password = '" + pin + "'";
    // PreparedStatement pstmt = connection.prepareStatement(query);
    // ResultSet rs = pstmt.executeQuery();
    // boolean resBool = rs.next();
    // if (resBool) {
    // String customerName = rs.getString("admin_name");
    // System.out.println("Login Successful. Welcome, " + customerName);
    // showAccountMenu(accountNumber);
    // } else {
    // System.out.println("Invalid Account Number or PIN");
    // }
    // } catch (SQLException e) {
    // System.out.println("Error: " + e.getMessage());
    // }
    // }
    public static void login(Scanner scanner) {
        try {
            System.out.println("\n\033[1;34m--- Employee Login ---\033[0m");
            System.out.print("\033[1;33mEnter Employee ID: \033[0m");
            String employeeId = scanner.next();
            System.out.print("\033[1;33mEnter Password: \033[0m");
            String password = scanner.next();

            // Query to verify login details from the employee table
            String query = "SELECT * FROM employee WHERE emp_id = ? AND emp_pass= ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, employeeId);
            pstmt.setString(2, password); // Assuming `emp_status` column stores the password

            ResultSet rs = pstmt.executeQuery();

            // Check if the credentials match
            if (rs.next()) {
                String employeeName = rs.getString("emp_name");
                System.out.println("\n\033[1;32mLogin Successful.\033[0m \033[1;36mWelcome, " + employeeName + "!\033[0m\n");

                // Call a function to display the employee menu or relevant operations
                showAccountMenu(employeeId);
            } else {
                System.out.println("\n\033[1;31mInvalid Employee ID or Password. Please try again.\033[0m");
            }

            // Close resources
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("\n\033[1;31mError: " + e.getMessage() + "\033[0m");
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
            System.out.println("\n\u001B[44m========================================\u001B[0m");
            System.out.println("\u001B[1m\u001B[36m|       Employee Account Menu         |\u001B[0m");
            System.out.println("\u001B[44m========================================\u001B[0m");
            System.out.println("| \u001B[32m1. Register User                   \u001B[0m|");
            System.out.println("| \u001B[32m2. Check User Data                 \u001B[0m|");
            System.out.println("| \u001B[32m3. Verify Loan                     \u001B[0m|");
            System.out.println("| \u001B[32m4. Deposit Money                   \u001B[0m|");
            System.out.println("| \u001B[32m5. Withdraw Money                  \u001B[0m|");
            System.out.println("| \u001B[32m6. Transfer Funds                  \u001B[0m|");
            System.out.println("| \u001B[32m7. Transaction History             \u001B[0m|");
            System.out.println("| \u001B[32m8. Clear Pending Tickets           \u001B[0m|");
            System.out.println("| \u001B[33m9. View Payslip                    \u001B[0m|");
            System.out.println("| \u001B[33m10. Logout                         \u001B[0m|");
            System.out.println("========================================");

            System.out.print("\u001B[36m\nEnter your choice: \u001B[0m");
            String choice = scanner.next();

            switch (choice) {
                case "1" ->
                    registerUser(scanner);
                case "2" ->
                    checkUserData(accountNumber, scanner);
                case "3" ->
                    loanApproval(scanner);
                case "4" ->
                    deposit(scanner);
                case "5" ->
                    withdraw(scanner);
                case "6" ->
                    transferFunds(scanner);
                case "7" ->
                    viewTransactionHistory(scanner);
                case "8" ->
                    clearPendingTickets(scanner);
                case "9" ->
                    viewPayslip(accountNumber, scanner);
                case "10" -> {
                    logout = true;
                    System.out.println("\u001B[32m\nLogging out. Goodbye!\u001B[0m");
                }
                default ->
                System.out.println("\n\033[1;31mInvalid option. Try again.\033[0m");
            }
        }
    }

    private static void viewPayslip(String employeeId, Scanner scanner) {
        // Scanner scanner = new Scanner(System.in);
        final String RESET = "\u001B[0m";
        final String CYAN = "\u001B[36m";
        final String GREEN = "\u001B[32m";
        final String YELLOW = "\u001B[33m";
        final String RED = "\u001B[31m";
        final String BOLD = "\u001B[1m";
        try {
            // System.out.print("Enter Employee ID: ");
            // int employeeId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print(CYAN + "Enter Payslip Month (e.g., 'October 2024'): " + RESET);
            String month = scanner.nextLine();

            String query = "SELECT * FROM payslips WHERE employee_id = ? AND month = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, employeeId);
            statement.setString(2, month);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println(GREEN + "\n--- Payslip Details ---" + RESET);
                System.out.println(BOLD + "Employee ID: " + RESET + resultSet.getInt("employee_id"));
                System.out.println(BOLD + "Employee Name: " + RESET + resultSet.getString("employee_name"));
                System.out.println(BOLD + "Month: " + RESET + resultSet.getString("month"));
                System.out.println(BOLD + "Basic Salary: " + RESET + resultSet.getDouble("basic_salary"));
                System.out.println(BOLD + "Allowances: " + RESET + resultSet.getDouble("allowances"));
                System.out.println(BOLD + "Deductions: " + RESET + resultSet.getDouble("deductions"));
                System.out.println(BOLD + "Net Salary: " + RESET + resultSet.getDouble("net_salary"));
                System.out.println(BOLD + "Issue Date: " + RESET + resultSet.getDate("issue_date"));
            } else {
                System.out.println(RED + "No payslip found for the given Employee ID and Month." + RESET);
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println(RED + "Error retrieving payslip: " + e.getMessage() + RESET);
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
            String insertQuery = "INSERT INTO users (USER_ACCNUM, USER_NAME, USER_BAL, USER_RAISEDTICK, "
                    + "USER_ADDRESS, USER_PHONE, ADHAR_NUM, PAN_NUM, USER_PASS) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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

            // Header in green
            System.out.println("\u001B[32m\n--- Pending Loan Requests ---\u001B[0m");
            System.out.println("\u001B[32m============================\u001B[0m");  // Separator line

            List<Integer> loanList = new ArrayList<>();
            boolean hasPendingLoans = false;

            while (rs.next()) {
                hasPendingLoans = true;
                int loanId = rs.getInt("loanId");
                String loanDescription = rs.getString("loanDescription");
                loanList.add(loanId);

                // Display Loan ID and Description with bold formatting
                System.out.println("\u001B[1mLoan ID: \u001B[0m" + loanId + " \u001B[1m| Description: \u001B[0m" + loanDescription);
            }

            if (!hasPendingLoans) {
                System.out.println("\u001B[31mNo pending loan found.\u001B[0m");  // Red message for no pending loans
            } else {
                // Prompt to enter loan IDs with cyan color
                System.out.print("\u001B[36m\nEnter the loan ID(s) to clear (comma-separated, or type 'all' to clear all): \u001B[0m");
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
                                System.out.println("\u001B[31mInvalid Loan ID: " + loanId + "\u001B[0m");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("\u001B[31mInvalid input: " + id + " is not a valid number.\u001B[0m");
                        }
                    }
                }

                // Clear the selected loan(s)
                for (int loanId : loansToClear) {
                    String clearQuery = "UPDATE loan SET loanstatus='VERIFIED' WHERE loanid=" + loanId;
                    PreparedStatement clearStmt = connection.prepareStatement(clearQuery);

                    int rowsAffected = clearStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        // Green message for success
                        System.out.println("\u001B[32mLoan ID " + loanId + " has been verified successfully.\u001B[0m");
                    } else {
                        // Red message for failure
                        System.out.println("\u001B[31mFailed to verify Loan ID " + loanId + ". Please try again.\u001B[0m");
                    }
                    clearStmt.close();
                }
            }

            rs.close();
            fetchStmt.close();
        } catch (SQLException e) {
            System.out.println("\u001B[31mError: " + e.getMessage() + "\u001B[0m");
        }
    }

    private static void checkUserData(String accountNumber, Scanner sc) {
        try {
            // Asking for the user's account number in a formatted and colored way
            System.out.print("\u001B[36mEnter Account Number of the User: \u001B[0m");
            int userAcc = sc.nextInt();

            // Prepare the query to fetch user data
            String query = "SELECT * FROM users WHERE user_accnum = " + userAcc;
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            boolean userFound = rs.next();

            if (userFound) {
                // Header in green to display required user details
                System.out.println("\n\u001B[32m--- User Details ---\u001B[0m");
                System.out.println("\u001B[32m====================\u001B[0m");  // Separator line

                // Extract user data from the result set
                String userAccNum = rs.getString("user_accNum");
                String username = rs.getString("user_name");
                Double bal = rs.getDouble("user_bal");
                Integer pendingTickets = rs.getInt("user_raisedTick");
                String address = rs.getString("user_address");
                Integer phone = rs.getInt("user_phone");
                Integer adhar_num = rs.getInt("adhar_num");
                Integer pan_num = rs.getInt("pan_num");

                // Display the user's details with bold labels
                System.out.println("\u001B[1mUser Account Number:\u001B[0m " + userAccNum);
                System.out.println("\u001B[1mUser Name:\u001B[0m " + username);
                System.out.println("\u001B[1mUser Address:\u001B[0m " + address);
                System.out.println("\u001B[1mUser Phone Number:\u001B[0m " + phone);
                System.out.println("\u001B[1mUser Balance:\u001B[0m " + bal);
                System.out.println("\u001B[1mTickets Pending for User:\u001B[0m " + pendingTickets);
            } else {
                // If no account is found, display an error message in red
                System.out.println("\u001B[31mAccount does not exist. Please try again!\u001B[0m");
            }
        } catch (SQLException e) {
            // Catch any SQL exceptions and print them in red
            System.out.println("\u001B[31mError: " + e.getMessage() + "\u001B[0m");
        }
    }

    private static void viewTransactionHistory(Scanner sc) {
        try {
            // Prompt the admin to enter the user ID for transaction history
            System.out.print("\u001B[36mEnter the User ID to view transaction history: \u001B[0m");
            int userId = sc.nextInt();

            // SQL query to fetch transactions involving the specified user as sender or receiver
            String query = "SELECT transaction_id, sender_id, receiver_id, amount, transaction_date "
                    + "FROM transactions "
                    + "WHERE sender_id = ? OR receiver_id = ? "
                    + "ORDER BY transaction_date DESC";

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);

            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n\u001B[32m--- Transaction History ---\u001B[0m"); // Green header
            System.out.println("\u001B[32m==============================\u001B[0m");  // Separator line

            boolean hasTransactions = false;

            // Iterate through the results and display each transaction
            while (rs.next()) {
                hasTransactions = true;
                int transactionId = rs.getInt("transaction_id");
                int senderId = rs.getInt("sender_id");
                int receiverId = rs.getInt("receiver_id");
                double amount = rs.getDouble("amount");
                Timestamp transactionDate = rs.getTimestamp("transaction_date");

                // Display transaction details with formatting
                System.out.printf("\u001B[1mTransaction ID:\u001B[0m %d | \u001B[1mSender ID:\u001B[0m %d | "
                        + "\u001B[1mReceiver ID:\u001B[0m %d | \u001B[1mAmount:\u001B[0m %.2f | \u001B[1mDate:\u001B[0m %s%n",
                        transactionId, senderId, receiverId, amount, transactionDate);
            }

            if (!hasTransactions) {
                // If no transactions are found, display a message in red
                System.out.println("\u001B[31mNo transactions found for User ID: " + userId + "\u001B[0m");
            }

            // Close resources
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            // Display SQL errors in red
            System.out.println("\u001B[31mError fetching transaction history: " + e.getMessage() + "\u001B[0m");
        }
    }

    private static void clearPendingTickets(Scanner sc) {
        try {
            String fetchQuery = "SELECT ticket_id, ticket_description FROM tickets WHERE ticket_status = 'Pending'";
            PreparedStatement fetchStmt = connection.prepareStatement(fetchQuery);
            ResultSet rs = fetchStmt.executeQuery();

            System.out.println("\n\u001B[32m--- Pending Tickets ---\u001B[0m");
            List<Integer> ticketIds = new ArrayList<>();
            while (rs.next()) {
                int ticketId = rs.getInt("ticket_id");
                String ticketDescription = rs.getString("ticket_description");
                ticketIds.add(ticketId);
                System.out.println("\u001B[1mTicket ID:\u001B[0m " + ticketId + " | \u001B[1mDescription:\u001B[0m " + ticketDescription);
            }

            if (ticketIds.isEmpty()) {
                System.out.println("\u001B[31mNo pending tickets found.\u001B[0m");
            } else {
                System.out.print("\u001B[36m\nEnter the Ticket ID(s) to clear (comma-separated, or type 'all' to clear all): \u001B[0m");
                sc.nextLine();
                String input = sc.nextLine();

                List<Integer> ticketsToClear = new ArrayList<>();
                if (input.equalsIgnoreCase("all")) {
                    ticketsToClear.addAll(ticketIds);
                } else {
                    String[] inputIds = input.split(",");
                    for (String id : inputIds) {
                        try {
                            int ticketId = Integer.parseInt(id.trim());
                            if (ticketIds.contains(ticketId)) {
                                ticketsToClear.add(ticketId);
                            } else {
                                System.out.println("\u001B[31mInvalid Ticket ID: " + ticketId + "\u001B[0m");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("\u001B[31mInvalid input: " + id + " is not a valid number.\u001B[0m");
                        }
                    }
                }

                for (int ticketId : ticketsToClear) {
                    String clearQuery = "UPDATE tickets SET ticket_status = 'Resolved' WHERE ticket_id = ?";
                    PreparedStatement clearStmt = connection.prepareStatement(clearQuery);
                    clearStmt.setInt(1, ticketId);

                    int rowsAffected = clearStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("\u001B[32mTicket ID " + ticketId + " has been cleared.\u001B[0m");
                    } else {
                        System.out.println("\u001B[31mFailed to clear Ticket ID " + ticketId + ". Please check the ticket ID or try again.\u001B[0m");
                    }
                    clearStmt.close();
                }
            }

            rs.close();
            fetchStmt.close();
        } catch (SQLException e) {
            System.out.println("\u001B[31mError: " + e.getMessage() + ". Please contact support.\u001B[0m");
        }
    }

    private static void deposit(Scanner scanner) {
        try {
            System.out.print("\u001B[36mEnter Account Number of the User: \u001B[0m");
            int userAcc = scanner.nextInt();

            String checkquery = "SELECT * FROM users where user_accnum=?";
            PreparedStatement checkingPreparedStatement = connection.prepareStatement(checkquery);
            checkingPreparedStatement.setInt(1, userAcc);

            int testrows = checkingPreparedStatement.executeUpdate();
            if (testrows > 0) {
                System.out.print("\u001B[36mEnter deposit amount: \u001B[0m");
                double amount = scanner.nextDouble();

                String updateQuery = "UPDATE users SET user_bal = user_bal + ? WHERE user_accnum = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setDouble(1, amount);
                updateStmt.setInt(2, userAcc);
                updateStmt.executeUpdate();

                String insertTransactionQuery = "INSERT INTO transactions (sender_id, receiver_id, amount, trans_type) VALUES (?,?,?, 'DEPOSIT')";
                PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery);
                transactionStmt.setInt(1, userAcc);
                transactionStmt.setInt(2, userAcc);
                transactionStmt.setDouble(3, amount);
                transactionStmt.executeUpdate();

                System.out.println("\u001B[32m\nDeposit successful!\u001B[0m");
            } else {
                System.out.println("\u001B[31m\nAccount Number does not exist.\u001B[0m");
            }

        } catch (SQLException e) {
            System.out.println("\u001B[31mError: " + e.getMessage() + " \u001B[0mPlease try again.");
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
            System.out.print("\u001B[36mEnter source Account Number of the User: \u001B[0m");
            int userAcc = scanner.nextInt();
            String checkquery = "SELECT * FROM users where user_accnum=?";
            PreparedStatement checkingPreparedStatement = connection.prepareStatement(checkquery);
            checkingPreparedStatement.setInt(1, userAcc);

            int testrows = checkingPreparedStatement.executeUpdate();
            if (testrows > 0) {
                System.out.print("\u001B[36mEnter destination account number: \u001B[0m");
                String destinationAccount = scanner.next();

                String checkDestinationQuery = "SELECT * FROM users WHERE user_accnum = ?";
                PreparedStatement checkDestStmt = connection.prepareStatement(checkDestinationQuery);
                checkDestStmt.setString(1, destinationAccount);
                ResultSet destRs = checkDestStmt.executeQuery();
                Boolean checke = destRs.next();
                if (!checke) {
                    System.out.println("\u001B[31m\nDestination account does not exist. Transfer aborted.\u001B[0m");
                    return;
                }
                System.out.print("\u001B[36mEnter transfer amount: \u001B[0m");
                double amount = scanner.nextDouble();
                System.out.print("\u001B[36mEnter your password: \u001B[0m");
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

                        String insertTransactionQuery = "INSERT INTO transactions (sender_id, receiver_id, amount, trans_type) VALUES (?,?,?, 'TRANSFER')";
                        PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery);
                        transactionStmt.setInt(1, userAcc);
                        transactionStmt.setString(2, destinationAccount);
                        transactionStmt.setDouble(3, amount);
                        transactionStmt.executeUpdate();

                        connection.commit();
                        System.out.println("\u001B[32m\nTransfer successful!\u001B[0m");
                    } else {
                        System.out.println("\u001B[31m\nInsufficient funds!\u001B[0m");
                    }
                } else {
                    System.out.println("\u001B[31m\nInvalid Password!\u001B[0m");
                }

                connection.setAutoCommit(true);
            } else {
                System.out.println("\u001B[31m\nSource Account Number does not exist.\u001B[0m");
            }

            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("\u001B[31mError rolling back transaction: " + ex.getMessage() + "\u001B[0m");
            }
            System.out.println("\u001B[31mError: " + e.getMessage() + "\u001B[0mPlease try again.");
        }
    }

}
