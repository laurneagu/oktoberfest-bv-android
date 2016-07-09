package larc.ludicon.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import larc.ludicon.Adapters.FriendsListAdapter;
import larc.ludicon.Adapters.LeftPanelItemClicker;
import larc.ludicon.Adapters.LeftSidePanelAdapter;
import larc.ludicon.R;
import larc.ludicon.UserInfo.User;
import larc.ludicon.Utils.CustomView.NonScrollListView;
import larc.ludicon.Utils.UserInfo;


public class EventDetails extends Activity {

    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog dialog;
    private int TIMEOUT = 80;

    /**
     * Method that jumps to the MainActivity
     */
    public void jumpToMainActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 5 seconds
                Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(goToNextActivity);
                finish();
            }
        }, 300); // Delay time for transition to next activity -> insert any time wanted here instead of 5000
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        getActionBar().hide();
        TextView title = (TextView)findViewById(R.id.hello_message_activity);
        title.setText("Event Details");

        ((TextView)findViewById(R.id.event_organiser_points)).setText("");
        ((TextView)findViewById(R.id.event_organiser_name)).setText("");
        ((TextView)findViewById(R.id.event_date)).setText("");
        ((TextView)findViewById(R.id.event_place)).setText("");

        Intent intent = getIntent();
        final String eventUid  = intent.getStringExtra("eventUid");

        // Left side panel -------------------------------------------------------------------------------------------
        mDrawerList = (ListView) findViewById(R.id.leftMenu);
        initializeLeftSidePanel();

        User.setImage();

        // User picture and name for HEADER MENU
        TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText(User.getFirstName(getApplicationContext()) + " " + User.getLastName(getApplicationContext()));

        ImageView userPic = (ImageView) findViewById(R.id.userPicture);
        Drawable d = new BitmapDrawable(getResources(), User.image);
        userPic.setImageDrawable(d);

        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                EventDetails.this.startActivity(mainIntent);
            }
        });
        // -------------------------------------------------------------------------------------------------------------
        // Cancel Event Button
        Button cancelEvent = (Button)findViewById(R.id.cancelbtn);
        final Context context = this.getApplicationContext();
        cancelEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EventDetails.this)
                    .setTitle("Cancel Event")
                    .setMessage("Are you sure you want to cancel the event? This action can't be undone.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNegativeButton("NO", null)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(EventDetails.this, "Event will be cancelled", Toast.LENGTH_SHORT).show();
                        User.firebaseRef.child("events").child(eventUid).child("active").setValue(false);
                        jumpToMainActivity();
                    }
                }).show();
            }
        });

        Firebase eventRef = User.firebaseRef.child("events").child(eventUid);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> users = new HashMap<String, String>();
                String creatorID = "";
                String creatorName = "";
                String creatorImage = "";
                String date = "";
                String place = "";
                String sport = "";
                String privacy = "";

                for (DataSnapshot details : dataSnapshot.getChildren()) {
                    if (details.getKey().toString().equalsIgnoreCase("users")) {
                        users = (HashMap<String, String>) details.getValue();
                    }
                    if (details.getKey().toString().equalsIgnoreCase("createdBy")) {
                        creatorID = (String) details.getValue();
                    }
                    if (details.getKey().toString().equalsIgnoreCase("creatorName")) {
                        creatorName = (String) details.getValue();
                    }
                    if (details.getKey().toString().equalsIgnoreCase("creatorImage")) {
                        creatorImage = (String) details.getValue();
                    }
                    if (details.getKey().toString().equalsIgnoreCase("date")) {
                        date = (String) details.getValue();
                    }
                    if (details.getKey().toString().equalsIgnoreCase("privacy")) {
                        privacy = (String) details.getValue();
                    }
                    if (details.getKey().toString().equalsIgnoreCase("sport")) {
                        sport = (String) details.getValue();
                    }
                    if (details.getKey().toString().equalsIgnoreCase("place")) {
                        for (DataSnapshot eventData : details.getChildren()) {
                            if (eventData.getKey().toString().equalsIgnoreCase("name"))
                                place = eventData.getValue().toString();
                        }

                    }
                }





                final ImageView profilePicture = (ImageView) findViewById(R.id.profileImageView);
                Picasso.with(getApplicationContext()).load(creatorImage).into(profilePicture);

                final ImageView icon = (ImageView) findViewById(R.id.sportImage);
                String uri = "@drawable/" + sport.toLowerCase().replace(" ", "");
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                icon.setImageDrawable(res);

                ((TextView)findViewById(R.id.event_organiser_name)).setText(creatorName);
                ((TextView)findViewById(R.id.event_date)).setText(date);
                ((TextView)findViewById(R.id.event_place)).setText(place);

                ((TextView)findViewById(R.id.event_play)).setText("Will play "+ sport +
                        (users.size()-1>0?
                                " with " + ((users.size()-1)==1?
                                        "one other"
                                        :String.valueOf(users.size() - 1) + " others")
                                :" alone :(" ));

                Firebase pointsRef = User.firebaseRef.child("points").child(sport).child(creatorID);
                pointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ((TextView) findViewById(R.id.event_organiser_points)).setText(
                                dataSnapshot.getValue() == null ?
                                        "0 points - begginer" :
                                        dataSnapshot.getValue() + " points - Grand Master");
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                Runnable r = new UserListThread(users, creatorID, sport, getApplicationContext());
                new Thread(r).start();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        final ShareDialog shareDialog = new ShareDialog(this);

        // Share facebook button
        Button shareFb = (Button)findViewById(R.id.sharefb);
        shareFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("http://ludicon.info/"))
                        .setImageUrl(Uri.parse("http://www.ludicon.info/img/sports/jogging.png"))
                                .setContentTitle("First post")
                                .setContentDescription("Hello there ! This is first post in facebook")
                                .build();

                if (ShareDialog.canShow(ShareLinkContent.class) == true)
                    shareDialog.show(content);

            }
        });

    }

    public class UserListThread implements Runnable {
        private  HashMap<String, String> users = new HashMap<String, String>();
        final ArrayList<UserInfo> usersList = new ArrayList<>();
        String creatorID;
        String sport;
        Context context;
        Object syncr;
        int val = 0;

        public UserListThread(Object parameter, String creatorID, String sport, Context context) {
            users = (HashMap<String, String>) parameter;
            this.creatorID = creatorID;
            this.sport = sport;
            this.context = context;
            syncr = new Object();
        }

        public void run() {


            Iterator it = users.entrySet().iterator();
            int index = 0;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                final String user_uid = pair.getKey().toString();
                if(creatorID.equalsIgnoreCase(user_uid)){
                    continue;
                }
                usersList.add(new UserInfo("","creatorID","", index++, user_uid));
            }

            for(int i = 0 ; i  < index ; ++i){

                Firebase pct = User.firebaseRef.child("points").child(sport).child(usersList.get(i).uid);
                final int ind = i;
                pct.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usersList.get(ind).points =
                                dataSnapshot.getValue()==null?
                                        "0 points - begginer":
                                        dataSnapshot.getValue() + " points - Grand Master";
                        synchronized (syncr){
                            val ++;
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                Firebase usr = User.firebaseRef.child("users").child(usersList.get(i).uid);
                usr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for ( DataSnapshot data : snapshot.getChildren() ) {

                            if( (data.getKey()).compareTo("firstName") == 0) {
                                usersList.get(ind).name = data.getValue().toString();
                            }
                            if( (data.getKey()).compareTo("profileImageURL") == 0 )
                                usersList.get(ind).photo = data.getValue().toString();
                        }

                        synchronized (syncr){
                            val ++;
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }

//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            while(val != 2*index) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FriendsListAdapter adpt = new FriendsListAdapter(usersList, context);
                    NonScrollListView lv = (NonScrollListView) findViewById(R.id.listViewUsers);
                    if (lv != null)
                        lv.setAdapter(adpt);
                }
            });

        }
    }



    // Left side panel -----------------------------------------------------------------------------
    public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, EventDetails.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), EventDetails.this);

        final ImageButton showPanel = (ImageButton) findViewById(R.id.showPanel);
        showPanel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        // Toggle efect on left side panel
        mDrawerToggle = new android.support.v4.app.ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }
    // -----------------------------------------------------------------------------

}
