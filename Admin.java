
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Admin {

    private static Connection connection = null;

    public Admin(Connection conn) {
        connection = conn;
    }

    public static void adminLogin(Scanner scanner) {
        try {
            System.out.print("\n\033[1;33m>>\033[0m Enter Admin ID: ");
            String accountNumber = scanner.next();

            System.out.print("\033[1;33m>>\033[0m Enter Password: ");
            String pin = scanner.next();

            String query = "SELECT * FROM admin_table WHERE admin_name = ? AND admin_password = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, pin);

            ResultSet rs = pstmt.executeQuery();
            boolean resBool = rs.next();

            if (resBool) {
                String customerName = rs.getString("admin_name");
                System.out.println("\n\033[1;32m[Success]\033[0m Login Successful. Welcome, " + customerName + "!");
                showAdminAccountMenu(accountNumber);
            } else {
                System.out.println("\n\033[1;31m[Error]\033[0m Invalid Admin ID or Password.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void registerEmployee(Scanner sc) {
        try {
            printHeader("Register New Employee");

            // Prompt for employee details
            System.out.print("\033[1;33m>>\033[0m Enter Full Name: ");
            sc.nextLine(); // Consume leftover newline
            String empName = sc.nextLine();

            System.out.print("\033[1;33m>>\033[0m Enter Email Address: ");
            String empEmail = sc.nextLine();

            System.out.print("\033[1;33m>>\033[0m Enter Contact Number (10 digits): ");
            String empPhone = sc.nextLine();
            while (!empPhone.matches("\\d{10}")) {
                System.out.print("\033[1;31m[Error]\033[0m Invalid number. Re-enter Contact Number: ");
                empPhone = sc.nextLine();
            }

            System.out.print("\033[1;33m>>\033[0m Enter Address: ");
            String empAddress = sc.nextLine();

            System.out.print("\033[1;33m>>\033[0m Enter Salary: ");
            double empSalary = sc.nextDouble();
            sc.nextLine(); // Consume leftover newline

            System.out.print("\033[1;33m>>\033[0m Enter Position: ");
            String empPosition = sc.nextLine();

            System.out.print("\033[1;33m>>\033[0m Enter Department: ");
            String empDepartment = sc.nextLine();

            System.out.print("\033[1;33m>>\033[0m Enter Hire Date (YYYY-MM-DD): ");
            String empHireDate = sc.nextLine();
            while (!empHireDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                System.out.print("\033[1;31m[Error]\033[0m Invalid date format. Re-enter Hire Date: ");
                empHireDate = sc.nextLine();
            }

            System.out.print("\033[1;33m>>\033[0m Enter Aadhaar Number (12 digits): ");
            String aadharNum = sc.nextLine();
            while (!aadharNum.matches("\\d{12}")) {
                System.out.print("\033[1;31m[Error]\033[0m Invalid Aadhaar. Re-enter Aadhaar Number: ");
                aadharNum = sc.nextLine();
            }

            System.out.print("\033[1;33m>>\033[0m Enter PAN Number: ");
            String panNum = sc.nextLine();

            System.out.print("\033[1;33m>>\033[0m Enter Status (Active/Inactive): ");
            String empStatus = sc.nextLine();

            // Fetch the current maximum employee ID from the database
            String getMaxEmpIdQuery = "SELECT MAX(EMP_ID) AS MAX_EMP_ID FROM employee";
            PreparedStatement maxEmpIdStmt = connection.prepareStatement(getMaxEmpIdQuery);
            ResultSet rs = maxEmpIdStmt.executeQuery();

            int empId = 1; // Starting employee ID
            if (rs.next()) {
                int maxEmpId = rs.getInt("MAX_EMP_ID");
                empId = maxEmpId + 1;
            }

            // Insert the employee details into the database
            String insertQuery = "INSERT INTO employee (EMP_ID, EMP_NAME, EMP_EMAIL, EMP_PHONE, EMP_ADDRESS, EMP_SALARY, EMP_POSITION, EMP_DEPARTMENT, EMP_HIRE_DATE, ACTIVE_LOANS, EMP_STATUS, ADHAR_NUM, PAN_NUM) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            pstmt.setInt(1, empId);
            pstmt.setString(2, empName);
            pstmt.setString(3, empEmail);
            pstmt.setString(4, empPhone);
            pstmt.setString(5, empAddress);
            pstmt.setDouble(6, empSalary);
            pstmt.setString(7, empPosition);
            pstmt.setString(8, empDepartment);
            pstmt.setDate(9, java.sql.Date.valueOf(empHireDate));
            pstmt.setInt(10, 0); // Initially no active loans
            pstmt.setString(11, empStatus);
            pstmt.setString(12, aadharNum);
            pstmt.setString(13, panNum);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("\033[1;32m[Success]\033[0m Employee registration successful!");
                System.out.println("\033[1;34m[Info]\033[0m Generated Employee ID: " + empId);
            } else {
                System.out.println("\033[1;31m[Error]\033[0m Failed to register the employee.");
            }

            rs.close();
            maxEmpIdStmt.close();
            pstmt.close();

        } catch (SQLException e) {
            System.out.println("\033[1;31m[Database Error]\033[0m " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\033[1;31m[Unexpected Error]\033[0m " + e.getMessage());
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

    private static void printHeader(String title) {
        System.out.println("\033[1;36m=========================================");
        System.out.println("           " + title);
        System.out.println("=========================================\033[0m");
    }

    private static void showAdminAccountMenu(String accountNumber) {
        Scanner scanner = new Scanner(System.in);
        boolean logout = false;

        while (!logout) {
            System.out.println("\n");
            printHeader(" Admin Account Menu ");

            System.out.println("\033[1;34m1.\033[0m Register User");
            System.out.println("\033[1;34m2.\033[0m Check Complete User Data");
            System.out.println("\033[1;34m3.\033[0m Approve Loan");
            System.out.println("\033[1;34m4.\033[0m Deposit Money");
            System.out.println("\033[1;34m5.\033[0m Withdraw Money");
            System.out.println("\033[1;34m6.\033[0m Transfer Funds");
            System.out.println("\033[1;34m7.\033[0m Transaction History");
            System.out.println("\033[1;34m8.\033[0m Clear Pending Tickets");
            // System.out.println("\033[1;34m9.\033[0m View Payslip");
            System.out.println("\033[1;34m9.\033[0m Register Employee");
            System.out.println("\033[1;34m10.\033[0m Logout");

            System.out.print("\n\033[1;33m>>\033[0m Enter your choice: ");

            String choice = scanner.next();

            switch (choice) {
                case "1" ->
                    registerUser(scanner);
                case "2" ->
                    checkUserData(accountNumber, scanner);
                case "3" ->
                    approveLoan(scanner);
                // case "4" -> updateKyc(accountNumber, scanner);
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
                    registerEmployee(scanner);
                case "10" ->
                    logout = true;
                default ->
                    System.out.println("Invalid option. Try again.");
            }

        }
    }

    private static void viewTransactionHistory(Scanner sc) {
        try {
            // Prompt the admin to enter the user ID for transaction history
            System.out.print("Enter the User ID to view transaction history: ");
            int userId = sc.nextInt();

            // SQL query to fetch transactions involving the specified user as sender or
            // receiver
            String query = "SELECT * "
                    + "FROM transactions "
                    + "WHERE sender_id = ? OR receiver_id = ? "
                    + "ORDER BY transaction_date DESC";

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
                String type = rs.getString("trans_type");
                Timestamp transactionDate = rs.getTimestamp("transaction_date");

                // Display transaction details
                System.out.printf(
                        "Transaction ID: %d | Sender ID: %d | Receiver ID: %d | Amount: %.2f | Amount: " + type
                        + " | Date: %s%n",
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

    private static void deposit(Scanner scanner) {
        try {
            System.out.print("\n\033[1;33m>>\033[0m Enter Account Number of the User: ");
            int userAcc = scanner.nextInt();

            String checkQuery = "SELECT * FROM users WHERE user_accnum = ?";
            PreparedStatement checkingPreparedStatement = connection.prepareStatement(checkQuery);
            checkingPreparedStatement.setInt(1, userAcc);

            int testRows = checkingPreparedStatement.executeUpdate();
            if (testRows > 0) {
                System.out.print("\033[1;34m>>\033[0m Enter deposit amount: ");
                double amount = scanner.nextDouble();

                // Update the user's balance
                String updateQuery = "UPDATE users SET user_bal = user_bal + ? WHERE user_accnum = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setDouble(1, amount);
                updateStmt.setInt(2, userAcc);
                updateStmt.executeUpdate();

                // Record the transaction
                String insertTransactionQuery = "INSERT INTO transactions (sender_id, receiver_id, amount, trans_type) VALUES (?, ?, ?, 'DEPOSIT')";
                PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery);
                transactionStmt.setInt(1, userAcc);
                transactionStmt.setInt(2, userAcc);
                transactionStmt.setDouble(3, amount);
                transactionStmt.executeUpdate();

                System.out.println("\n\033[1;32m[Success]\033[0m Deposit successful!");
            } else {
                System.out.println("\n\033[1;31m[Error]\033[0m Account Number does not exist.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // private static void withdraw(Scanner scanner) {
    // try {
    // System.out.print("Enter Account Number of the User: ");
    // int userAcc = scanner.nextInt();
    // String checkquery = "SELECT * FROM users where user_accnum=?";
    // PreparedStatement checkingPreparedStatement =
    // connection.prepareStatement(checkquery);
    // checkingPreparedStatement.setInt(1, userAcc);
    // int testrows = checkingPreparedStatement.executeUpdate();
    // if (testrows > 0) {
    // System.out.print("Enter withdrawal amount: ");
    // double amount = scanner.nextDouble();
    // String checkBalanceQuery = "SELECT balance FROM accounts WHERE account_number
    // = ?";
    // PreparedStatement checkStmt = connection.prepareStatement(checkBalanceQuery);
    // checkStmt.setInt(1, userAcc);
    // ResultSet rs = checkStmt.executeQuery();
    // if (rs.next()) {
    // double currentBalance = rs.getDouble("balance");
    // if (currentBalance >= amount) {
    // String updateQuery = "UPDATE accounts SET balance = balance - ? WHERE
    // account_number = ?";
    // PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
    // updateStmt.setDouble(1, amount);
    // updateStmt.setInt(2, userAcc);
    // updateStmt.executeUpdate();
    // String insertTransactionQuery = "INSERT INTO transactions (account_number,
    // type, amount) VALUES (?, 'WITHDRAWAL', ?)";
    // PreparedStatement transactionStmt =
    // connection.prepareStatement(insertTransactionQuery);
    // transactionStmt.setInt(1, userAcc);
    // transactionStmt.setDouble(2, amount);
    // transactionStmt.executeUpdate();
    // System.out.println("Withdrawal successful!");
    // } else {
    // System.out.println("Insufficient funds!");
    // }
    // }
    // System.out.println("\nDeposit successful!");
    // } else {
    // System.out.println("\nAccount Number does not exist");
    // }
    // } catch (SQLException e) {
    // System.out.println("Error: " + e.getMessage());
    // }
    // }
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
                            .println("\033[1;32m[Success]\033[0m Withdrawal successful! Amount Withdrawn: ₹" + amount);
                } else if (amount <= 0) {
                    System.out.println("\033[1;31m[Error]\033[0m Withdrawal amount must be greater than zero.");
                } else {
                    System.out.println(
                            "\033[1;31m[Error]\033[0m Insufficient funds! Current Balance: ₹" + currentBalance);
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

    // private static void approveLoan(Scanner sc) {
    // try {
    // // Fetch all pending tickets
    // String fetchQuery = "SELECT * FROM loan WHERE loanstatus = 'VERIFIED'";
    // PreparedStatement fetchStmt = connection.prepareStatement(fetchQuery);
    // ResultSet rs = fetchStmt.executeQuery();
    // System.out.println("\n--- Pending Loan ---");
    // List<Integer> loanList = new ArrayList<>();
    // while (rs.next()) {
    // int loanId = rs.getInt("loanId");
    // String loanDescription = rs.getString("loanDescription");
    // loanList.add(loanId);
    // System.out.println("Loan ID: " + loanId + " | Description: " +
    // loanDescription); // to be continues
    // }
    // if (loanList.isEmpty()) {
    // System.out.println("No pending loan found.");
    // } else {
    // // Ask user to decide which loan(s) to clear
    // System.out.print("\nEnter the loan ID(s) to clear (comma-separated, or type
    // 'all' to clear all): ");
    // sc.nextLine(); // Clear scanner buffer
    // String input = sc.nextLine();
    // List<Integer> loansToClear = new ArrayList<>();
    // if (input.equalsIgnoreCase("all")) {
    // loansToClear.addAll(loanList);
    // } else {
    // // Parse user input for specific loan IDs
    // String[] inputIds = input.split(",");
    // for (String id : inputIds) {
    // try {
    // int loanId = Integer.parseInt(id.trim());
    // if (loanList.contains(loanId)) {
    // loansToClear.add(loanId);
    // } else {
    // System.out.println("Invalid Loan ID: " + loanId);
    // }
    // } catch (NumberFormatException e) {
    // System.out.println("Invalid input: " + id + " is not a valid number.");
    // }
    // }
    // }
    // // Clear selected loan
    // for (int loanId : loansToClear) {
    // String clearQuery = "UPDATE loan SET loanstatus='APPROVED' WHERE loanid=" +
    // loanId;
    // PreparedStatement clearStmt = connection.prepareStatement(clearQuery);
    // // clearStmt.setInt(1, loanId);
    // int rowsAffected = clearStmt.executeUpdate();
    // if (rowsAffected > 0) {
    // System.out.println("Loan ID " + loanId + " has been approved.");
    // } else {
    // System.out.println("Failed to verify Loan ID " + loanId + ".");
    // }
    // clearStmt.close();
    // }
    // }
    // rs.close();
    // fetchStmt.close();
    // } catch (SQLException e) {
    // System.out.println("Error: " + e.getMessage());
    // }
    // }
    private static void approveLoan(Scanner sc) {
        try {
            // Fetch all pending loans with 'VERIFIED' status
            String fetchQuery = "SELECT loanId, loanDescription, loanAmount, loanDuration, loanEmi, loanStatus, userId FROM loan WHERE loanstatus = 'VERIFIED'";
            PreparedStatement fetchStmt = connection.prepareStatement(fetchQuery);
            ResultSet rs = fetchStmt.executeQuery();

            System.out.println("\n\033[1;36m--- Pending Loans ---\033[0m");
            List<Integer> loanList = new ArrayList<>();
            Map<Integer, Integer> loanUserMap = new HashMap<>(); // Map loanId to userId
            Map<Integer, Double> loanAmountMap = new HashMap<>(); // Map loanId to loanAmount
            while (rs.next()) {
                int loanId = rs.getInt("loanId");
                String loanDescription = rs.getString("loanDescription");
                int userId = rs.getInt("userId");
                double loanAmount = rs.getDouble("loanAmount");

                loanList.add(loanId);
                loanUserMap.put(loanId, userId);
                loanAmountMap.put(loanId, loanAmount);

                System.out.println("\033[1;32mLoan ID:\033[0m " + rs.getInt("loanId"));
                System.out.println("\033[1;32mDescription:\033[0m " + rs.getString("loanDescription"));
                System.out.println("\033[1;32mAmount:\033[0m " + rs.getDouble("loanAmount"));
                System.out.println("\033[1;32mDuration (months):\033[0m " + rs.getInt("loanDuration"));
                System.out.println("\033[1;32mEMI:\033[0m " + rs.getDouble("loanEmi"));
                System.out.println("\033[1;32mStatus:\033[0m " + rs.getString("loanStatus"));
                System.out.println("\033[1;32mUser ID:\033[0m " + rs.getInt("userId"));

                System.out.println("\033[1;34m----------------------------------------\033[0m");
            }

            if (loanList.isEmpty()) {
                System.out.println("\033[1;33mNo pending loans found.\033[0m");
            } else {
                System.out.print(
                        "\n\033[1;33m>>\033[0m Enter the loan ID(s) to approve (comma-separated, or type 'all' to approve all): ");
                sc.nextLine(); // Clear scanner buffer
                String input = sc.nextLine();

                List<Integer> loansToApprove = new ArrayList<>();
                if (input.equalsIgnoreCase("all")) {
                    loansToApprove.addAll(loanList);
                } else {
                    // Parse user input for specific loan IDs
                    String[] inputIds = input.split(",");
                    for (String id : inputIds) {
                        try {
                            int loanId = Integer.parseInt(id.trim());
                            if (loanList.contains(loanId)) {
                                loansToApprove.add(loanId);
                            } else {
                                System.out.println("\033[1;31mInvalid Loan ID:\033[0m " + loanId);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("\033[1;31mInvalid input:\033[0m " + id + " is not a valid number.");
                        }
                    }
                }

                // Approve selected loans and update user balances
                for (int loanId : loansToApprove) {
                    int userId = loanUserMap.get(loanId);
                    double loanAmount = loanAmountMap.get(loanId);

                    // Update loan status to 'APPROVED'
                    String approveQuery = "UPDATE loan SET loanstatus = 'APPROVED' WHERE loanId = ?";
                    PreparedStatement approveStmt = connection.prepareStatement(approveQuery);
                    approveStmt.setInt(1, loanId);

                    int rowsAffected = approveStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("\033[1;32m[Success]\033[0m Loan ID " + loanId + " has been approved.");

                        // Increment active loans counter for the user
                        String updateActiveLoansQuery = "UPDATE users SET active_loans = active_loans + 1 WHERE user_accnum = ?";
                        PreparedStatement updateActiveLoansStmt = connection.prepareStatement(updateActiveLoansQuery);
                        updateActiveLoansStmt.setInt(1, userId);
                        int activeLoanRows = updateActiveLoansStmt.executeUpdate();
                        if (activeLoanRows > 0) {
                            System.out.println(
                                    "\033[1;32m[Success]\033[0m Active loans count updated for User ID " + userId);
                        } else {
                            System.out
                                    .println("\033[1;31m[Error]\033[0m Failed to update active loans count for User ID "
                                            + userId);
                        }
                        updateActiveLoansStmt.close();

                        // Update user balance with the loan amount
                        String updateUserBalanceQuery = "UPDATE users SET user_bal = user_bal + ? WHERE user_accnum = ?";
                        PreparedStatement updateUserBalanceStmt = connection.prepareStatement(updateUserBalanceQuery);
                        updateUserBalanceStmt.setDouble(1, loanAmount);
                        updateUserBalanceStmt.setInt(2, userId);
                        int balanceUpdateRows = updateUserBalanceStmt.executeUpdate();
                        if (balanceUpdateRows > 0) {
                            System.out.println(
                                    "\033[1;32m[Success]\033[0m User balance updated with loan amount for User ID "
                                    + userId);
                        } else {
                            System.out.println(
                                    "\033[1;31m[Error]\033[0m Failed to update user balance for User ID " + userId);
                        }
                        updateUserBalanceStmt.close();
                    } else {
                        System.out.println("\033[1;31m[Error]\033[0m Failed to approve Loan ID " + loanId + ".");
                    }
                    approveStmt.close();
                }
            }

            rs.close();
            fetchStmt.close();
        } catch (SQLException e) {
            System.out.println("\033[1;31m[Database Error]\033[0m " + e.getMessage());
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
                System.out.println("\n\033[1;36m--- User Details ---\033[0m");
                String userAccNum = rs.getString("user_accNum");
                String username = rs.getString("user_name");
                Double bal = rs.getDouble("user_bal");
                Integer pendingTickets = rs.getInt("user_raisedTick");
                String address = rs.getString("user_address");
                Integer phone = rs.getInt("user_phone");
                Integer adhar_num = rs.getInt("adhar_num");
                Integer pan_num = rs.getInt("pan_num");
                Integer active_loan = rs.getInt("active_loans");
                System.out.println("\033[1;34mUser Account Number:\033[0m " + userAccNum);
                System.out.println("\033[1;34mUser Name:\033[0m " + username);
                System.out.println("\033[1;34mAccount Balance:\033[0m $" + bal);
                System.out.println("\033[1;34mUser Address:\033[0m " + address);
                System.out.println("\033[1;34mUser Phone Number:\033[0m " + phone);
                System.out.println("\033[1;34mPending Tickets:\033[0m " + pendingTickets);
                System.out.println("\033[1;34mAadhar Number:\033[0m " + adhar_num);
                System.out.println("\033[1;34mPan Number:\033[0m " + pan_num);
                System.out.println("\033[1;34mActive Loans:\033[0m " + active_loan);
                if (active_loan > 0) {
                    try {
                        // Query to fetch active loans for the user
                        String fetchActiveLoansQuery = "SELECT loanId, loanAmount, loanDescription FROM loan WHERE loanstatus = 'APPROVED' AND userId = ?";
                        PreparedStatement fetchActiveLoansStmt = connection.prepareStatement(fetchActiveLoansQuery);
                        fetchActiveLoansStmt.setString(1, userAccNum); // Replace with the current user's ID

                        ResultSet activeLoansRs = fetchActiveLoansStmt.executeQuery();

                        System.out.println("\033[1;36m--- Active Loans Details ---\033[0m");
                        while (activeLoansRs.next()) {
                            int loanId = activeLoansRs.getInt("loanId");
                            double loanAmount = activeLoansRs.getDouble("loanAmount");
                            String loanDescription = activeLoansRs.getString("loanDescription");

                            // Display loan details
                            System.out.println("\033[1;32mLoan ID:\033[0m " + loanId);
                            System.out.println("\033[1;32mDescription:\033[0m " + loanDescription);
                            System.out.println("\033[1;32mAmount:\033[0m " + loanAmount);
                            System.out.println("\033[1;34m----------------------------------------\033[0m");
                        }

                        activeLoansRs.close();
                        fetchActiveLoansStmt.close();
                    } catch (SQLException e) {
                        System.out.println("\033[1;31m[Database Error]\033[0m " + e.getMessage());
                    }
                }
            } else {
                System.out.println("Account does not exist. Please try again!");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void checkEmployeeData(Scanner sc) {
        try {
            System.out.print("\033[1;33m>>\033[0m Enter Employee ID: ");
            int empId = sc.nextInt();
            String query = "SELECT * FROM employee WHERE emp_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, empId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n\033[1;36m--- Employee Details ---\033[0m");
                int employeeId = rs.getInt("emp_id");
                String empName = rs.getString("emp_name");
                String empEmail = rs.getString("emp_email");
                String empPhone = rs.getString("emp_phone");
                String empAddress = rs.getString("emp_address");
                double empSalary = rs.getDouble("emp_salary");
                String empPosition = rs.getString("emp_position");
                String empDepartment = rs.getString("emp_department");
                Date empHireDate = rs.getDate("emp_hire_date");
                int activeLoans = rs.getInt("active_loans");
                String empStatus = rs.getString("emp_status");
                long aadharNum = rs.getLong("adhar_num");
                long panNum = rs.getLong("pan_num");

                // Display Employee Details
                System.out.println("\033[1;34mEmployee ID:\033[0m " + employeeId);
                System.out.println("\033[1;34mName:\033[0m " + empName);
                System.out.println("\033[1;34mEmail:\033[0m " + empEmail);
                System.out.println("\033[1;34mPhone:\033[0m " + empPhone);
                System.out.println("\033[1;34mAddress:\033[0m " + empAddress);
                System.out.println("\033[1;34mSalary:\033[0m ₹" + empSalary);
                System.out.println("\033[1;34mPosition:\033[0m " + empPosition);
                System.out.println("\033[1;34mDepartment:\033[0m " + empDepartment);
                System.out.println("\033[1;34mHire Date:\033[0m " + empHireDate);
                System.out.println("\033[1;34mActive Loans:\033[0m " + activeLoans);
                System.out.println("\033[1;34mStatus:\033[0m " + empStatus);
                System.out.println("\033[1;34mAadhar Number:\033[0m " + aadharNum);
                System.out.println("\033[1;34mPAN Number:\033[0m " + panNum);

                if (activeLoans > 0) {
                    try {
                        // Query to fetch active loans for the employee
                        String fetchActiveLoansQuery = "SELECT loanId, loanAmount, loanDescription FROM loan WHERE loanstatus = 'APPROVED' AND userId = ?";
                        PreparedStatement fetchActiveLoansStmt = connection.prepareStatement(fetchActiveLoansQuery);
                        fetchActiveLoansStmt.setInt(1, empId);

                        ResultSet activeLoansRs = fetchActiveLoansStmt.executeQuery();

                        System.out.println("\n\033[1;36m--- Active Loans Details ---\033[0m");
                        while (activeLoansRs.next()) {
                            int loanId = activeLoansRs.getInt("loanId");
                            double loanAmount = activeLoansRs.getDouble("loanAmount");
                            String loanDescription = activeLoansRs.getString("loanDescription");

                            // Display Loan Details
                            System.out.println("\033[1;32mLoan ID:\033[0m " + loanId);
                            System.out.println("\033[1;32mDescription:\033[0m " + loanDescription);
                            System.out.println("\033[1;32mAmount:\033[0m ₹" + loanAmount);
                            System.out.println("\033[1;34m----------------------------------------\033[0m");
                        }

                        activeLoansRs.close();
                        fetchActiveLoansStmt.close();
                    } catch (SQLException e) {
                        System.out.println("\033[1;31m[Database Error]\033[0m " + e.getMessage());
                    }
                }
            } else {
                System.out.println("\033[1;31m[Error]\033[0m Employee does not exist. Please try again!");
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("\033[1;31m[Database Error]\033[0m " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\033[1;31m[Unexpected Error]\033[0m " + e.getMessage());
        }
    }

    private static void clearPendingTickets(Scanner sc) {
        try {
            // Fetch all pending tickets
            String fetchQuery = "SELECT * FROM tickets WHERE ticket_status = 'Pending'";
            PreparedStatement fetchStmt = connection.prepareStatement(fetchQuery);
            ResultSet rs = fetchStmt.executeQuery();

            System.out.println("\n\033[1;34m--- Pending Tickets ---\033[0m");
            List<Integer> ticketIds = new ArrayList<>();
            while (rs.next()) {
                int ticketId = rs.getInt("ticket_id");
                String ticketDescription = rs.getString("ticket_description");
                String userInfo = rs.getString("user_info");
                ticketIds.add(ticketId);
                System.out.println("\033[1;33mTicket ID:\033[0m " + ticketId + " | \033[1;36mDescription:\033[0m "
                        + ticketDescription + " | \033[1;36mDescription:\033[0m " + userInfo);
            }

            if (ticketIds.isEmpty()) {
                System.out.println("\033[1;32mNo pending tickets found.\033[0m");
            } else {
                // Ask user to decide which ticket(s) to clear
                System.out.print(
                        "\n\033[1;33mEnter the Ticket ID(s) to clear (comma-separated, or type 'all' to clear all): \033[0m");
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
                                System.out.println("\033[1;31mInvalid Ticket ID:\033[0m " + ticketId);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("\033[1;31mInvalid input:\033[0m " + id + " is not a valid number.");
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
                        System.out.println("\033[1;32mTicket ID " + ticketId + " has been cleared.\033[0m");
                    } else {
                        System.out.println("\033[1;31mFailed to clear Ticket ID " + ticketId + ".\033[0m");
                    }
                    clearStmt.close();
                }
            }

            rs.close();
            fetchStmt.close();
        } catch (SQLException e) {
            System.out.println("\033[1;31mDatabase Error:\033[0m " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\033[1;31mUnexpected Error:\033[0m " + e.getMessage());
        }
    }

    // private static void transferFunds(Scanner scanner) {
    // try {
    // System.out.print("Enter source Account Number of the User: ");
    // int userAcc = scanner.nextInt();
    // String checkquery = "SELECT * FROM users where user_accnum=?";
    // PreparedStatement checkingPreparedStatement =
    // connection.prepareStatement(checkquery);
    // checkingPreparedStatement.setInt(1, userAcc);
    // int testrows = checkingPreparedStatement.executeUpdate();
    // if (testrows > 0) {
    // System.out.print("Enter destination account number: ");
    // String destinationAccount = scanner.next();
    // String checkDestinationQuery = "SELECT * FROM users WHERE user_accnum = ?";
    // PreparedStatement checkDestStmt =
    // connection.prepareStatement(checkDestinationQuery);
    // checkDestStmt.setString(1, destinationAccount);
    // ResultSet destRs = checkDestStmt.executeQuery();
    // Boolean checke = destRs.next();
    // if (!checke) {
    // System.out.println("\nDestination account does not exist. Transfer
    // aborted.");
    // return;
    // }
    // System.out.print("Enter transfer amount: ");
    // double amount = scanner.nextDouble();
    // System.out.print("Enter your password: ");
    // String pass = scanner.next();
    // String checkBalanceQuery = "SELECT * FROM users WHERE user_accnum = ? and
    // user_pass= ?";
    // PreparedStatement checkStmt = connection.prepareStatement(checkBalanceQuery);
    // checkStmt.setInt(1, userAcc);
    // checkStmt.setString(2, pass);
    // ResultSet rs = checkStmt.executeQuery();
    // Boolean ifUserPassOk = rs.next();
    // if (ifUserPassOk) {
    // double currentBalance = rs.getDouble("user_bal");
    // if (currentBalance >= amount) {
    // connection.setAutoCommit(false);
    // String deductQuery = "UPDATE users SET user_bal = user_bal - ? WHERE
    // user_accnum = ?";
    // PreparedStatement deductStmt = connection.prepareStatement(deductQuery);
    // deductStmt.setDouble(1, amount);
    // deductStmt.setInt(2, userAcc);
    // deductStmt.executeUpdate();
    // String creditQuery = "UPDATE users SET user_bal = user_bal + ? WHERE
    // user_accnum = ?";
    // PreparedStatement creditStmt = connection.prepareStatement(creditQuery);
    // creditStmt.setDouble(1, amount);
    // creditStmt.setString(2, destinationAccount);
    // creditStmt.executeUpdate();
    // String insertTransactionQuery = "INSERT INTO transactions (sender_id,
    // receiver_id, amount,trans_type) VALUES (?,?,?, 'TRANSFER')";
    // PreparedStatement transactionStmt =
    // connection.prepareStatement(insertTransactionQuery);
    // transactionStmt.setInt(1, userAcc);
    // transactionStmt.setString(2, destinationAccount);
    // transactionStmt.setDouble(3, amount);
    // transactionStmt.executeUpdate();
    // connection.commit();
    // System.out.println("\nTransfer successful!");
    // } else {
    // System.out.println("\nInsufficient funds!");
    // }
    // } else {
    // System.out.println("\nInvalid Password!");
    // }
    // connection.setAutoCommit(true);
    // } else {
    // System.out.println("\n Source Account Number does not exist");
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
    private static void transferFunds(Scanner scanner) {
        try {
            // Prompt for source account number
            System.out.print("\nEnter source Account Number of the User: ");
            int userAcc = scanner.nextInt();

            // Validate source account existence
            String checkQuery = "SELECT * FROM users WHERE user_accnum = ?";
            try (PreparedStatement checkingPreparedStatement = connection.prepareStatement(checkQuery)) {
                checkingPreparedStatement.setInt(1, userAcc);
                try (ResultSet rs = checkingPreparedStatement.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("\n\033[1;31mSource account does not exist.\033[0m");
                        return;
                    }

                    // Prompt for destination account number
                    System.out.print("\nEnter destination account number: ");
                    String destinationAccount = scanner.next();

                    // Validate destination account existence
                    String checkDestinationQuery = "SELECT * FROM users WHERE user_accnum = ?";
                    try (PreparedStatement checkDestStmt = connection.prepareStatement(checkDestinationQuery)) {
                        checkDestStmt.setString(1, destinationAccount);
                        try (ResultSet destRs = checkDestStmt.executeQuery()) {
                            if (!destRs.next()) {
                                System.out.println("\n\033[1;31mDestination account does not exist. Transfer aborted.\033[0m");
                                return;
                            }
                        }
                    }

                    // Input transfer amount
                    double amount = 0;
                    while (true) {
                        System.out.print("\n\033[1;33mEnter transfer amount: \033[0m");
                        if (scanner.hasNextDouble()) {
                            amount = scanner.nextDouble();
                            if (amount <= 0) {
                                System.out.println("\033[1;31mAmount must be positive.\033[0m");
                                continue;
                            }
                            break;
                        } else {
                            System.out.println("\033[1;31mInvalid amount! Please enter a valid number.\033[0m");
                            scanner.next(); // Clear invalid input
                        }
                    }

                    // Password input
                    System.out.print("\n\033[1;33mEnter your password: \033[0m");
                    String pass = scanner.next();

                    // Validate source account balance and password
                    String checkBalanceQuery = "SELECT user_bal FROM users WHERE user_accnum = ? AND user_pass = ?";
                    try (PreparedStatement checkStmt = connection.prepareStatement(checkBalanceQuery)) {
                        checkStmt.setInt(1, userAcc);
                        checkStmt.setString(2, pass);
                        try (ResultSet userRs = checkStmt.executeQuery()) {
                            if (!userRs.next()) {
                                System.out.println("\n\033[1;31mInvalid password!\033[0m");
                                return;
                            }

                            // Check if the user has sufficient balance
                            double currentBalance = userRs.getDouble("user_bal");
                            if (currentBalance >= amount) {
                                // Start transaction
                                connection.setAutoCommit(false);
                                try {
                                    // Deduct from source account
                                    String deductQuery = "UPDATE users SET user_bal = user_bal - ? WHERE user_accnum = ?";
                                    try (PreparedStatement deductStmt = connection.prepareStatement(deductQuery)) {
                                        deductStmt.setDouble(1, amount);
                                        deductStmt.setInt(2, userAcc);
                                        deductStmt.executeUpdate();
                                    }

                                    // Credit to destination account
                                    String creditQuery = "UPDATE users SET user_bal = user_bal + ? WHERE user_accnum = ?";
                                    try (PreparedStatement creditStmt = connection.prepareStatement(creditQuery)) {
                                        creditStmt.setDouble(1, amount);
                                        creditStmt.setString(2, destinationAccount);
                                        creditStmt.executeUpdate();
                                    }

                                    // Record the transaction
                                    String insertTransactionQuery = "INSERT INTO transactions (sender_id, receiver_id, amount, trans_type) "
                                            + "VALUES (?, ?, ?, 'TRANSFER')";
                                    try (PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery)) {
                                        transactionStmt.setInt(1, userAcc);
                                        transactionStmt.setString(2, destinationAccount);
                                        transactionStmt.setDouble(3, amount);
                                        transactionStmt.executeUpdate();
                                    }

                                    // Commit transaction
                                    connection.commit();
                                    System.out.println("\n\033[1;32mTransfer successful!\033[0m");
                                } catch (SQLException e) {
                                    // Rollback transaction in case of error
                                    connection.rollback();
                                    System.out.println("\n\033[1;31mError during transfer, transaction rolled back: " + e.getMessage() + "\033[0m");
                                } finally {
                                    connection.setAutoCommit(true);
                                }
                            } else {
                                System.out.println("\n\033[1;31mInsufficient funds!\033[0m");
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("\n\033[1;31mError: " + e.getMessage() + "\033[0m");
        }
    }

}
