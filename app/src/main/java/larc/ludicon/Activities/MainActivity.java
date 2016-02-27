package larc.ludicon.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.batch.android.Batch;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import larc.ludicon.Adapters.LeftPanelItemClicker;
import larc.ludicon.Adapters.LeftSidePanelAdapter;
import larc.ludicon.R;
import larc.ludicon.UserInfo.User;
import larc.ludicon.Utils.util.AsyncBackgroundTask;
import larc.ludicon.Utils.util.BackgroundService;

public class MainActivity extends Activity {

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    class Event {
        Map<String, Boolean> usersUID = new HashMap<String,Boolean>();
        Date date;
        String sport;
        //Address place;
        double latitude;
        double longitude;
        String id;
        public String getFirstUser() {
            for(Map.Entry<String,Boolean> e : usersUID.entrySet()){
                if(e.getValue()){
                    return e.getKey();
                }
            }
            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Batch.onStart(this);

        Batch.User.getEditor()
                .setIdentifier(User.uid)
                .save(); // Don't forget to save the changes!

        setContentView(R.layout.activity_main);
        final Locale locale = Locale.getDefault();

        // Background Task:

        //AsyncBackgroundTask backgroundTask = new AsyncBackgroundTask(getApplicationContext());
        //backgroundTask.execute();

        // Background Service:
        ///Intent mServiceIntent = new Intent(this, BackgroundService.class);
        //mServiceIntent.setData(null);
        //this.startService(mServiceIntent);


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
        Firebase userRef = User.firebaseRef.child("events"); // check events
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Event> eventList = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren())
                {
                    Event event = new Event();
                    boolean isPublic = true;
                    boolean doIParticipate = false;
                    event.id = data.getKey();
                    Map<String, Boolean> participants = new HashMap<String, Boolean>();



                    for( DataSnapshot details : data.getChildren() ) {

                        if(details.getKey().toString().equalsIgnoreCase("privacy"))
                            if( details.getValue().toString().equalsIgnoreCase("private"))
                                isPublic = false;

                        if (details.getKey().toString().equalsIgnoreCase("sport"))
                            event.sport = details.getValue().toString();

                        if (details.getKey().toString().equalsIgnoreCase("date"))
                            event.date = new Date(details.getValue().toString());

                        if (details.getKey().toString().equalsIgnoreCase("place"))
                        {
                            Geocoder geocoder = new Geocoder(getApplicationContext(), locale);
                            Map<String,Double> position = ( Map<String,Double>) details.getValue();
                            double latitude = position.get("latitude");
                            double longitude = position.get("longitude");

                            Log.v("LAT-LONG",latitude + " " + longitude);
                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                if(addresses.size()>0) {
                                    Log.v("Address", addresses.get(0).toString());
                                    event.latitude = latitude;
                                    event.longitude = longitude;
                                            //addresses.get(0);
                                }
                            }
                            catch(IOException exc){}

                        }

                        if(details.getKey().toString().equalsIgnoreCase("users"))
                        {
                            for( DataSnapshot user : details.getChildren())
                            {
                                String userID = user.getKey().toString();
                                if( userID.equalsIgnoreCase(User.uid))
                                {
                                    // TODO check if I have accepted
                                    doIParticipate = true;
                                    participants.put(user.getKey().toString(), (Boolean) user.getValue());
                                    break;
                                }
                                else
                                {
                                    participants.put(user.getKey().toString(), (Boolean) user.getValue());
                                }
                            }
                        }
                    }



                    if( doIParticipate == false && new Date().before(event.date)  && isPublic )
                    {
                        event.usersUID = participants;
                        eventList.add(event);
                    }
                }
                //
                MyCustomAdapter adapter = new MyCustomAdapter(eventList,getApplicationContext());
                ListView listView= (ListView) findViewById(R.id.events_listView);
                if( listView != null)
                    listView.setAdapter(adapter);
            }
            @Override
             public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {

        private ArrayList<Event> list = new ArrayList<>();
        private Context context;
        final ListView listView = (ListView) findViewById(R.id.events_listView);

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
            final ImageButton details = (ImageButton) view.findViewById(R.id.details_btn);
            final ImageButton join = (ImageButton) view.findViewById(R.id.join_btn);

            // Set name and picture for the first user of the event
            final String userUID = list.get(position).getFirstUser();
            //Log.v("User",userUID);
            Firebase userRef = User.firebaseRef.child("users").child(userUID).child("name"); // check user
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    if (snapshot.getValue() != null) {
                        //Log.v("Name",snapshot.getValue().toString());
                        name.setText(snapshot.getValue().toString());
                        Firebase userRef = User.firebaseRef.child("users").child(userUID).child("profileImageURL");
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    new DownloadImageTask(profilePicture).execute(snapshot.getValue().toString());
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });

            firstPart.setText("Will play " + list.get(position).sport);
            secondPart.setText(" with " + ( list.get(position).usersUID.size()-1 ) + " others");
            if(list.get(position) != null )
                place.setText(String.valueOf(list.get(position).longitude) + " " +list.get(position).latitude);
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
                            if (snapshot == null) Log.v("NULL", "Snapshot e null");
                            for (DataSnapshot data : snapshot.getChildren()) {

                                map.put(data.getKey(), data.getValue());
                            }
                            map.put(User.uid,true);
                            // NOTE: I need userRef to set the map value
                            Firebase userRef = User.firebaseRef.child("events").child(list.get(position).id).child("users");
                            userRef.updateChildren(map);

                            Map<String,Object> ev =  new HashMap<String,Object>();
                            ev.put(list.get(position).id, true);
                            User.firebaseRef.child("users").child(User.uid).child("events").updateChildren(ev);


                            // TODO Reload Listview - Not working yet
                            //if(listView != null)
                            //    listView.invalidateViews();
                            //finish();
                            //startActivity(getIntent());
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                }
            });
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

        protected Bitmap doInBackground(String... urls) {
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
