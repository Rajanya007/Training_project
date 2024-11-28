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
            System.out.println("\033[1;36m----------------------------------------------\033[0m");
            System.out.println("\033[1;33m        --- User Login ---\033[0m");
            System.out.println("\033[1;36m----------------------------------------------\033[0m");
    
            System.out.print("\033[1;34mEnter Account Number: \033[0m");
            String accountNumber = scanner.next();
    
            System.out.print("\033[1;34mEnter Password: \033[0m");
            String pass = scanner.next();
    
            String query = "SELECT * FROM users WHERE user_accnum = ? AND user_pass = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, pass);
    
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String customerName = rs.getString("user_name");
                System.out.println("\n\033[1;32mLogin Successful. Welcome, " + customerName + "!\033[0m");
                showUserAccountMenu(accountNumber);
            } else {
                System.out.println("\033[1;31mInvalid Account Number or Password. Please try again.\033[0m");
            }
        } catch (SQLException e) {
            System.out.println("\033[1;31mError: Database connection failed. Please check your connection or contact support.\033[0m");
            System.out.println("\033[1;31mDetails: " + e.getMessage() + "\033[0m");
        } catch (Exception e) {
            System.out.println("\033[1;31mUnexpected error occurred. Please try again later.\033[0m");
            System.out.println("\033[1;31mDetails: " + e.getMessage() + "\033[0m");
        }
    }
    
    private static void showUserAccountMenu(String accountNumber) {
        Scanner scanner = new Scanner(System.in);
        boolean logout = false;
    
        while (!logout) {
            System.out.println("\033[1;36m----------------------------------------------\033[0m");
            System.out.println("\033[1;33m        --- Account Menu ---\033[0m");
            System.out.println("\033[1;36m----------------------------------------------\033[0m");
    
            System.out.println("\033[1;32m1.\033[0m \033[0;37mCheck Balance\033[0m");
            System.out.println("\033[1;32m2.\033[0m \033[0;37mTransfer Funds\033[0m");
            System.out.println("\033[1;32m3.\033[0m \033[0;37mTransaction History\033[0m");
            System.out.println("\033[1;32m4.\033[0m \033[0;37mApply Loan\033[0m");
            System.out.println("\033[1;32m5.\033[0m \033[0;37mRaise an Issue (Ticket)\033[0m");
            System.out.println("\033[1;32m6.\033[0m \033[0;37mCheck Loan Status\033[0m");
            System.out.println("\033[1;32m7.\033[0m \033[0;37mCheck Ticket Status\033[0m");
            System.out.println("\033[1;32m8.\033[0m \033[0;37mLogout\033[0m");
            System.out.print("\033[1;33mEnter your choice: \033[0m");
    
            String choice = scanner.next();
            switch (choice) {
                case "1" -> checkUserBalance(accountNumber);
                case "2" -> userTransferFunds(accountNumber, scanner);
                case "3" -> viewUserTransactionHistory(accountNumber);
                case "4" -> userLoanApply(accountNumber, scanner);
                case "5" -> raiseTicket(accountNumber, scanner);
                case "6" -> checkLoanStatus(accountNumber, scanner);
                case "7" -> checkTicketStatus(accountNumber, scanner);
                case "8" -> {
                    System.out.println("\033[1;32mLogging out... Goodbye!\033[0m");
                    logout = true;
                }
                default -> System.out.println("\033[1;31mInvalid option. Please try again.\033[0m");
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
            System.out.println("\033[1;36m\n--- Ticket Status ---\033[0m");
            
            if (!rs.isBeforeFirst()) {
                System.out.println("\033[1;31mNo tickets found for your account.\033[0m");
            } else {
                while (rs.next()) {
                    int ticketId = rs.getInt("ticket_id");
                    String ticketDesc = rs.getString("ticket_description");
                    String ticketStatus = rs.getString("ticket_status");
    
                    System.out.printf("\n\033[1;32mTicket ID: %d | Description: %s | Status: %s\033[0m\n",
                            ticketId, ticketDesc, ticketStatus);
                }
            }
        } catch (SQLException e) {
            System.out.println("\033[1;31mError fetching ticket status: " + e.getMessage() + "\033[0m");
        }
    }
    
    // pending
    private static void checkLoanStatus(String accountNumber, Scanner sc) {
        try {
            String query = "SELECT * FROM loan WHERE userid = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, accountNumber);
    
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\033[1;36m\n--- Loan Application Status ---\033[0m");
    
            if (!rs.isBeforeFirst()) {
                System.out.println("\033[1;31mNo loan applications found for your account.\033[0m");
            } else {
                while (rs.next()) {
                    int loanId = rs.getInt("loanid");
                    double loanAmount = rs.getDouble("loanamount");
                    String loanStatus = rs.getString("loanstatus");
    
                    System.out.printf("\n\033[1;32mLoan ID: %d | Amount: $%.2f | Status: %s\033[0m\n",
                            loanId, loanAmount, loanStatus);
                }
            }
        } catch (SQLException e) {
            System.out.println("\033[1;31mError fetching loan status: " + e.getMessage() + "\033[0m");
        }
    }
    
    // done
    private static void raiseTicket(String accountNumber, Scanner sc) {
        try {
            System.out.println("\n\033[1;36m--- Raise a Ticket ---\033[0m");
            System.out.print("\033[1;33mEnter a brief description of your issue: \033[0m");
            sc.nextLine(); // Consume leftover newline
            String description = sc.nextLine();
    
            String getMaxAccNumQuery = "SELECT MAX(ticket_id) as maxticket FROM tickets";
            PreparedStatement maxAccNumStmt = connection.prepareStatement(getMaxAccNumQuery);
            ResultSet rs = maxAccNumStmt.executeQuery();
    
            int startingPoint = 1000; // Starting ticket ID
            int ticketRef = startingPoint; // Default to starting point if no tickets exist
    
            if (rs.next()) {
                int maxAccNum = rs.getInt("maxticket");
                ticketRef = Math.max(maxAccNum + 1, startingPoint); // Ensure it starts from the defined starting point
            }
    
            String query = "INSERT INTO tickets (ticket_id, ticket_description, ticket_status, user_info) " +
                    "VALUES (?, ?, 'Pending', ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, ticketRef);
            pstmt.setString(2, description);
            pstmt.setString(3, accountNumber);
    
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("\033[1;32mYour ticket has been raised successfully!\033[0m");
                System.out.println("\033[1;33mTicket ID: " + ticketRef + "\033[0m");
            } else {
                System.out.println("\033[1;31mFailed to raise the ticket. Please try again.\033[0m");
            }
    
        } catch (SQLException e) {
            System.out.println("\033[1;31mError raising ticket: " + e.getMessage() + "\033[0m");
        }
    }
    
    // DONE
    private static void checkUserBalance(String accountNumber) {
        try {
            String query = "SELECT user_bal FROM users WHERE user_accnum = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
    
            if (rs.next()) {
                double balance = rs.getDouble("user_bal");
                System.out.printf("\n\033[1;36mCurrent Balance: \033[0m\033[1;32m$%.2f\033[0m%n", balance);
            } else {
                System.out.println("\033[1;31mAccount not found. Please check your account number.\033[0m");
            }
        } catch (SQLException e) {
            System.out.println("\033[1;31mError checking balance: " + e.getMessage() + "\033[0m");
        }
    }
    
    // DONE
    private static void deposit(String accountNumber, Scanner scanner) {
        try {
            System.out.print("\033[1;36mEnter deposit amount: \033[0m");
            double amount = scanner.nextDouble();
    
            if (amount <= 0) {
                System.out.println("\033[1;31mInvalid amount. Please enter a positive value.\033[0m");
                return;
            }
    
            String updateQuery = "UPDATE users SET user_bal = user_bal + ? WHERE user_accnum = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setDouble(1, amount);
            updateStmt.setString(2, accountNumber);
            int rowsUpdated = updateStmt.executeUpdate();
    
            if (rowsUpdated > 0) {
                String insertTransactionQuery = "INSERT INTO transactions (sender_id, receiver_id, amount, trans_type) VALUES (?, ?, ?, 'DEPOSIT')";
                PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery);
                transactionStmt.setString(1, accountNumber);
                transactionStmt.setString(2, accountNumber);
                transactionStmt.setDouble(3, amount);
                transactionStmt.executeUpdate();
    
                System.out.println("\n\033[1;32mDeposit successful! Your balance has been updated.\033[0m");
            } else {
                System.out.println("\033[1;31mAccount not found or deposit failed. Please try again.\033[0m");
            }
    
        } catch (SQLException e) {
            System.out.println("\033[1;31mError during deposit: " + e.getMessage() + "\033[0m");
        }
    }
    
    // DONE
    private static void withdraw(String accountNumber, Scanner scanner) {
        try {
            System.out.print("\033[1;36mEnter withdrawal amount: \033[0m");
            double amount = scanner.nextDouble();
    
            if (amount <= 0) {
                System.out.println("\033[1;31mInvalid amount. Please enter a positive value.\033[0m");
                return;
            }
    
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
    
                    String insertTransactionQuery = "INSERT INTO transactions (sender_id, receiver_id, amount, trans_type) VALUES (?, ?, ?, 'WITHDRAWAL')";
                    PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery);
                    transactionStmt.setString(1, accountNumber);
                    transactionStmt.setString(2, accountNumber);
                    transactionStmt.setDouble(3, amount);
                    transactionStmt.executeUpdate();
    
                    System.out.println("\n\033[1;32mWithdrawal successful! Your balance has been updated.\033[0m");
                } else {
                    System.out.println("\033[1;31mInsufficient funds! Please ensure you have enough balance to withdraw.\033[0m");
                }
            } else {
                System.out.println("\033[1;31mAccount not found. Please check your account number.\033[0m");
            }
    
        } catch (SQLException e) {
            System.out.println("\033[1;31mError during withdrawal: " + e.getMessage() + "\033[0m");
        }
    }
    
    // DONE
    private static void userTransferFunds(String sourceAccount, Scanner scanner) {
        try {
            System.out.print("\033[1;36mEnter destination account number: \033[0m");
            String destinationAccount = scanner.next();
    
            // Check if the destination account exists
            String checkDestinationQuery = "SELECT * FROM users WHERE user_accnum = ?";
            PreparedStatement checkDestStmt = connection.prepareStatement(checkDestinationQuery);
            checkDestStmt.setString(1, destinationAccount);
            ResultSet destRs = checkDestStmt.executeQuery();
            if (!destRs.next()) {
                System.out.println("\033[1;31mDestination account does not exist. Transfer aborted.\033[0m");
                return;
            }
    
            // Get transfer amount
            double amount = 0;
            while (true) {
                System.out.print("\033[1;36mEnter transfer amount: \033[0m");
                if (scanner.hasNextDouble()) {
                    amount = scanner.nextDouble();
                    if (amount <= 0) {
                        System.out.println("\033[1;31mInvalid amount. Please enter a positive value.\033[0m");
                        continue;
                    }
                    break;
                } else {
                    System.out.println("\033[1;31mInvalid Transfer Amount. Please enter a numeric value.\033[0m");
                    scanner.next(); // Consume invalid input
                }
            }
    
            // Verify user password
            System.out.print("\033[1;36mEnter your password: \033[0m");
            String pass = scanner.next();
    
            String checkBalanceQuery = "SELECT * FROM users WHERE user_accnum = ? AND user_pass = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkBalanceQuery);
            checkStmt.setString(1, sourceAccount);
            checkStmt.setString(2, pass);
            ResultSet rs = checkStmt.executeQuery();
    
            if (rs.next()) {
                double currentBalance = rs.getDouble("user_bal");
    
                // Check if the user has enough balance
                if (currentBalance >= amount) {
                    connection.setAutoCommit(false);
    
                    // Deduct the amount from the source account
                    String deductQuery = "UPDATE users SET user_bal = user_bal - ? WHERE user_accnum = ?";
                    PreparedStatement deductStmt = connection.prepareStatement(deductQuery);
                    deductStmt.setDouble(1, amount);
                    deductStmt.setString(2, sourceAccount);
                    deductStmt.executeUpdate();
    
                    // Credit the amount to the destination account
                    String creditQuery = "UPDATE users SET user_bal = user_bal + ? WHERE user_accnum = ?";
                    PreparedStatement creditStmt = connection.prepareStatement(creditQuery);
                    creditStmt.setDouble(1, amount);
                    creditStmt.setString(2, destinationAccount);
                    creditStmt.executeUpdate();
    
                    // Insert the transaction record
                    String insertTransactionQuery = "INSERT INTO transactions (sender_id, receiver_id, amount, trans_type) VALUES (?, ?, ?, 'TRANSFER')";
                    PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery);
                    transactionStmt.setString(1, sourceAccount);
                    transactionStmt.setString(2, destinationAccount);
                    transactionStmt.setDouble(3, amount);
                    transactionStmt.executeUpdate();
    
                    connection.commit();
                    System.out.println("\n\033[1;32mTransfer successful! Your balance has been updated.\033[0m");
                } else {
                    System.out.println("\033[1;31mInsufficient funds! Please ensure you have enough balance to transfer.\033[0m");
                }
            } else {
                System.out.println("\033[1;31mSource account does not exist OR Invalid Password!\033[0m");
            }
    
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("\033[1;31mError during transfer: " + e.getMessage() + "\033[0m");
        } catch (Exception e) {
            System.out.println("\033[1;31mInvalid Entry. Please try again! " + e.getMessage() + "\033[0m");
        }
    }
    
    // done
    private static void viewUserTransactionHistory(String accountNumber) {
        try {
            // SQL query to fetch transactions involving the specified user as sender or receiver
            String query = "SELECT transaction_id, sender_id, receiver_id, amount, transaction_date " +
                    "FROM transactions " +
                    "WHERE sender_id = ? OR receiver_id = ? " +
                    "ORDER BY transaction_date DESC";
    
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, accountNumber);
    
            ResultSet rs = pstmt.executeQuery();
    
            System.out.println("\n\033[1;36m--- Transaction History ---\033[0m");
            boolean hasTransactions = false;
    
            // Check if there are any transactions
            while (rs.next()) {
                hasTransactions = true;
                int transactionId = rs.getInt("transaction_id");
                String senderId = rs.getString("sender_id");
                String receiverId = rs.getString("receiver_id");
                double amount = rs.getDouble("amount");
                Timestamp transactionDate = rs.getTimestamp("transaction_date");
    
                // Display transaction details in a readable format
                System.out.printf("\n\033[1;32mTransaction ID:\033[0m %d | \033[1;34mSender ID:\033[0m %s | \033[1;34mReceiver ID:\033[0m %s " +
                                "| \033[1;33mAmount:\033[0m $%.2f | \033[1;35mDate:\033[0m %s%n",
                        transactionId, senderId, receiverId, amount, transactionDate);
            }
    
            if (!hasTransactions) {
                System.out.println("\033[1;31mNo transactions found for User ID: " + accountNumber + "\033[0m");
            }
    
            // Close resources
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("\033[1;31mError fetching transaction history: " + e.getMessage() + "\033[0m");
        }
    }
    
    // done with exc

    private static void userLoanApply(String accountNumber, Scanner scanner) {
        try {
            while (true) {
                // Loan type selection with color
                System.out.println("\n\033[1;36m--- Select Loan Type ---\033[0m");
                System.out.println("\033[1;32m1.\033[0m Personal with 10% interest rate");
                System.out.println("\033[1;32m2.\033[0m Car with 12% interest rate");
                System.out.println("\033[1;32m3.\033[0m Home with 15% interest rate");
                System.out.print("\n\033[1;33mEnter your choice: \033[0m");
                String loanType = scanner.next();
                int rate = 0;
    
                // Loan type validation
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
                        System.out.println("\n\033[1;31mInvalid input! Please select a valid loan type from the options above.\033[0m");
                        continue;
                }
    
                // Loan amount input with validation
                double loanAmount = 0;
                while (true) {
                    System.out.print("\n\033[1;33mEnter loan amount: \033[0m");
                    if (scanner.hasNextDouble()) {
                        loanAmount = scanner.nextDouble();
                        if (loanAmount <= 0) {
                            System.out.println("\033[1;31mPlease enter a positive loan amount.\033[0m");
                            continue;
                        }
                        break;
                    } else {
                        System.out.println("\033[1;31mInvalid Loan Amount! Please enter a valid number.\033[0m");
                        scanner.next(); // Clear the invalid input
                    }
                }
    
                // Loan duration input with validation
                int loanDuration = 0;
                while (true) {
                    System.out.print("\n\033[1;33mEnter loan duration (in months): \033[0m");
                    if (scanner.hasNextInt()) {
                        loanDuration = scanner.nextInt();
                        if (loanDuration <= 0) {
                            System.out.println("\033[1;31mLoan duration must be a positive number.\033[0m");
                            continue;
                        }
                        break;
                    } else {
                        System.out.println("\033[1;31mInvalid Loan Duration! Please enter a valid number.\033[0m");
                        scanner.next(); // Clear the invalid input
                    }
                }
    
                // Consume leftover newline
                scanner.nextLine();
    
                // Loan description input
                System.out.print("\n\033[1;33mEnter loan description: \033[0m");
                String loanDescription = scanner.nextLine();
    
                // EMI Calculation for simple interest
                double interest = (loanAmount * rate * loanDuration) / 1200;
                double emi = (loanAmount + interest) / loanDuration;
    
                // Constructing query to insert loan application
                String query = "INSERT INTO loan (loandescription, loanamount, loanduration, loanemi, loanstatus, userid) " +
                        "VALUES (?, ?, ?, ?, 'PENDING', ?)";
    
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setString(1, loanDescription);
                pstmt.setDouble(2, loanAmount);
                pstmt.setInt(3, loanDuration);
                pstmt.setDouble(4, emi);
                pstmt.setString(5, accountNumber);
    
                // Executing the insert query
                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("\n\033[1;32mThank you for applying! We'll get back to you for any updates.\033[0m");
                } else {
                    System.out.println("\n\033[1;31mSomething went wrong! Please try again.\033[0m");
                }
    
                break; // Exit the loop after successful application
    
            }
        } catch (SQLException e) {
            System.out.println("\n\033[1;31mDatabase error: " + e.getMessage() + "\033[0m");
            System.out.println("\n\033[1;31mPlease try again later.\033[0m");
        } catch (Exception e) {
            System.out.println("\n\033[1;31mSomething went wrong: " + e.getMessage() + "\033[0m");
            System.out.println("\n\033[1;31mPlease ensure you provide valid inputs.\033[0m");
        }
    }
    
}
