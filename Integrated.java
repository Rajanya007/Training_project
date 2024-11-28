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
        System.out.println("\033[0;36m"); // Set text color to cyan
        System.out.println("============================================");
        System.out.println("       \033[1;35m--- Online Banking System ---\033[0;36m");
        System.out.println("============================================\033[0m");
        System.out.println();
        System.out.println("\033[1;34mPlease select an option:\033[0m");
        System.out.println("\033[1;32m1.\033[0m \033[0;37mUser Login\033[0m");
        System.out.println("\033[1;32m2.\033[0m \033[0;37mEmployee Login\033[0m");
        System.out.println("\033[1;32m3.\033[0m \033[0;37mAdmin Login\033[0m");
        System.out.println("\033[1;32m4.\033[0m \033[0;37mExit\033[0m");
        System.out.println();
        System.out.print("\033[1;33mEnter your choice: \033[0m");
    }
}
