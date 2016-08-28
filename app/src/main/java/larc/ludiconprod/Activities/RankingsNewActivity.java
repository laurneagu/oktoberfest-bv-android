package larc.ludiconprod.Activities;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.lang.reflect.Type;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import larc.ludiconprod.Adapters.LeftPanelItemClicker;
import larc.ludiconprod.Adapters.LeftSidePanelAdapter;
import larc.ludiconprod.R;
import larc.ludiconprod.Services.FriendlyService;
import larc.ludiconprod.UserInfo.ActivityInfo;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.Location.GPSTracker;
import larc.ludiconprod.Utils.MainPageUtils.ViewPagerAdapter;
import larc.ludiconprod.Utils.UserInRanks;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;
import larc.ludiconprod.Utils.util.DateManager;
import larc.ludiconprod.Utils.util.Utils;

/**
 * Created by LaurUser on 8/27/2016.
 */

public class RankingsNewActivity extends AppCompatActivity {

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog dialog;
    private int TIMEOUT = 80;

    /* SlideTab */
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Friends","Local"};
    int Numboftabs =2;
    boolean addedSwipe = false;
    final ArrayList<String> favoriteSports = new ArrayList<>();
    private final ArrayList<UserInRanks> usersLocalList = new ArrayList<>();
    private TextView headerMessage;

    public RankingsNewActivity(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);

            getSupportActionBar().hide();
            setContentView(R.layout.activity_rankings);


            // Set text message in header
            headerMessage = (TextView)findViewById(R.id.hello_message_activity);
            headerMessage.setText("FOOTBALL");

            // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
            adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

            // Assigning ViewPager View and setting the adapter
            pager = (ViewPager) findViewById(R.id.pager);
            pager.setAdapter(adapter);

            // Assiging the Sliding Tab Layout View
            tabs = (SlidingTabLayout) findViewById(R.id.tabs);
            tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(pager);
            /**************/

            dialog = ProgressDialog.show(RankingsNewActivity.this, "", "Loading. Please wait", true);

