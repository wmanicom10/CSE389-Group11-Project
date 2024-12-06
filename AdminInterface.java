import java.util.Scanner;

import static java.lang.System.out;

public class AdminInterface {
    private static final Scanner scanner = new Scanner(System.in);
    // get username/password from database
    private static final String adminUsername = "a";
    private static final String adminPassword = "b";

    public static void main(String[] args) {
        try {
            // Check admin username/password
            while (!checkUser()) {}
            // menu loop
            while (true) {
                displayMenu();
                getUserChoice();
            }
        }
        catch (Exception e) {
            out.println("An error occurred: " + e.getMessage());
        }
    }

    private static boolean checkUser() {
        boolean isAdmin = false;
        out.println("Please enter the admin username and password");
        out.print("Username: ");
        String adminUsernameInput = scanner.nextLine();
        out.print("Password: ");
        String adminPasswordInput = scanner.nextLine();
        if (adminUsernameInput.equals(adminUsername) && adminPasswordInput.equals(adminPassword)) {
            isAdmin = true;
        }
        else {
            out.println("\nIncorrect admin username or password. Please try again.\n");
        }
        return isAdmin;
    }

    private static void displayMenu() {
        out.println("\nAdmin Interface");
        out.println("1. View Users");
        out.println("2. Add User");
        out.println("3. Remove User");
        out.println("4. Exit");
        out.print("Choose an option: ");
    }

    private static void getUserChoice() {
        if (scanner.hasNextInt()) {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewUsers();
                    break;
                case 2:
                    addUser();
                    break;
                case 3:
                    removeUser();
                    break;
                case 4:
                    out.println("Exiting...");
                    System.exit(0);
                default:
                    out.println("Invalid option. Please try again.");
            }
        }
        else {
            out.println("Invalid input. Please enter a number between 1 and 4.");
            scanner.next();
        }
    }

    // view all users in the database
    public static void viewUsers() {
        out.println("\nView Users");
    }

    // add a user to the system
    public static void addUser() {
        out.println("\nAdd User");
    }

    // remove a user from the system
        public static void removeUser() {
        out.println("\nRemove User");
    }
}
