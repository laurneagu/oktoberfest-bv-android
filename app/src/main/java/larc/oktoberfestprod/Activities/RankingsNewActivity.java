/*
package larc.ludiconprod.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.MainPageUtils.ViewPagerAdapter;
import larc.ludiconprod.Utils.UserInRanks;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;
import larc.ludiconprod.Utils.util.Utils;

*/
/**
 * Created by LaurUser on 8/27/2016.
 *//*


public class RankingsNewActivity extends Fragment {

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog dialog;
    private int TIMEOUT = 80;

    */
/* SlideTab *//*

    Toolbar toolbar;
    ViewPager pager;
    EditViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Friends", "Local"};
    int Numboftabs = 2;
    boolean addedSwipe = false;
    boolean addedSwipe2 = false;
    final ArrayList<String> favoriteSports = new ArrayList<>();
    private final ArrayList<UserInRanks> usersLocalList = new ArrayList<>();
    private final ArrayList<UserInRanks> usersFriendsList = new ArrayList<>();
    private TextView headerMessage;
    private View v;

    public RankingsNewActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.activity_rankings, container,false);

        try {
            super.onCreate(savedInstanceState);

            // Hide App bar
            // If the Android version is lower than Jellybean, use this call to hide
            // the status bar.
            if (Build.VERSION.SDK_INT < 16) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            if (android.os.Build.VERSION.SDK_INT >= 11) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }

            // Set text message in header
            headerMessage = (TextView) v.findViewById(R.id.hello_message_activity);
            headerMessage.setText("FOOTBALL");

            // Creating The EditViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
            adapter = new EditViewPagerAdapter(getActivity().getSupportFragmentManager(), Titles, Numboftabs);

            // Assigning ViewPager View and setting the adapter
            pager = (ViewPager) v.findViewById(R.id.pager);
            pager.setAdapter(adapter);

            // Assiging the Sliding Tab Layout View
            tabs = (SlidingTabLayout) v.findViewById(R.id.tabs);
            tabs.setDistributeEvenly(false); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(pager);
            */
/**************//*


            dialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait", true);

            // Add functionality on tapping sport icons
            //// ------------------------------------------------------------  //////////////////
            ImageButton imButt = (ImageButton) v.findViewById(R.id.footballRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "football";
                    usersLocalList.clear();
                    headerMessage.setText("FOOTBALL");
                    updateList();
                }
            });

            imButt = (ImageButton) v.findViewById(R.id.volleyRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "volleyball";
                    usersLocalList.clear();
                    headerMessage.setText("VOLLEY");
                    updateList();
                }
            });

            imButt = (ImageButton) v.findViewById(R.id.basketballRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "basketball";
                    usersLocalList.clear();
                    headerMessage.setText("BASKETBALL");
                    updateList();
                }
            });

            imButt = (ImageButton) v.findViewById(R.id.squashRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "squash";
                    usersLocalList.clear();
                    headerMessage.setText("SQUASH");
                    updateList();
                }
            });

            imButt = (ImageButton) v.findViewById(R.id.pingpongRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "pingpong";
                    usersLocalList.clear();
                    headerMessage.setText("PING PONG");
                    updateList();
                }
            });

            imButt = (ImageButton) v.findViewById(R.id.tennisRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "tennis";
                    usersLocalList.clear();
                    headerMessage.setText("TENNIS");
                    updateList();
                }
            });

            imButt = (ImageButton) v.findViewById(R.id.cyclingRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "cycling";
                    usersLocalList.clear();
                    headerMessage.setText("CYCLING");
                    updateList();
                }
            });

            imButt = (ImageButton) v.findViewById(R.id.joggingRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "jogging";
                    usersLocalList.clear();
                    headerMessage.setText("JOGGING");
                    updateList();
                }
            });

            imButt = (ImageButton) v.findViewById(R.id.gymRankIB);
            imButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSport = "gym";
                    usersLocalList.clear();
                    headerMessage.setText("GYM");
                    updateList();
                }
            });

            imButt = (ImageButton) v.findViewById(R.id.otherRankIB);
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
        finally {
            return v;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (localAdapter != null)
            localAdapter.notifyDataSetChanged();

        if (friendsAdapter != null)
            friendsAdapter.notifyDataSetChanged();
    }

    public void continueUpdatingTimeline() {
        try {
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
                                    */
