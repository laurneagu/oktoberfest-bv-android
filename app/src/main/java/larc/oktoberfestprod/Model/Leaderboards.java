/*
package larc.ludiconprod.Model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import larc.ludiconprod.UserInfo.User;

*/
/**
 * Created by LaurUser on 7/5/2017.
 *//*


public class Leaderboards {

    // Singleton for connection to database
    private static Leaderboards instance = null;
    protected Leaderboards() {
        eventsForUserReference =  User.firebaseRef.child("users").child(User.uid).child("events");
    }
    public static Leaderboards getInstance() {
        if(instance == null) {
            instance = new Leaderboards();
        }
        return instance;
    }

    // Database reference
    DatabaseReference eventsForUserReference;

    public void setPointsInDatabase(final int unsavedPoints, final String eventID) {

        // Update points for each event in user's details
        eventsForUserReference.child(eventID).child("points").setValue(unsavedPoints);

        // TODO - to remove this
        // OLD IMPLEMENTATION TO SAVE IN FIREBASE in /points node the number of points for current activity
        // NOT NEEDED ANYMORE SAVE JUST IN THE user/event_id/points NODE + automatically will update LEADERBOARDS
        */
/*
        // Get and update total number of points for user in sport
        DatabaseReference pointsRef = leaderboardsReference.child(sport).child(User.uid);
        pointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null)
                    writeToDatabaseReference(sport, Integer.parseInt(snapshot.getValue().toString()), unsavedPoints, eventID);
                else
                    writeToDatabaseReference(sport, 0, unsavedPoints, eventID);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });

        DatabaseReference generalPointsRef = User.firebaseRef.child("points").child("general").child(User.uid);
        generalPointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.getValue() != null)
                    writeToDatabaseReference("general", Integer.parseInt(snapshot.getValue().toString()), unsavedPoints, eventID);
                else
                    writeToDatabaseReference("general", 0, unsavedPoints, eventID);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
        *//*

    }

    */
/*
    private void writeToDatabaseReference(final String sport, int points, final int unsavedPoints, final String eventID) {
        DatabaseReference pointsRef = User.firebaseRef.child("points").child(sport).child(User.uid);

        pointsRef.setValue(points + unsavedPoints);
        // Save points also in general
        // Update points for each event in user's details
        if (!sport.equalsIgnoreCase("general")) {
            User.firebaseRef.child("users").child(User.uid).child("events").child(eventID).child("points").setValue(unsavedPoints);
        }
    }
    *//*

}
*/