            // Add functionality on tapping sport icons
            //// ------------------------------------------------------------  //////////////////
            ImageButton imButt = (ImageButton)findViewById(R.id.footballRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "football";
                    usersLocalList.clear();
                    headerMessage.setText("FOOTBALL");
                    updateList();
                }
            });

            imButt = (ImageButton)findViewById(R.id.volleyRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "volleyball";
                    usersLocalList.clear();
                    headerMessage.setText("VOLLEY");
                    updateList();
                }
            });

            imButt = (ImageButton)findViewById(R.id.basketballRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "basketball";
                    usersLocalList.clear();
                    headerMessage.setText("BASKETBALL");
                    updateList();
                }
            });

            imButt = (ImageButton)findViewById(R.id.squashRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "squash";
                    usersLocalList.clear();
                    headerMessage.setText("SQUASH");
                    updateList();
                }
            });

            imButt = (ImageButton)findViewById(R.id.pingpongRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "pingpong";
                    usersLocalList.clear();
                    headerMessage.setText("PING-PONG");
                    updateList();
                }
            });

            imButt = (ImageButton)findViewById(R.id.tennisRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "tennis";
                    usersLocalList.clear();
                    headerMessage.setText("TENNIS");
                    updateList();
                }
            });

            imButt = (ImageButton)findViewById(R.id.cyclingRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "cycling";
                    usersLocalList.clear();
                    headerMessage.setText("CYCLING");
                    updateList();
                }
            });

            imButt = (ImageButton)findViewById(R.id.joggingRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "jogging";
                    usersLocalList.clear();
                    headerMessage.setText("JOGGING");
                    updateList();
                }
            });

            imButt = (ImageButton)findViewById(R.id.gymRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "gym";
                    usersLocalList.clear();
                    headerMessage.setText("GYM");
                    updateList();
                }
            });

            imButt = (ImageButton)findViewById(R.id.otherRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "other";
                    usersLocalList.clear();
                    headerMessage.setText("OTHER");
                    updateList();
                }
            });

            //// ------------------------------------------------------------  ///////////////////
            continueUpdatingTimeline();

        }
        catch(Exception exc) {
            Utils.quit();
        }

    }
    public void continueUpdatingTimeline() {
        try {

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

            userPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    RankingsNewActivity.this.startActivity(mainIntent);
                }
            });
            // -------------------------------------------------------------------------------------------------------------

    /*
        final SwipeRefreshLayout mSwipeRefreshLayout1 = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh1);
        mSwipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
                mSwipeRefreshLayout1.setRefreshing(false);
            }
        });
*/
        /*
        final SwipeRefreshLayout mSwipeRefreshLayout2 = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh2);
        mSwipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
                mSwipeRefreshLayout2.setRefreshing(false);
            }
        });
        */


            updateList();

        } catch (Exception exc) {
            Utils.quit();
        }

    }

    public String selectedSport = "football";

    public void updateList()
    {
        usersLocalList.clear();

        // Event lists:
        DatabaseReference userRef = User.firebaseRef.child("points").child(selectedSport); // check points
        Query myTopUsers = userRef.orderByValue();
        myTopUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    final UserInRanks userInRanks = new UserInRanks(data.getKey());
                    userInRanks.points = Integer.parseInt(data.getValue().toString());

                    // Get user picture and user name
                    DatabaseReference profileRef = User.firebaseRef.child("users").child(data.getKey());
                    profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                if (data.getKey().equalsIgnoreCase("name"))
                                    userInRanks.name = data.getValue().toString();
                                if (data.getKey().equalsIgnoreCase("profileImageURL")) {
                                    userInRanks.profileImageURL = data.getValue().toString();

                                    // Temporary solution to order the result, as for now Query is not working properly
                                    int i = 0;
                                    while(usersLocalList.size()>i && userInRanks.points < usersLocalList.get(i).points){
                                        i++;
                                    }
                                    usersLocalList.add(i,userInRanks);

                                    if (localAdapter != null) localAdapter.notifyDataSetChanged();

                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                        }
                    });
                }

                /*
                // Sort by date
                Collections.sort(friendsEventsList, new Comparator<Event>() {
                    @Override
                    public int compare(Event lhs, Event rhs) {
                        return lhs.date.compareTo(rhs.date);
                    }
                });
                // Sort by date
                Collections.sort(myEventsList, new Comparator<Event>() {
                    @Override
                    public int compare(Event lhs, Event rhs) {
                        return lhs.date.compareTo(rhs.date);
                    }
                });
                */

                /* Local Ranking */
                localAdapter = new UsersLocalAdapter(usersLocalList, getApplicationContext());
                ListView frlistView = (ListView) findViewById(R.id.events_listView1);
                if (frlistView != null) {
                    frlistView.setAdapter(localAdapter);
                }

                /* Friends */
                //TimelineMyActAdapter myadapter = new TimelineMyActAdapter(usersLocalList, getApplicationContext());
                ListView mylistView = (ListView) findViewById(R.id.events_listView2);
                if (mylistView != null) {
                    mylistView.setAdapter(localAdapter);
                }

                /*Swipe */
                if (!addedSwipe) {
                    final SwipeRefreshLayout mSwipeRefreshLayout1 = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh1);
                    mSwipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            updateList();
                            mSwipeRefreshLayout1.setRefreshing(false);
                        }
                    });
                    addedSwipe = true;
                }


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
                timer.schedule(delayedThreadStartTask, TIMEOUT * 2 * usersLocalList.size());

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

    private  UsersLocalAdapter localAdapter;
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


    @Override
    public void onStop(){
        super.onStop();
  }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    // Left side menu
    public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, RankingsNewActivity.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), RankingsNewActivity.this);

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

    // Adapter for the Around activities tab
    public class UsersLocalAdapter extends BaseAdapter implements ListAdapter {

        class ViewHolder {
            TextView name;
            TextView points;
            ImageView profilePicture;
            ImageView place;
            TextView placeText;
        };
        private ArrayList<UserInRanks> list = new ArrayList<>();
        private Context context;

        public UsersLocalAdapter(ArrayList<UserInRanks> list, Context context) {
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
            ViewHolder holder = null;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.user_ranks, null);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "View profile ?!", Toast.LENGTH_SHORT).show();
                    }
                });

                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.nameUserRankTV);
                holder.profilePicture = (ImageView) view.findViewById(R.id.profilePictureRanks);
                holder.place = (ImageView) view.findViewById(R.id.placeIV);
                holder.placeText = (TextView) view.findViewById(R.id.placeTextIV);
                holder.points = (TextView) view.findViewById(R.id.pointsRankTV);

                view.setTag(holder);
            }
            else {
                holder = (ViewHolder)view.getTag();
            }

            // Gold medal
            if (position == 0){

            }
            // Silver
            else if(position == 1){

            }
            // Bronze
            else if(position == 2){

            }
            // Other places
            else {
                holder.place.setVisibility(View.INVISIBLE);
                holder.placeText.setVisibility(View.VISIBLE);
                holder.placeText.setText("#" + (position+1));
            }

            holder.name.setText(list.get(position).name);
            Picasso.with(context).load(list.get(position).profileImageURL).into( holder.profilePicture);

            holder.points.setText(list.get(position).points + " points");

            return view;
        }
    }


    public static int getDayOfMonth(Date aDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    // Adapter for the My pending activities tab
    public class TimelineMyActAdapter extends BaseAdapter implements ListAdapter {

        class ViewHolder{
            TextView name ;
            ImageView profilePicture;
            TextView firstPart;
            TextView secondPart;
            TextView time;
            TextView place;
            ImageView icon;
            ImageButton details;
            TextView description;
            TextView players;
        }

        private ArrayList<Event> list = new ArrayList<>();
        private Context context;
        final ListView listView = (ListView) findViewById(R.id.events_listView2);

        public TimelineMyActAdapter(ArrayList<Event> list, Context context) {
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
            ViewHolder holder = null;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.timeline_list_myactivities_layout, null);

                view.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //Toast.makeText(getApplicationContext(), "Hei, wait for it..", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), EventDetails.class);
                                                intent.putExtra("eventUid", list.get(position).id);
                                                startActivity(intent);
                                            }
                                        }
                );
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.nameLabel);
                holder.profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
                holder.firstPart = (TextView) view.findViewById(R.id.firstPartofText);
                holder.secondPart = (TextView) view.findViewById(R.id.secondPartofText);
                holder.time = (TextView) view.findViewById(R.id.timeText);
                holder.place = (TextView) view.findViewById(R.id.placeText);
                holder.icon = (ImageView) view.findViewById(R.id.sportIcon);
                holder.description = (TextView) view.findViewById(R.id.descriptionID);
                holder.players = (TextView) view.findViewById(R.id.playersID);

                view.setTag(holder);
            }

            else {
                holder = (ViewHolder)view.getTag();
            }
            /*
            final TextView name = (TextView) view.findViewById(R.id.nameLabel);
            final ImageView profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
            final TextView firstPart = (TextView) view.findViewById(R.id.firstPartofText);
            final TextView secondPart = (TextView) view.findViewById(R.id.secondPartofText);
            final TextView time = (TextView) view.findViewById(R.id.timeText);
            final TextView place = (TextView) view.findViewById(R.id.placeText);
            final ImageView icon = (ImageView) view.findViewById(R.id.sportIcon);
            final ImageButton details = (ImageButton) view.findViewById(R.id.details_btn);
            */

            // Set name and picture for the first user of the event
            //final String userUID = list.get(position).getFirstUser();
            String firstName = list.get(position).creatorName.split(" ")[0];
            holder.name.setText(firstName);
            Picasso.with(context).load(list.get(position).profileImageURL).into(holder.profilePicture);

            // Redirect to user profile on picture click
            holder.profilePicture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (User.uid.equals(list.get(position).creator)) {
                        Toast.makeText(context, "This is you ! We can't compare with yourself..", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("uid", list.get(position).getFirstUser());
                        startActivity(intent);
                    }
                }
            });

            /*
            DatabaseReference userRef = User.firebaseRef.child("users").child(userUID);
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
                public void onCancelled(DatabaseError firebaseError) {
                }
            });*/

            String uri = "@drawable/" + list.get(position).sport.toLowerCase().replace(" ", "");

            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);

            holder.icon.setImageDrawable(res);
            holder.firstPart.setText("Will play " + list.get(position).sport);
            if ((list.get(position).noUsers - 1) > 1) {
                holder.secondPart.setText(" with " + (list.get(position).noUsers - 1) + " others");
            } else if ((list.get(position).noUsers - 1) == 1) {
                holder.secondPart.setText(" with 1 other");
            } else {
                holder.secondPart.setText(" with no others");
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
            if(list.get(position).description.equalsIgnoreCase(""))
                holder.description.setText("no description");
            else
                holder.description.setText("\"" + list.get(position).description + "\"");

            holder.players.setText( list.get(position).noUsers + "/" + list.get(position).roomCapacity);

            if(list.get(position) != null ) {
                if(list.get(position).isOfficial==0){
                    holder.place.setTextColor(Color.DKGRAY);
                }
                holder.place.setText(list.get(position).place);
            }
            else
                holder.place.setText("Unknown");

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
            holder.time.setText(day + " at " + hour);
            /*details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Hei, wait for it..", Toast.LENGTH_SHORT).show();
                }
            });
            */

            /*
            try{
                Thread.sleep(50,1);
            }
            catch(InterruptedException exc ) {}
            */

            return view;
        }
    }
}

