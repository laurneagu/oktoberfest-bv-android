package larc.ludiconprod.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import larc.ludiconprod.Adapters.FriendsListAdapter;
import larc.ludiconprod.Adapters.LeftPanelItemClicker;
import larc.ludiconprod.Adapters.LeftSidePanelAdapter;
import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.CustomView.NonScrollListView;
import larc.ludiconprod.Utils.UserInfo;
import larc.ludiconprod.Utils.util.DateManager;
import larc.ludiconprod.Utils.util.Utils;


public class EventDetails extends Activity implements OnMapReadyCallback {

    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog dialog;
    private int TIMEOUT = 80;
    public static int numberOfFriendsAddded = 0;
    public static long participants = 0;
    public static int roomCapacity = 0;
    public static boolean doIparticipate = false;
    private GoogleMap m_gmap;
    public ImageButton header_button;
    public static boolean creatorIsCurrentUser = false;
    public static EventDetails instance;
    public static String eventID;

    @Override
    public void onMapReady(GoogleMap map) {
        m_gmap = map;
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                    }
                });
            }
        });

    }

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
        try{
        super.onCreate(savedInstanceState);

            // Hide App bar
            // If the Android version is lower than Jellybean, use this call to hide
            // the status bar.
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            // remove title
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_event_details);
        //getActionBar().hide();
        TextView title = (TextView)findViewById(R.id.hello_message_activity);
        //title.setText("Event Details");
        doIparticipate = false;
            instance = this;
        ((TextView)findViewById(R.id.event_organiser_points)).setText("");
        ((TextView)findViewById(R.id.event_organiser_name)).setText("");
        ((TextView)findViewById(R.id.event_date)).setText("");
        ((TextView)findViewById(R.id.event_place)).setText("");
        ((TextView)findViewById(R.id.event_players)).setText("");
        ((TextView)findViewById(R.id.event_desc)).setText("");

        Intent intent = getIntent();
        final String eventUid  = intent.getStringExtra("eventUid");
        eventID = eventUid;

        // Left side panel -------------------------------------------------------------------------------------------
        mDrawerList = (ListView) findViewById(R.id.leftMenu);
        initializeLeftSidePanel();

        User.setImage();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // User picture and name for HEADER MENU
            // User picture and name for HEADER MENU
            Typeface segoeui = Typeface.createFromAsset(getAssets(), "fonts/seguisb.ttf");

            final TextView userName = (TextView) findViewById(R.id.userName);
            userName.setText(User.getFirstName(getApplicationContext()));
            userName.setTypeface(segoeui);

            TextView userSportsNumber = (TextView)findViewById(R.id.userSportsNumber);
            userSportsNumber.setText(User.getNumberOfSports(getApplicationContext()));
            userSportsNumber.setTypeface(segoeui);

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
        final Button cancelEvent = (Button)findViewById(R.id.cancelbtn);
        //cancelEvent.setVisibility(View.INVISIBLE);
        final Context context = this.getApplicationContext();
        cancelEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(EventDetails.this, R.style.MyAlertDialogStyle);
                builder.setTitle("Cancel Event")
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

        final Button shareFb = (Button)findViewById(R.id.sharefb);
        final Button chatBtn = (Button) findViewById(R.id.eventchatbtn);
        final Button addFriend = (Button) findViewById(R.id.addfriend);
        final Button removeFriend = (Button) findViewById(R.id.removefriend);

        DatabaseReference eventRef = User.firebaseRef.child("events").child(eventUid);
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
                String latitude = "";
                String longitude = "";
                String isOfficial = "";
                int players = 0;
                String desc = "";
                long evenDate = 0;

                for (DataSnapshot details : dataSnapshot.getChildren()) {
                    if (details.getKey().toString().equalsIgnoreCase("users")) {
                        users = (HashMap<String, String>) details.getValue();
                    }
                    if (details.getKey().toString().equalsIgnoreCase("createdBy")) {
                        creatorID = (String) details.getValue();
                    }
                    if ( details.getKey().toString().equalsIgnoreCase("isOfficial")) {
                        isOfficial = details.getValue().toString();
                    }
                    if (details.getKey().toString().equalsIgnoreCase("creatorName")) {
                        creatorName = (String) details.getValue();
                    }
                    if (details.getKey().toString().equalsIgnoreCase("creatorImage")) {
                        creatorImage = (String) details.getValue();
                    }
                    if (details.getKey().toString().equalsIgnoreCase("date")) {
                        date = (String) DateManager.convertFromSecondsToText((long)details.getValue());
                        evenDate = (long)details.getValue();
                    }
                    if (details.getKey().toString().equalsIgnoreCase("privacy")) {
                        privacy = (String) details.getValue();
                    }
                    if (details.getKey().toString().equalsIgnoreCase("sport")) {
                        sport = (String) details.getValue();
                    }
                    if (details.getKey().toString().equalsIgnoreCase("description")) {
                        desc = details.getValue().toString();
                    }
                    if (details.getKey().toString().equalsIgnoreCase("roomCapacity")) {
                        players = Integer.parseInt(details.getValue().toString());
                        roomCapacity = players;
                    }
                    if (details.getKey().toString().equalsIgnoreCase("users")) {
                        participants = details.getChildrenCount();
                        for (DataSnapshot data : details.getChildren() )
                        {
                            if ( data.getKey().equalsIgnoreCase(User.uid))
                            {
                                removeFriend.setVisibility(View.VISIBLE);
                                addFriend.setVisibility(View.VISIBLE);
                                chatBtn.setVisibility(View.VISIBLE);
                                shareFb.setVisibility(View.VISIBLE);
                                initializeLeaveEventButton(details, eventUid);
                            }
                        }
                    }
                    if (details.getKey().toString().equalsIgnoreCase("place")) {
                        for (DataSnapshot eventData : details.getChildren()) {
                            if (eventData.getKey().toString().equalsIgnoreCase("name"))
                                place = eventData.getValue().toString();
                            if (eventData.getKey().toString().equalsIgnoreCase("latitude"))
                                latitude = eventData.getValue().toString();
                            if (eventData.getKey().toString().equalsIgnoreCase("longitude"))
                                longitude = eventData.getValue().toString();
                        }

                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                        m_gmap.clear();

                        m_gmap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Event Venue"));

                        CameraUpdate center=
                                CameraUpdateFactory.newLatLngZoom(latLng,15);

                        m_gmap.moveCamera(center);

                        //m_gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
                }

                final String eventDate = date;
                final String eventSport = sport;
                final String eventPrivacy = privacy;
                final String eventLat = latitude;
                final String eventLong = longitude;
                final String eventisOfficial = isOfficial;
                final String eventPlace = place;
                final String eventDesc = desc;
                final Date evDate = new Date(evenDate);
                // Edit Event Button
                final Button editEvent = (Button)findViewById(R.id.editbtn);
                //editEvent.setVisibility(View.INVISIBLE);
                editEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), EditEventActivity.class);
                        intent.putExtra("eventID", eventUid);
                        intent.putExtra("eventDate", evDate.getTime() + "");
                        intent.putExtra("sport", eventSport);
                        intent.putExtra("eventPrivacy", eventPrivacy);
                        intent.putExtra("latitude", eventLat);
                        intent.putExtra("longitude", eventLong);
                        intent.putExtra("isOfficial", eventisOfficial);
                        intent.putExtra("place",eventPlace);
                        intent.putExtra("desc", eventDesc);
                        intent.putExtra("maxPlayers", roomCapacity+"");
                        startActivity(intent);
                    }
                });

                // Hide Edit and Cancel Buttons if user is not the owner of the event
                if ( creatorID.equalsIgnoreCase(User.uid) ) {
                    cancelEvent.setVisibility(View.VISIBLE);
                    editEvent.setVisibility(View.VISIBLE);
                    creatorIsCurrentUser = true;
                }

                final ImageView profilePicture = (ImageView) findViewById(R.id.profileImageView);
                profilePicture.setBackgroundResource(R.drawable.defaultpicture);
                Picasso.with(getApplicationContext()).load(creatorImage).into(profilePicture);

                final ImageView icon = (ImageView) findViewById(R.id.sportImage);
                String uri = "@drawable/" + sport.toLowerCase().replace(" ", "");
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                icon.setImageDrawable(res);

                ((TextView)findViewById(R.id.event_organiser_name)).setText(creatorName);
                ((TextView)findViewById(R.id.event_date)).setText(date);
                ((TextView)findViewById(R.id.event_place)).setText(place);
                ((TextView)findViewById(R.id.event_desc)).setText(desc);
                ((TextView)findViewById(R.id.event_players)).setText(users.size()+"/"+players+" players");


                ((TextView)findViewById(R.id.event_play)).setText("Will play "+ sport +
                        (users.size()-1>0?
                                " with " + ((users.size()-1)==1?
                                        "one other"
                                        :String.valueOf(users.size() - 1) + " others")
                                :" alone :(" ));

                DatabaseReference pointsRef = User.firebaseRef.child("points").child(sport).child(creatorID);
                pointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ((TextView) findViewById(R.id.event_organiser_points)).setText(
                                dataSnapshot.getValue() == null ?
                                        "0 points - Beginner" :
                                        dataSnapshot.getValue() + " points - Grand Master");
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });

                Runnable r = new UserListThread(users, creatorID, sport, getApplicationContext());
                new Thread(r).start();

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

        final ShareDialog shareDialog = new ShareDialog(this);

        // Share facebook button
        shareFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("http://ludicon.info/"))
                        .setImageUrl(Uri.parse("http://www.ludicon.info/img/sports/jogging.png"))
                                .setContentTitle("Activity on Ludicon")
                                .setContentDescription("I will attend an event in Ludicon ! Let's go and play ! ")
                                .build();

                if (ShareDialog.canShow(ShareLinkContent.class) == true)
                    shareDialog.show(content);

            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), GroupChatTemplate.class);
                intent.putExtra("eventID",eventUid);
                startActivity(intent);
            }
        });

        // Add offline friend
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if ( EventDetails.participants == EventDetails.roomCapacity ) {
                    Toast.makeText(getApplicationContext(),"The event has reached its maximum capacity",Toast.LENGTH_SHORT).show();
                }
                else {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(EventDetails.this, R.style.MyAlertDialogStyle);
                    builder.setTitle("Add offline friend")
                            .setMessage("Are you sure you want to add an offline friend?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setNegativeButton("NO", null)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    FirebaseDatabase.getInstance().getReference().child("events").child(eventUid).child("users").child("+" + 5*participants +userName.getText()).setValue(true);
                                    Toast.makeText(getApplicationContext(),"You have added 1 friend",Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                    EventDetails.this.startActivity(mainIntent);
//                                    startActivity(getIntent());
                                }
                            }).show();
                }
            }
        });

        // Remove offline friend
        removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                 DatabaseReference pct = User.firebaseRef.child("events").child(eventUid).child("users");
                 pct.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ds) {
                            final DataSnapshot dataSnapshot = ds;
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(EventDetails.this, R.style.MyAlertDialogStyle);
                            builder.setTitle("Remove offline friend")
                                    .setMessage("Are you sure you want to remove an offline friend?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setNegativeButton("NO", null)
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            for (DataSnapshot data : dataSnapshot.getChildren())
                                                if (data.getKey().charAt(0) == '+') {
                                                    String users = data.getKey().substring(1);
                                                    int i = 0;
                                                    while (Character.isDigit(users.charAt(i))) i++;
                                                    users = users.substring(i);
                                                    if (users.equalsIgnoreCase(userName.getText().toString())) {
                                                        User.firebaseRef.child("events").child(eventUid).child("users").child(data.getKey()).removeValue();
                                                        finish();
                                                        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                                        EventDetails.this.startActivity(mainIntent);
//                                                        startActivity(getIntent());
                                                        return;
                                                    }
                                                }



                                            Toast.makeText(getApplicationContext(), "You have added no friends", Toast.LENGTH_SHORT).show();
                                        }
                                    }).show();



                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {

                        }
                    });

                }
        });
        }
        catch(Exception exc) {
            Utils.quit();
        }
    }

    public void removeUser(final String name, final String uid){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(EventDetails.this, R.style.MyAlertDialogStyle);
        builder.setTitle("Remove user")
            .setMessage("Are you sure you want to remove " + name + " ?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setNegativeButton("NO", null)
            .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    DatabaseReference pct = User.firebaseRef.child("events").child(eventID).child("users");
                    pct.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(uid.equalsIgnoreCase("friend"))
                            {
                                String[] splitted = name.split("'");
                                for (DataSnapshot data : dataSnapshot.getChildren())
                                    if (data.getKey().contains(splitted[0])) {
                                        User.firebaseRef.child("events").child(eventID).child("users").child(data.getKey()).removeValue();
                                        finish();
                                        startActivity(getIntent());
                                        return;
                                    }
                            }
                            else {
                                for (DataSnapshot data : dataSnapshot.getChildren())
                                    if (data.getKey().equalsIgnoreCase(uid)) {
                                        User.firebaseRef.child("events").child(eventID).child("users").child(data.getKey()).removeValue();
                                        User.firebaseRef.child("users").child(uid).child("events").child(eventID).child("participation").setValue(false);
                                        finish();
                                        startActivity(getIntent());
                                        return;
                                    }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError firebaseError) {

                        }
                    });
                }
            }).show();
    }

    public void initializeLeaveEventButton (final DataSnapshot details, final String eventUid) {

        header_button = (ImageButton) findViewById(R.id.header_button);
        header_button.setVisibility(View.VISIBLE);
        header_button.setBackgroundResource(R.drawable.remove_event_button);
        header_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(EventDetails.this, R.style.MyAlertDialogStyle);
                builder.setTitle("Leave Event")
                        .setMessage("Are you sure you want to leave the event?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNegativeButton("NO", null)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Map<String,Object> users = new HashMap<>();
                                boolean isAnotherUser = false;
                                for(DataSnapshot data : details.getChildren())
                                {
                                    if (!data.getKey().equalsIgnoreCase(User.uid)) {
                                        users.put(data.getKey().toString(), Boolean.parseBoolean(data.getValue().toString()));

                                        if(!isAnotherUser){

                                            if(data.getKey().toString().startsWith("+")){
                                                continue;
                                            }


                                            User.firebaseRef.child("events").child(eventUid).child("createdBy").setValue(data.getKey().toString());

                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(data.getKey().toString());
                                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    for (DataSnapshot data : snapshot.getChildren()) {
                                                        if (data.getKey().equalsIgnoreCase("name")) {
                                                            User.firebaseRef.child("events").child(eventUid).child("creatorName").setValue(data.getValue().toString());
                                                        }
                                                        if (data.getKey().equalsIgnoreCase("profileImageURL")) {
                                                            User.firebaseRef.child("events").child(eventUid).child("creatorImage").setValue(data.getValue().toString());                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError firebaseError) {
                                                }
                                            });



                                            isAnotherUser = true;
                                        }
                                    }
                                }

                                if(!isAnotherUser){
                                    // if there is no user left, cancel event
                                    User.firebaseRef.child("events").child(eventUid).child("active").setValue(false);
                                }
                                    User.firebaseRef.child("events").child(eventUid).child("users").setValue(users);

                                header_button.setVisibility(View.INVISIBLE);
                                finish();

                            }
                        }).show();
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
            EventDetails.numberOfFriendsAddded = 0;
        }

        public void run() {
            Iterator it = users.entrySet().iterator();
            int index = 0;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                final String user_uid = pair.getKey().toString();
                boolean added = false;
                if ( user_uid.equalsIgnoreCase(User.uid) )
                    EventDetails.doIparticipate = true;
                if(user_uid.charAt(0) == '+') {
                    String user = user_uid.substring(1);
                    int i = 0;
                    while (Character.isDigit(user.charAt(i))) i++;
                    user = user.substring(i);
                    String photoURL = "http://media3.oakpark.com/Images/2/2/36431/2/1/2_2_36431_2_1_690x520.jpg";
                    usersList.add(new UserInfo(user+ "'s friend", photoURL, "0", index++ , "friend"));
                    added = true;
                }
                if(creatorID.equalsIgnoreCase(user_uid)){
                    continue;
                }
                if (!added)
                    usersList.add(new UserInfo("","creatorID","", index++, user_uid));
            }

            for(int i = 0 ; i  < index ; ++i){

                DatabaseReference pct = User.firebaseRef.child("points").child(sport).child(usersList.get(i).uid);
                final int ind = i;
                pct.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usersList.get(ind).points =
                                dataSnapshot.getValue()==null?
                                        "0 points - Beginner":
                                        dataSnapshot.getValue() + " points - Grand Master";
                        synchronized (syncr){
                            val ++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });

                DatabaseReference usr = User.firebaseRef.child("users").child(usersList.get(i).uid);
                usr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for ( DataSnapshot data : snapshot.getChildren() ) {

                            if( (data.getKey()).compareTo("firstName") == 0) {
                                usersList.get(ind).name = data.getValue().toString();
                            }
                            if( (data.getKey()).compareTo("profileImageURL") == 0 ) {
                                usersList.get(ind).photo = data.getValue().toString();
                            }
                        }

                        synchronized (syncr){
                            val ++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

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
                    final FriendsListAdapter adpt = new FriendsListAdapter(usersList, context, instance);
                    NonScrollListView lv = (NonScrollListView) findViewById(R.id.listViewUsers);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?>adapter,View v, int position, long id){
                            UserInfo userInfo = (UserInfo)adpt.getItem(position);
                            if(userInfo.uid.equalsIgnoreCase("friend"))
                                Toast.makeText(getApplicationContext(),"This user does not have an account in Ludicon. Yet!",Toast.LENGTH_SHORT).show();
                            else
                            {
                                if( userInfo.uid.equalsIgnoreCase(User.uid))
                                {
                                    Toast.makeText(getApplicationContext(),"It's just you! :)",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                    intent.putExtra("uid", userInfo.uid);
                                    startActivity(intent);
                                }
                            }
                        }
                    });
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
