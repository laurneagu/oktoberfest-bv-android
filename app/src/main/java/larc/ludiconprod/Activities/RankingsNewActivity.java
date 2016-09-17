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
    CharSequence Titles[] = {"Friends", "Local"};
    int Numboftabs = 2;
    boolean addedSwipe = false;
    boolean addedSwipe2 = false;
    final ArrayList<String> favoriteSports = new ArrayList<>();
    private final ArrayList<UserInRanks> usersLocalList = new ArrayList<>();
    private final ArrayList<UserInRanks> usersFriendsList = new ArrayList<>();
    private TextView headerMessage;

    public RankingsNewActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            getSupportActionBar().hide();
            setContentView(R.layout.activity_rankings);


            // Set text message in header
            headerMessage = (TextView) findViewById(R.id.hello_message_activity);
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
            ImageButton imButt = (ImageButton) findViewById(R.id.footballRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "football";
                    usersLocalList.clear();
                    headerMessage.setText("FOOTBALL");
                    updateList();
                }
            });

            imButt = (ImageButton) findViewById(R.id.volleyRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "volleyball";
                    usersLocalList.clear();
                    headerMessage.setText("VOLLEY");
                    updateList();
                }
            });

            imButt = (ImageButton) findViewById(R.id.basketballRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "basketball";
                    usersLocalList.clear();
                    headerMessage.setText("BASKETBALL");
                    updateList();
                }
            });

            imButt = (ImageButton) findViewById(R.id.squashRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "squash";
                    usersLocalList.clear();
                    headerMessage.setText("SQUASH");
                    updateList();
                }
            });

            imButt = (ImageButton) findViewById(R.id.pingpongRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "pingpong";
                    usersLocalList.clear();
                    headerMessage.setText("PING PONG");
                    updateList();
                }
            });

            imButt = (ImageButton) findViewById(R.id.tennisRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "tennis";
                    usersLocalList.clear();
                    headerMessage.setText("TENNIS");
                    updateList();
                }
            });

            imButt = (ImageButton) findViewById(R.id.cyclingRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "cycling";
                    usersLocalList.clear();
                    headerMessage.setText("CYCLING");
                    updateList();
                }
            });

            imButt = (ImageButton) findViewById(R.id.joggingRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "jogging";
                    usersLocalList.clear();
                    headerMessage.setText("JOGGING");
                    updateList();
                }
            });

            imButt = (ImageButton) findViewById(R.id.gymRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "gym";
                    usersLocalList.clear();
                    headerMessage.setText("GYM");
                    updateList();
                }
            });

            imButt = (ImageButton) findViewById(R.id.otherRankIB);
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

        } catch (Exception exc) {
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
            updateList();

        } catch (Exception exc) {
            Utils.quit();
        }

    }

    public String selectedSport = "football";

    public void updateList() {
        usersLocalList.clear();
        usersFriendsList.clear();

        // Points Local Users lists:
        DatabaseReference userRef = User.firebaseRef.child("points").child(selectedSport); // check points
        Query myTopUsers = userRef.orderByValue();
        myTopUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (final DataSnapshot data : snapshot.getChildren()) {
                    final UserInRanks userInRanks = new UserInRanks(data.getKey());
                    userInRanks.points = Integer.parseInt(data.getValue().toString());

                    // Get user picture and user name
                    DatabaseReference profileRef = User.firebaseRef.child("users").child(data.getKey());
                    profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot datachild : snapshot.getChildren()) {
                                if (datachild.getKey().equalsIgnoreCase("name"))
                                    userInRanks.name = datachild.getValue().toString();
                                if (datachild.getKey().equalsIgnoreCase("profileImageURL")) {
                                    userInRanks.profileImageURL = datachild.getValue().toString();

                                    // Temporary solution to order the result, as for now Query is not working properly
                                    int i = 0;
                                    while (usersLocalList.size() > i && userInRanks.points < usersLocalList.get(i).points) {
                                        i++;
                                    }

                                    usersLocalList.add(i, userInRanks);
                                    if (localAdapter != null) localAdapter.notifyDataSetChanged();

                                    // it's me, man -- add me to the friends list too
                                    if (data.getKey().equalsIgnoreCase(User.uid)){
                                            int j = 0;
                                            while (usersFriendsList.size() > j && userInRanks.points < usersFriendsList.get(j).points) {
                                                j++;
                                            }
                                            usersFriendsList.add(j, userInRanks);

                                            if (friendsAdapter != null) friendsAdapter.notifyDataSetChanged();
                                    }
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                        }
                    });
                }

                /* Local Ranking */
                localAdapter = new UsersLocalAdapter(usersLocalList, getApplicationContext());
                ListView frlistView = (ListView) findViewById(R.id.events_listView1);
                if (frlistView != null) {
                    frlistView.setAdapter(localAdapter);
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

                 /*Swipe */
                if (!addedSwipe2) {
                    final SwipeRefreshLayout mSwipeRefreshLayout2 = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh2);
                    mSwipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            updateList();
                            mSwipeRefreshLayout2.setRefreshing(false);
                        }
                    });
                    addedSwipe2 = true;
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


        // Points Friend Users lists:
        DatabaseReference userFriendsRef = User.firebaseRef.child("users").child(User.uid).child("friends"); // check points friends
        //Query myTopUserFriends = userFriendsRef.orderByValue();
        userFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (final DataSnapshot data : snapshot.getChildren()) {
                    // This is good we save it
                    if (data.getValue().toString().equalsIgnoreCase("true")){

                        DatabaseReference pointsRef = User.firebaseRef.child("points").child(selectedSport); // check points
                        pointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataPoints : dataSnapshot.getChildren()) {
                                    if (dataPoints.getKey().equalsIgnoreCase(data.getKey())) {
                                        final UserInRanks user = new UserInRanks(data.getKey());
                                        user.points = Integer.parseInt(dataPoints.getValue().toString());

                                        DatabaseReference userProfileFriendRef = User.firebaseRef.child("users").child(data.getKey()); // check points friends
                                        userProfileFriendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                                for (DataSnapshot dataFriendProfile : dataSnapshot2.getChildren()) {
                                                    if (dataFriendProfile.getKey().equalsIgnoreCase("name")) {
                                                        user.name = dataFriendProfile.getValue().toString();
                                                        continue;
                                                    }
                                                    if(dataFriendProfile.getKey().equalsIgnoreCase("profileImageURL")){
                                                        user.profileImageURL = dataFriendProfile.getValue().toString();
                                                        // Temporary solution to order the result
                                                        int i = 0;
                                                        while (usersFriendsList.size() > i && user.points < usersFriendsList.get(i).points) {
                                                            i++;
                                                        }

                                                        usersFriendsList.add(i,user);

                                                        if (friendsAdapter != null) friendsAdapter.notifyDataSetChanged();

                                                        break;
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        break; /// yey it has points
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }



                /* Friends */
                friendsAdapter = new UsersLocalAdapter(usersFriendsList, getApplicationContext());
                ListView mylistView = (ListView) findViewById(R.id.events_listView2);
                if (mylistView != null) {
                    mylistView.setAdapter(friendsAdapter);
                }

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });


    }

    private UsersLocalAdapter localAdapter;
    private UsersLocalAdapter friendsAdapter;

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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
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
            RelativeLayout rl_ranks;
        }

        ;
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
                holder.rl_ranks = (RelativeLayout)view.findViewById(R.id.bodyOfRanks);
                holder.name = (TextView) view.findViewById(R.id.nameUserRankTV);
                holder.profilePicture = (ImageView) view.findViewById(R.id.profilePictureRanks);
                holder.place = (ImageView) view.findViewById(R.id.placeIV);
                holder.placeText = (TextView) view.findViewById(R.id.placeTextIV);
                holder.points = (TextView) view.findViewById(R.id.pointsRankTV);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (list.get(position).id.equalsIgnoreCase(User.uid)){
                holder.rl_ranks.setBackgroundColor(Color.LTGRAY);
            }
            else{
                holder.rl_ranks.setBackgroundColor(Color.TRANSPARENT);
            }

            // Gold medal
            if (position == 0) {
                holder.placeText.setVisibility(View.INVISIBLE);
                holder.place.setImageResource(R.drawable.medal1);
            }
            // Silver
            else if (position == 1) {
                holder.placeText.setVisibility(View.INVISIBLE);
                holder.place.setImageResource(R.drawable.medal2);
            }
            // Bronze
            else if (position == 2) {
                holder.placeText.setVisibility(View.INVISIBLE);
                holder.place.setImageResource(R.drawable.medal3);
            }
            // Other places
            else {
                holder.place.setImageResource(R.drawable.medal4);
                //holder.place.setVisibility(View.INVISIBLE);
                holder.placeText.setVisibility(View.VISIBLE);
                holder.placeText.setText("" + (position + 1 ));
            }

            holder.name.setText(list.get(position).name);
            Picasso.with(context).load(list.get(position).profileImageURL).into(holder.profilePicture);

            holder.points.setText(list.get(position).points + " points");

            return view;
        }
    }
}