/*
                                    while (usersLocalList.size() > i && userInRanks.points < usersLocalList.get(i).points) {
                                        i++;
                                    }
                                    *//*


                                    usersLocalList.add(i, userInRanks);
                                    //if (localAdapter != null) localAdapter.notifyDataSetChanged();

                                    // it's me, man -- add me to the friends list too
                                    if (data.getKey().equalsIgnoreCase(User.uid)){
                                            int j = 0;
                                            //while (usersFriendsList.size() > j && userInRanks.points < usersFriendsList.get(j).points) {
                                            //    j++;
                                            //}
                                            usersFriendsList.add(j, userInRanks);


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

                //if (localAdapter != null)localAdapter.notifyDataSetChanged();
                //if (friendsAdapter != null) friendsAdapter.notifyDataSetChanged();

                */
/* Local Ranking *//*

                localAdapter = new UsersLocalAdapter(usersLocalList, getActivity());
                ListView frlistView = (ListView) v.findViewById(R.id.events_listView1);
                if (frlistView != null) {
                    frlistView.setAdapter(localAdapter);
                }

                */
/*Swipe *//*

                if (!addedSwipe) {
                    final SwipeRefreshLayout mSwipeRefreshLayout1 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh1);
                    mSwipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            updateList();
                            mSwipeRefreshLayout1.setRefreshing(false);
                        }
                    });
                    addedSwipe = true;
                }

                 */
/*Swipe *//*

                if (!addedSwipe2) {
                    final SwipeRefreshLayout mSwipeRefreshLayout2 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh2);
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

                timer.schedule(delayedThreadStartTask, TIMEOUT * 6);

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
                                                        */
/*while (usersFriendsList.size() > i && user.points < usersFriendsList.get(i).points) {
                                                            i++;
                                                        }*//*


                                                        usersFriendsList.add(i,user);

                                                        //if (friendsAdapter != null) friendsAdapter.notifyDataSetChanged();

                                                        break;
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        if (friendsAdapter != null)
                                            friendsAdapter.notifyDataSetChanged();

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



                */
/* Friends *//*

                friendsAdapter = new UsersLocalAdapter(usersFriendsList, getActivity());
                ListView mylistView = (ListView) v.findViewById(R.id.events_listView2);
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
*/
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    *//*


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

                final View currView = view;

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currView.setBackgroundColor(Color.parseColor("#C3DC6E"));
                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra("uid", list.get(position).id);
                        startActivity(intent);
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

            view.setBackgroundColor(Color.parseColor("#FFFFFF"));

            if (list.get(position).id.equalsIgnoreCase(User.uid)){
                holder.rl_ranks.setBackgroundColor(Color.LTGRAY);
            }
            else{
                holder.rl_ranks.setBackgroundColor(Color.TRANSPARENT);
            }

            */
/*
            // Gold medal
            if (position == 0) {
                holder.place.setBackgroundResource(R.drawable.medal1);
            }
            // Silver
            else if (position == 1) {
                holder.place.setBackgroundResource(R.drawable.medal2);
            }
            // Bronze
            else if (position == 2) {
               holder.place.setBackgroundResource(R.drawable.medal3);
            }
            // Other places
            else {
                    holder.place.setBackgroundResource(R.drawable.medal4);
                }
            *//*


            if (position >= 9){

                    int dpValue = 20; // margin in dips
                    float d = context.getResources().getDisplayMetrics().density;

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)holder.placeText.getLayoutParams();
                    layoutParams.leftMargin = (int)(dpValue * d);
                    holder.placeText.setLayoutParams(layoutParams);
            }

            holder.placeText.setText("" + (position + 1 ));
            holder.placeText.setVisibility(View.VISIBLE);

            holder.name.setText(list.get(position).name);

            holder.profilePicture.setBackgroundResource(R.drawable.defaultpicture);
            Picasso.with(context).load(list.get(position).profileImageURL).into(holder.profilePicture);

            holder.points.setText(list.get(position).points + "");

            return view;
        }
    }
}

*/
