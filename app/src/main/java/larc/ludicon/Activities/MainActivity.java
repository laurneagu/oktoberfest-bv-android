package larc.ludicon.Activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import android.app.PendingIntent;
import android.widget.ViewFlipper;


//import com.batch.android.Batch;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import larc.ludicon.Adapters.LeftPanelItemClicker;
import larc.ludicon.Adapters.LeftSidePanelAdapter;
import larc.ludicon.R;
import larc.ludicon.UserInfo.ActivityInfo;
import larc.ludicon.UserInfo.User;
import larc.ludicon.Utils.util.AsyncBackgroundTask;
import larc.ludicon.Utils.util.BackgroundService;

public class MainActivity extends Activity {

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog dialog;
    private int TIMEOUT = 80;

    private ViewFlipper flipper;
    private int currentPage = 0; // 0 = friends, 1 = my

    class Event {
        Map<String, Boolean> usersUID = new HashMap<String,Boolean>();
        Date date;
        int noUsers;
        String sport;
        String creator;
        String place;
        double latitude;
        double longitude;
        String id;
        String creatorName;
        String profileImageURL;
        public String getFirstUser() {
            return creator;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /* Batch.onStart(this);

        Batch.User.getEditor()
                .setIdentifier(User.uid)
                .save(); // Don't forget to save the changes!
        */

        setContentView(R.layout.activity_main);
        flipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        flipper.setInAnimation(this, R.anim.right_enter);
        flipper.setOutAnimation(this, R.anim.left_out);

        addFriendsActivityButtonEventListener();
        addMyActivityButtonEventListener();

        final Locale locale = Locale.getDefault();

        dialog = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait", true);

        // Background Service:
        Intent mServiceIntent = new Intent(this, BackgroundService.class);
        startService(mServiceIntent);

        //Clean up shared pref for events: just for debugging
        SharedPreferences.Editor editor = getSharedPreferences("UserDetails", 0).edit();
        String connectionsJSONString = new Gson().toJson(null);
        editor.putString("events", connectionsJSONString);
        editor.commit();

        /*
        // Update sharedpref for events:
        Firebase usersRef = User.firebaseRef.child("users").child(User.uid).child("events");
        usersRef.addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(DataSnapshot snapshot) {
                                               final List<ActivityInfo> activityInfos = new ArrayList<ActivityInfo>();

                                               if (snapshot == null)
                                                   Log.v("NULL", "Snapshot e null");
                                               for (DataSnapshot data : snapshot.getChildren()) {


                                                   if ((Boolean) data.getValue() == true) {
                                                       Firebase eventRef = User.firebaseRef.child("events").child(data.getKey().toString());
                                                       eventRef.addListenerForSingleValueEvent(new ValueEventListener() {

                                                           @Override
                                                           public void onDataChange(DataSnapshot dataSnapshot) {
                                                               ActivityInfo ai = new ActivityInfo();

                                                               for (DataSnapshot details : dataSnapshot.getChildren()) {

                                                                   if (details.getKey().toString().equalsIgnoreCase("users")) {
                                                                       int count = 0;

                                                                       for (DataSnapshot user : details.getChildren()) {
                                                                           count++;
                                                                       }
                                                                       ai.others = count;

                                                                   }
                                                                   if (details.getKey().toString().equalsIgnoreCase("date")) {
                                                                       ai.date = new Date(details.getValue().toString());
                                                                   }
                                                                   if (details.getKey().toString().equalsIgnoreCase("sport")) {
                                                                       ai.sport = details.getValue().toString();
                                                                   }
                                                                   if (details.getKey().toString().equalsIgnoreCase("place")) {
                                                                       // TODO get nown zone
                                                                       ai.place = "Herastrau";
                                                                   }
                                                               }

                                                               String connectionsJSONString = getSharedPreferences("UserDetails", 0).getString("events", null);
                                                               Type type = new TypeToken< List <ActivityInfo>>() {}.getType();
                                                               List < ActivityInfo > events = null;
                                                               if(connectionsJSONString != null) {
                                                                   events = new Gson().fromJson(connectionsJSONString, type);
                                                               }
                                                               if(events == null){
                                                                   events = new ArrayList<ActivityInfo>();
                                                                   events.add(ai);
                                                               }
                                                               else{
                                                                   Boolean exist = false;
                                                                   for(ActivityInfo act : events){
                                                                       if(ai.date.compareTo(act.date) == 0){
                                                                           exist = true;
                                                                       }
                                                                   }
                                                                   if(!exist) {
                                                                       events.add(ai);
                                                                   }
                                                               }

                                                               SharedPreferences.Editor editor = getSharedPreferences("UserDetails", 0).edit();
                                                               connectionsJSONString = new Gson().toJson(events);
                                                               editor.putString("events", connectionsJSONString);
                                                               editor.commit();

                                                           }

                                                           @Override
                                                           public void onCancelled(FirebaseError firebaseError) {

                                                           }
                                                       });
                                                   }
                                               }

                                           }

                                           @Override
                                           public void onCancelled(FirebaseError firebaseError) {
                                           }
                                       });

        */


        // Left side panel
        mDrawerList = (ListView) findViewById(R.id.leftMenu);
        initializeLeftSidePanel();

        User.setImage();

        // User picture and name for HEADER MENU
        TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText(User.getFirstName(getApplicationContext()) + " " + User.getLastName(getApplicationContext()));

        ImageView userPic = (ImageView) findViewById(R.id.userPicture);
        Drawable d = new BitmapDrawable(getResources(), User.image);
        userPic.setImageDrawable(d);
        // -------------------------------------------------------------------------------------------------------------
        /*
        final ArrayList<Event> eventList = new ArrayList<>();
        MyCustomAdapter adapter = new MyCustomAdapter(eventList, getApplicationContext());
        ListView listView = (ListView) findViewById(R.id.events_listView);
        if (listView != null)
            listView.setAdapter(adapter);
        updateinBackground(listView, eventList);*/

        Firebase userRef = User.firebaseRef.child("events"); // check events
        userRef.addValueEventListener(new ValueEventListener() {
        @Override
            public void onDataChange(DataSnapshot snapshot) {
                final ArrayList<Event> eventList = new ArrayList<>();
                final ArrayList<Event> myEventsList = new ArrayList<>();
                final ArrayList<Event> friendsEventsList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Event event = new Event();
                    boolean isPublic = true;
                    boolean doIParticipate = false;
                    event.id = data.getKey();
                    Map<String, Boolean> participants = new HashMap<String, Boolean>();


                    for (DataSnapshot details : data.getChildren()) {

                        if (details.getKey().toString().equalsIgnoreCase("creatorName"))
                            event.creatorName = details.getValue().toString();

                        if (details.getKey().toString().equalsIgnoreCase("creatorImage"))
                            event.profileImageURL = details.getValue().toString();

                        if (details.getKey().toString().equalsIgnoreCase("privacy"))
                            if (details.getValue().toString().equalsIgnoreCase("private"))
                                isPublic = false;

                        if (details.getKey().toString().equalsIgnoreCase("sport"))
                            event.sport = details.getValue().toString();
                        if (details.getKey().equalsIgnoreCase("createdBy"))
                            event.creator = details.getValue().toString();

                        if (details.getKey().toString().equalsIgnoreCase("date"))
                            event.date = new Date(details.getValue().toString());

                        if (details.getKey().toString().equalsIgnoreCase("place")) {
                            Map<String, Object> position = (Map<String, Object>) details.getValue();
                            double latitude = (double) position.get("latitude");
                            double longitude = (double) position.get("longitude");
                            String addressName = (String) position.get("name");
                            event.place = addressName;
                            event.latitude = latitude;
                            event.longitude = longitude;
                        }

                        if (details.getKey().toString().equalsIgnoreCase("users")) {
                            for (DataSnapshot user : details.getChildren()) {
                                String userID = user.getKey().toString();
                                if (userID.equalsIgnoreCase(User.uid)) {
                                    // TODO check if I have accepted
                                    doIParticipate = true;
                                    participants.put(user.getKey().toString(), (Boolean) user.getValue());
                                    break;
                                } else {
                                    participants.put(user.getKey().toString(), (Boolean) user.getValue());
                                }
                            }
                        }
                    }


//                    if (doIParticipate == false && new Date().before(event.date) && isPublic) {
//                        event.usersUID = participants;
//                        eventList.add(event);
//                    }

                    // Insert event in the correct list
                    if (new Date().before(event.date) && isPublic) {

                        event.usersUID = participants;

                        if (doIParticipate) {
                            myEventsList.add(event);
                        } else {
                            friendsEventsList.add(event);
                        }

                    }
                }
                /* Friends */
                MyCustomAdapter fradapter = new MyCustomAdapter(friendsEventsList, getApplicationContext());
                ListView frlistView = (ListView) findViewById(R.id.events_listView1);
                if (frlistView != null)
                    frlistView.setAdapter(fradapter);

                /* My */
                MyCustomAdapter myadapter = new MyCustomAdapter(myEventsList, getApplicationContext());
                ListView mylistView = (ListView) findViewById(R.id.events_listView2);
                if (mylistView != null)
                    mylistView.setAdapter(myadapter);




            // Dismiss loading dialog after  2 * TIMEOUT * eventList.size() ms
                Timer timer = new Timer();
                TimerTask delayedThreadStartTask = new TimerTask() {
                    @Override
                    public void run() {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        }).start();
                     }
                };
                timer.schedule(delayedThreadStartTask, TIMEOUT * 2 * eventList.size());

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
    public void updateinBackground(final ListView listview, final ArrayList<Event> events)
    {
        Firebase userRef = User.firebaseRef.child("events"); // check events
        userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildMoved(DataSnapshot snapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {

                for (int i = 0; i < events.size(); i++) {
                    if ((events.get(i).id).compareTo(snapshot.getValue().toString()) == 0) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if (data.getKey().toString().equalsIgnoreCase("privacy"))

                                if (data.getKey().toString().equalsIgnoreCase("sport"))
                                    events.get(i).sport = data.getValue().toString();
                            if (data.getKey().equalsIgnoreCase("createdBy"))
                                events.get(i).creator = data.getValue().toString();
                            if (data.getKey().toString().equalsIgnoreCase("date"))
                                events.get(i).date = new Date(data.getValue().toString());
                            if (data.getKey().toString().equalsIgnoreCase("place")) {
                                Map<String, Object> position = (Map<String, Object>) data.getValue();
                                double latitude = (double) position.get("latitude");
                                double longitude = (double) position.get("longitude");
                                String addressName = (String) position.get("name");
                                events.get(i).place = addressName;
                                events.get(i).latitude = latitude;
                                events.get(i).longitude = longitude;
                            }
                            boolean doIParticipate = false;
                            if (data.getKey().toString().equalsIgnoreCase("users")) {
                                for (DataSnapshot user : data.getChildren()) {
                                    if (user != null) {
                                        if (events.get(i).usersUID.get(user.getKey().toString()) == null)
                                            events.get(i).usersUID.put(user.getKey().toString(), true);
                                    }
                                    String userID = user.getKey().toString();
                                    if (userID.equalsIgnoreCase(User.uid)) {
                                        // TODO check if I have accepted
                                        doIParticipate = true;
                                    }
                                    if (doIParticipate == true) events.remove(i);
                                }
                            }
                            //events.get(i).noUsers = events.get(i).usersUID.size();
                        }
                    }
                }
                listview.invalidateViews();
            }

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                Event auxEvent = new Event();
                boolean doIParticipate = false;
                auxEvent.id = snapshot.getKey();
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.getKey().toString().equalsIgnoreCase("sport"))
                        auxEvent.sport = data.getValue().toString();
                    if (data.getKey().equalsIgnoreCase("createdBy"))
                        auxEvent.creator = data.getValue().toString();
                    if (data.getKey().toString().equalsIgnoreCase("date"))
                        auxEvent.date = new Date(data.getValue().toString());
                    if (data.getKey().toString().equalsIgnoreCase("place")) {
                        Map<String, Object> position = (Map<String, Object>) data.getValue();
                        double latitude = (double) position.get("latitude");
                        double longitude = (double) position.get("longitude");
                        String addressName = (String) position.get("name");
                        auxEvent.place = addressName;
                        auxEvent.latitude = latitude;
                        auxEvent.longitude = longitude;
                    }
                    if (data.getKey().toString().equalsIgnoreCase("users")) {
                        for (DataSnapshot user : data.getChildren()) {
                            if (user != null) {
                                auxEvent.usersUID.put(user.getKey().toString(), true);
                            }
                            String userID = user.getKey().toString();
                            if (userID.equalsIgnoreCase(User.uid)) {
                                // TODO check if I have accepted
                                doIParticipate = true;
                            }
                        }
                    }

                }
                auxEvent.noUsers = auxEvent.usersUID.size();
                if (doIParticipate == false)
                    events.add(auxEvent);
                listview.invalidateViews();
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }

    public void addFriendsActivityButtonEventListener(){
        Button fr = (Button)findViewById(R.id.fractbutton);
        fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage != 0){
                    currentPage = 0;
                    flipper.setInAnimation(getApplicationContext(), R.anim.right_enter);
                    flipper.setOutAnimation(getApplicationContext(), R.anim.left_out);
                    flipper.showNext();
                }
            }
        });
    }

    public void addMyActivityButtonEventListener(){
        Button my = (Button)findViewById(R.id.myactbutton);
        my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage != 1){
                    currentPage = 1;
                    flipper.setInAnimation(getApplicationContext(), R.anim.left_enter);
                    flipper.setOutAnimation(getApplicationContext(), R.anim.right_out);
                    flipper.showPrevious();
                }
            }
        });
    }

    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {

        private ArrayList<Event> list = new ArrayList<>();
        private Context context;
        final ListView listView = (ListView) findViewById(R.id.events_listView1);

        public MyCustomAdapter(ArrayList<Event> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }
        @Override
        public long getItemId(int pos) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.timeline_list_layout, null);
            }
            final TextView name = (TextView) view.findViewById(R.id.nameLabel);
            final ImageView profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
            final TextView firstPart = (TextView) view.findViewById(R.id.firstPartofText);
            final TextView secondPart = (TextView) view.findViewById(R.id.secondPartofText);
            final TextView time = (TextView) view.findViewById(R.id.timeText);
            final TextView place = (TextView) view.findViewById(R.id.placeText);
            final ImageView icon = (ImageView) view.findViewById(R.id.sportIcon);
            final ImageButton details = (ImageButton) view.findViewById(R.id.details_btn);
            final ImageButton join = (ImageButton) view.findViewById(R.id.join_btn);

            // Set name and picture for the first user of the event
            //final String userUID = list.get(position).getFirstUser();

            name.setText(list.get(position).creatorName);
            Picasso.with(context).load(list.get(position).profileImageURL).into(profilePicture);


            /*
            Firebase userRef = User.firebaseRef.child("users").child(userUID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    for ( DataSnapshot data : snapshot.getChildren() ) {

                        if( (data.getKey()).compareTo("name") == 0) {
                            name.setText(data.getValue().toString());
                        }
                        if( (data.getKey()).compareTo("profileImageURL") == 0 )
                            new DownloadImageTask(profilePicture).execute(data.getValue().toString());
                    }
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });*/

            String uri = "@drawable/" + list.get(position).sport.toLowerCase().replace(" ", "");

            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);

            icon.setImageDrawable(res);
            firstPart.setText("Will play " + list.get(position).sport);
            if ((list.get(position).usersUID.size() - 1) > 1) {
                secondPart.setText(" with " + (list.get(position).usersUID.size() - 1) + " others");
            } else if ((list.get(position).usersUID.size() - 1) == 1) {
                secondPart.setText(" with 1 other");
            } else {
                secondPart.setText(" with no others");
            }
            /*
            firstPart.setText("Will play " + list.get(position).sport);
            if ((list.get(position).usersUID.size() - 1) > 1) {
                secondPart.setText(" with " + (list.get(position).noUsers) + " others");
            } else if (list.get(position).noUsers  == 1) {
                secondPart.setText(" with 1 other");
            } else {
                secondPart.setText(" with no others");
            }*/

            if(list.get(position) != null )
                place.setText(list.get(position).place);
            else
                place.setText("Unknown");
            Calendar c = Calendar.getInstance();
            Date today = c.getTime();
            int todayDay = getDayOfMonth(today);
            int todayMonth = today.getMonth();
            int todayYear = today.getYear();

            String day;
            if ( todayDay == getDayOfMonth(list.get(position).date) && todayMonth == list.get(position).date.getMonth() && todayYear == list.get(position).date.getYear() )
                        day = "Today";
            else if ( todayDay == ( getDayOfMonth(list.get(position).date) - 1 ) && todayMonth == list.get(position).date.getMonth() && todayYear == list.get(position).date.getYear() )
                        day = "Tomorrow";
                 else day = getDayOfMonth(list.get(position).date) + "/" + (list.get(position).date.getMonth()+1) + "/" + (list.get(position).date.getYear()+1900);
            String dateHour = list.get(position).date.getHours() + "";
            String dateMin = list.get(position).date.getMinutes()+ "";
            if(dateHour.equalsIgnoreCase("0")) dateHour += "0";
            if(dateMin.equalsIgnoreCase("0")) dateMin += "0";
            String hour = dateHour + ":" + dateMin;
            time.setText(day + " at " + hour);
            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Hei, wait for it..", Toast.LENGTH_SHORT).show();
                }
            });
            join.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Firebase usersRef = User.firebaseRef.child("events").child(list.get(position).id).child("users");
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Map<String, Object> map = new HashMap<>();
                            for (DataSnapshot data : snapshot.getChildren()) {

                                map.put(data.getKey(), data.getValue());
                            }
                            map.put(User.uid,true);
                            // NOTE: I need userRef to set the map value
                            Firebase userRef = User.firebaseRef.child("events").child(list.get(position).id).child("users");
                            userRef.updateChildren(map);

                            Map<String,Object> ev =  new HashMap<String,Object>();
                            ev.put(list.get(position).id, true);
                            list.remove(position);
                            User.firebaseRef.child("users").child(User.uid).child("events").updateChildren(ev);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                }
            });

            try{
                Thread.sleep(50,1);
            }
            catch(InterruptedException exc ) {}

            return view;
        }
    }
    public static int getDayOfMonth(Date aDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
    // Method which downloads Image from URL
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected synchronized Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Left side panel
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Left side menu
    public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, MainActivity.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), MainActivity.this);

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
}
