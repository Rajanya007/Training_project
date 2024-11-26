
public class BankingExceptions {

    public static class TypeMismatchException extends Exception {

        public TypeMismatchException() {
            super("Invalid input type. Please enter the correct data type.");
        }
    }

    public static class InvalidAccountCredentialsException extends Exception {

        public InvalidAccountCredentialsException() {
            super("Invalid Username or Password.");
        }
    }

    public static class InsufficientFundsException extends Exception {

        public InsufficientFundsException() {
            super("Insufficient funds for this operation.");
        }
    }

    public static class InvalidAccountNumberException extends Exception {

        public InvalidAccountNumberException() {
            super("Account number does not exist.");
        }
    }

    public static class DatabaseConnectionException extends Exception {

        public DatabaseConnectionException() {
            super("Error connecting to the database.");
        }
    }

    public static class SQLExecutionException extends Exception {

        public SQLExecutionException() {
            super("Error executing the SQL query.");
        }
    }

    public static class UnexpectedErrorException extends Exception {

        public UnexpectedErrorException() {
            super("An unexpected error occurred. Please try again later.");
        }
    }

    public static class InvalidTransactionAmountException extends Exception {

        public InvalidTransactionAmountException() {
            super("Invalid transaction amount. It must be greater than zero.");
        }
    }

    public static class TransactionRollbackException extends Exception {

        public TransactionRollbackException() {
            super("Error rolling back the transaction. The system may be inconsistent.");
        }
    }

    public static class TransactionHistoryException extends Exception {

        public TransactionHistoryException() {
            super("Error fetching transaction history.");
        }
    }

    public static class AccountRegistrationException extends Exception {

        public AccountRegistrationException() {
            super("Error registering the new account.");
        }
    }

    public static class DatabaseUpdateException extends Exception {

        public DatabaseUpdateException() {
            super("Error updating database information.");
        }
    }

    public static class AccountCreationException extends Exception {

        public AccountCreationException() {
            super("Error creating the account. Please try again.");
        }
    }

    public static class DepositException extends Exception {

        public DepositException() {
            super("Error processing deposit. Please try again.");
        }
    }

    public static class WithdrawalException extends Exception {

        public WithdrawalException() {
            super("Error processing withdrawal. Please try again.");
        }
    }

    public static class TransferException extends Exception {

        public TransferException() {
            super("Error processing fund transfer. Please try again.");
        }
    }

    public static class InvalidTransferAccountException extends Exception {

        public InvalidTransferAccountException() {
            super("Destination account does not exist. Transfer aborted.");
        }
    }

    public static class BalanceCheckException extends Exception {

        public BalanceCheckException() {
            super("Error checking account balance.");
        }
    }

    public static class InvalidDepositAmountException extends Exception {

        public InvalidDepositAmountException() {
            super("Invalid deposit amount. Amount must be greater than zero.");
        }
    }

    public static class InvalidWithdrawalAmountException extends Exception {

        public InvalidWithdrawalAmountException() {
            super("Invalid withdrawal amount. Amount must be greater than zero.");
        }
    }

    public static class AccountNotFoundException extends Exception {

        public AccountNotFoundException() {
            super("Account not found.");
        }
    }

    public static class TransferToSameAccountException extends Exception {

        public TransferToSameAccountException() {
            super("You cannot transfer funds to the same account.");
        }
    }

    public static class DatabaseTimeoutException extends Exception {

        public DatabaseTimeoutException() {
            super("Database operation timed out. Please try again later.");
        }
    }

    public static class TransactionNotFoundException extends Exception {

        public TransactionNotFoundException() {
            super("Transaction not found.");
        }
    }

    public static class BankingServiceUnavailableException extends Exception {

        public BankingServiceUnavailableException() {
            super("Banking service is temporarily unavailable. Please try again later.");
        }
    }

    public static class LoanApplicationException extends Exception {

        public LoanApplicationException() {
            super("Error processing loan application. Please check your details.");
        }
    }

    public static class LoanEligibilityException extends Exception {

        public LoanEligibilityException() {
            super("You are not eligible for this loan due to insufficient balance or other criteria.");
        }
    }

    public static class LoanRepaymentException extends Exception {

        public LoanRepaymentException() {
            super("Error processing loan repayment. Insufficient funds or other issue.");
        }
    }

    public static class LoanLimitExceededException extends Exception {

        public LoanLimitExceededException() {
            super("Loan amount exceeds your allowed limit. Please adjust the amount.");
        }
    }

    public static class TicketCreationException extends Exception {

        public TicketCreationException() {
            super("Error creating a support ticket. Please try again later.");
        }
    }

    public static class InvalidTicketDetailsException extends Exception {

        public InvalidTicketDetailsException() {
            super("Invalid ticket details. Please ensure all fields are filled correctly.");
        }
    }

}
