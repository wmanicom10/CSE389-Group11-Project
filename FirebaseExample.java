import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class FirebaseExample {
    public static void main(String[] args) throws IOException {
        // Initialize Firebase
        FirebaseConfig.initialize();

        // Access the database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users/admin");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Check if the child exists before getting value
                    if (dataSnapshot.hasChild("username")) {
                        Object usernameObj = dataSnapshot.child("username").getValue();
                        if (usernameObj != null) {
                            System.out.println("Username: " + usernameObj);
                        } else {
                            System.out.println("Username is null");
                        }
                    } else {
                        System.out.println("No username child found");
                    }
                } else {
                    System.out.println("No data exists at this reference");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}