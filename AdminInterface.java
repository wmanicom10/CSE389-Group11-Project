import com.google.firebase.database.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import static java.lang.System.out;

public class AdminInterface {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        FirebaseConfig.initialize();
        try {
            // Loop until user enters correct admin username/password
            while (!checkUser()) {}

            while (true) {
                displayMenu();
                getUserChoice();
            }
        }
        catch (Exception e) {
            out.println("An error occurred: " + e.getMessage());
        }
    }

    // checks if user has correct admin username/password
    private static boolean checkUser() throws InterruptedException {
        // gets admin login info from firebase
        String[] adminLoginInfo = getAdminLoginInfo();
        String adminUsername = adminLoginInfo[0];
        String adminPassword = adminLoginInfo[1];

        out.println("Please enter the admin username and password");
        out.print("Username: ");
        String adminUsernameInput = scanner.nextLine();
        out.print("Password: ");
        String adminPasswordInput = scanner.nextLine();

        if (!adminUsernameInput.equals(adminUsername) || !adminPasswordInput.equals(adminPassword)) {
            out.println("\nIncorrect admin username or password. Please try again.\n");
            return false;
        }
        return true;
    }

    // gets admin username/password from firebase
    private static String[] getAdminLoginInfo() throws InterruptedException {
        final String[] adminLoginInfo = new String[2];
        final CountDownLatch latch = new CountDownLatch(1);

        // reference to the admin child of the users node in firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/admin");

        // retrieves data from firebase
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("username") && dataSnapshot.hasChild("password")) {
                        Object usernameObj = dataSnapshot.child("username").getValue();
                        Object passwordObj = dataSnapshot.child("password").getValue();
                        if (usernameObj != null && passwordObj != null) {
                            adminLoginInfo[0] = usernameObj.toString();
                            adminLoginInfo[1] = passwordObj.toString();
                        }
                    }
                }
                // data retrieval is complete
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                latch.countDown();
            }
        });
        // wait for data to be retrieved
        latch.await();

        return adminLoginInfo;
    }

    // prints admin interface menu
    private static void displayMenu() {
        out.println("\nAdmin Interface");
        out.println("1. View Users");
        out.println("2. Add User");
        out.println("3. Remove User");
        out.println("4. Exit");
        out.print("Choose an option: ");
    }

    // gets user choice for admin abilities
    private static void getUserChoice() {
        // make sure input is a number
        if (scanner.hasNextInt()) {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewUsers();
                    break;
                case 2:
                    if (addUser()) {
                        out.println("\nSuccessfully added user");
                    }
                    break;
                case 3:
                    if (removeUser()) {
                        out.println("\nSuccessfully removed user");
                    }
                    break;
                case 4:
                    // ends the program
                    out.println("Exiting...");
                    System.exit(0);
                default:
                    // make sure input is 1-4
                    out.println("Invalid option. Please try again.");
            }
        }
        else {
            out.println("Invalid input. Please enter a number between 1 and 4.");
            scanner.nextLine();
        }
    }

    // prints all users in the database
    public static void viewUsers() {
        final CountDownLatch latch = new CountDownLatch(1);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        // retrieves all usernames in the users node
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("\nUsers List:");
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String username = userSnapshot.child("username").getValue(String.class);
                        if (username != null) {
                            System.out.println("Username: " + username);
                        }
                    }
                } else {
                    System.out.println("No users found.");
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            out.println(e.getMessage());
        }
    }

    // adds user to the database
    public static boolean addUser() {
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = {false};

        out.print("\nEnter username: ");
        String username = scanner.nextLine();
        out.print("Enter password: ");
        String password = scanner.nextLine();

        // check if username is already taken
        if (isUsernameTaken(username)) {
            out.println("\nUsername is already taken. Please choose a different username.");
            return false;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        // generates a unique key for new user
        String userId = ref.push().getKey();
        if (userId != null) {
            // map to store username and password so it looks like json object
            Map<String, String> userData = new HashMap<>();
            userData.put("username", username);
            userData.put("password", password);

            // adds new user to database
            ref.child(userId).setValue(userData, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    out.println(databaseError.getMessage());
                } else {
                    success[0] = true;
                }
                latch.countDown();  // Ensure we notify that the operation is complete
            });

            try {
                latch.await();  // Wait for the operation to complete before proceeding
            } catch (InterruptedException e) {
                out.println(e.getMessage());
            }
        }
        return success[0];
    }

    // checks if username is in database
    private static boolean isUsernameTaken(final String username) {
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] isTaken = {false};

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    isTaken[0] = true;
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            out.println(e.getMessage());
        }
        return isTaken[0];
    }

    // removes user from the database
    public static boolean removeUser() {
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = {false};

        out.print("\nEnter the username of the user to remove: ");
        String username = scanner.nextLine();

        // Check if user exists
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        ref.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exists, remove the user
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Removing the user
                        userSnapshot.getRef().removeValue((databaseError, databaseReference) -> {
                            if (databaseError != null) {
                                out.println("\nFailed to remove user " + username + ": " + databaseError.getMessage());
                            } else {
                                success[0] = true;
                            }
                            latch.countDown();  // Ensure we notify that the operation is complete
                        });
                    }
                } else {
                    out.println("\nUser " + username + " not found.");
                    latch.countDown();  // Still count down even if the user was not found
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                latch.countDown();  // Count down in case of error to prevent infinite waiting
            }
        });

        try {
            latch.await();  // Wait for the operation to complete before proceeding
        } catch (InterruptedException e) {
            out.println(e.getMessage());
        }

        return success[0];
    }
}