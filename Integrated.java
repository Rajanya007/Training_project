import java.sql.*;
import java.util.Scanner;

public class Integrated {

    public static void main(String[] args) {

        DbConn conn = new DbConn();
        Connection c = conn.getConnection();
        User us = new User(c);
        Admin ad = new Admin(c);
        Employee emp = new Employee(c);
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            displayMainMenu();
            String choice = scanner.next();

            switch (choice) {
                case "1" -> us.user_login(scanner);
                case "2" -> emp.login(scanner);
                case "3" -> ad.adminLogin(scanner);
                case "4" -> exit = true;
                default -> System.out.println("Invalid option. Try again.");
            }
        }

    }

    private static void displayMainMenu() {
        System.out.println("\n--- Online Banking System ---");
        System.out.println("1. User Login");
        System.out.println("2. Employee Login");
        System.out.println("3. Admin Login");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
    }
}
