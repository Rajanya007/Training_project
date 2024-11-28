import java.sql.Connection;
import java.sql.DriverManager;

public class DbConn {
    public Connection conn = null;
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:ORCL";
    private static final String USER = "system";
    private static final String PASS = "Test@123";

    public Connection getConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            // Scanner scanner = new Scanner(System.in);
            // boolean exit = false;

            // while (!exit) {
            // displayMainMenu();
            // String choice = scanner.nextLine();

            // switch (choice) {
            // case "1" -> login(scanner);
            // // case 2 -> registerAccount(scanner);
            // case "3" -> exit = true;
            // default -> System.out.println("Invalid option. Try again.");
            // }
            // }
            return conn;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}
