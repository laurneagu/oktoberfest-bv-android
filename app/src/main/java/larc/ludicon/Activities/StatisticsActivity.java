package larc.ludicon.Activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import larc.ludicon.Adapters.LeftPanelItemClicker;
import larc.ludicon.Adapters.LeftSidePanelAdapter;
import larc.ludicon.R;
import larc.ludicon.UserInfo.ActivityInfo;
import larc.ludicon.UserInfo.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class StatisticsActivity extends Activity {

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    final int[] pointsPerMonth = new int[12];

    final ArrayList<EventStats> userEvents = new ArrayList<>();

    final int[] eventsPerSport = new int[8];
    final int[] pointsPerSport = new int[8];

    Map<String,Integer> sportsMap = new HashMap<String,Integer>();

    private class EventStats
    {
        String name,sport,id;
        int points;
        Date date;
    }

    private class SportStats
    {
        int id;
        int points;
        int events;
    }
    final ArrayList<Integer> dummy = new ArrayList<>();

    private void addSports()
    {
        sportsMap.put("football", 0);
        sportsMap.put("volley",1);
        sportsMap.put("basketball",2);
        sportsMap.put("chess",3);
        sportsMap.put("pingpong",4);
        sportsMap.put("tennis",5);
        sportsMap.put("cycling",6);
        sportsMap.put("jogging",7);
    }

    private void getDateAndSport()
    {
        Log.v("ArraySize",userEvents.size() + "");

        // If user has no events attended
        if ( userEvents.size() == 0 )
        {
            displayChart();
            displayStatistics();
        }
        else
        // For each event go to it's details (root->event->ID) and get it's date + sport
        for( int i = 0; i < userEvents.size(); i++ )
        {
            Firebase eventRef = User.firebaseRef.child("events").child(userEvents.get(i).id);
            final int j = i;
            eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        if (data.getKey().compareToIgnoreCase("sport") == 0) {
                            userEvents.get(j).sport = data.getValue().toString();
                            int sportID = sportsMap.get(data.getValue().toString());
                            if ( userEvents.get(j).points != 0 )
                            {
                                eventsPerSport[sportID] ++;
                                pointsPerSport[sportID] += userEvents.get(j).points;
                            }
                        }
                        if (data.getKey().compareToIgnoreCase("date") == 0) {
                            userEvents.get(j).date = new Date(data.getValue().toString());
                        }
                    }
                    Log.v(userEvents.get(j).date.getMonth() + "", userEvents.get(j).points + "");
                    // Add event points to respective month
                    pointsPerMonth[userEvents.get(j).date.getMonth()] += userEvents.get(j).points;
                    // Something to signal the finish of work
                    dummy.add(1);
                    if ( dummy.size() == userEvents.size() ) {
                        displayChart();
                        displayStatistics();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }
    }

    private void displayChart()
    {
        Log.v("Display","chart");
        BarChart chart = (BarChart) findViewById(R.id.chart);
        BarData data = new BarData (getXAxisValues(),getDataSet());
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        Log.v("Set data on chart","Dap");
        chart.setData(data);
        chart.setDescription("Months");
        chart.setDrawValueAboveBar(false);
        chart.setDrawingCacheEnabled(false);
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }
    private void displayStatistics()
    {
        final RecyclerView listOfSports = (RecyclerView) findViewById(R.id.listofSports);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listOfSports.setLayoutManager(layoutManager);

        Firebase userSports = User.firebaseRef.child("users").child(User.uid).child("sports");
        final ArrayList<Drawable> sportsList = new ArrayList<>();
        userSports.addListenerForSingleValueEvent(new ValueEventListener() { // get user sports
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot != null) {
                    for (DataSnapshot sport : snapshot.getChildren()) {
                        String uri = "@drawable/" + sport.getKey().toLowerCase().replace(" ", "");

                        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                        Drawable res = getResources().getDrawable(imageResource);
                        sportsList.add(res);
                    }
                }
                listOfSports.setAdapter(new MyAdapter(sportsList));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        StatsPerSportAdapter myadapter = new StatsPerSportAdapter(eventsPerSport,pointsPerSport, getApplicationContext());

        ListView mylistView = (ListView) findViewById(R.id.statsPerSport);
        if (mylistView != null)
            mylistView.setAdapter(myadapter);

        justifyListViewHeightBasedOnChildren(mylistView);

        final TextView totalPoints = (TextView)findViewById(R.id.totalPoints);
        totalPoints.append(getTotalPoints());
    }

    private String getTotalPoints()
    {   int sum = 0;
        for(int i = 0; i <= 11; i++)
            sum += pointsPerMonth[i];
        return sum+"";
    }

    // Adapter for the statistics per sport
    public class StatsPerSportAdapter extends BaseAdapter implements ListAdapter {

        private Context context;
        int [] eventsPerSp;
        int [] pointsPerSp;

        public StatsPerSportAdapter(int [] eventsPerS, int[] pointsPerS,  Context context) {
            this.eventsPerSp = eventsPerS;
            this.pointsPerSp = pointsPerS;
            this.context = context;
        }

        @Override
        public int getCount() {
            return 8;
        }
        @Override
        public Object getItem(int pos) {
            return eventsPerSp[pos];
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
                view = inflater.inflate(R.layout.stats_layout, null);
            }

            final TextView totalPoints = (TextView) view.findViewById(R.id.totalPointsPerSport);
            final TextView totalEvents = (TextView) view.findViewById(R.id.totalEventsAttendedPerSport);
            final ImageView sportLogo = (ImageView) view.findViewById(R.id.sport_logo);


            totalPoints.setText("Total points: " + pointsPerSp[position]);
            totalEvents.setText("Total events attended: " + eventsPerSp[position]);

            String sport = "";
            switch(position)
            {
                case 0 : sportLogo.setImageResource(R.drawable.football); sport = "football";    break;
                case 1 : sportLogo.setImageResource(R.drawable.volley);sport = "volley";      break;
                case 2 : sportLogo.setImageResource(R.drawable.basketball);sport = "basketball";  break;
                case 3 : sportLogo.setImageResource(R.drawable.chess);sport = "chess";       break;
                case 4 : sportLogo.setImageResource(R.drawable.pingpong);sport = "pingpong";    break;
                case 5 : sportLogo.setImageResource(R.drawable.tennis);sport = "tennis";      break;
                case 6 : sportLogo.setImageResource(R.drawable.cycling);sport = "cycling";     break;
                case 7 : sportLogo.setImageResource(R.drawable.jogging);sport = "jogging";     break;
                default : break;
            }


           //String uri = "@drawable/" + sport + ".png";
            //Log.v("SPORTDRAWABLE", uri);

            //sportLogo.setImageResource( getResources().getIdentifier(uri, null, getPackageName()));

            return view;
        }
    }

    public void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = 80 + totalHeight + (listView.getDividerHeight() * (adapter.getCount()));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<Drawable> icons;

        public MyAdapter(ArrayList<Drawable> icons) {
            this.icons = icons;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.icon_layout, null);

            // create ViewHolder

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {

            // - get data from your itemsData at this position
            // - replace the contents of the view with that itemsData

            viewHolder.imgViewIcon.setImageDrawable(icons.get(position));


        }

        // inner class to hold a reference to each item of RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView imgViewIcon;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
            }
        }


        // Return the size of your itemsData (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return icons.size();
        }
    }
    private static final String FIREBASE_URL = "https://ludicon.firebaseio.com/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_statistics);

        for(int i = 0; i < 8; i++)
        {
            eventsPerSport[i] = 0;
            pointsPerSport[i] = 0;
        }

        addSports();

        // Left side panel initializing
        mDrawerList = (ListView) findViewById(R.id.leftMenu);
        initializeLeftSidePanel();

        User.setImage();

        // User picture and name for HEADER MENU
        TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText(User.getFirstName(getApplicationContext()) + " " + User.getLastName(getApplicationContext()));

        TextView hello_message = (TextView) findViewById(R.id.hello_message_activity);
        hello_message.setText("Statistics");
        ImageView userPic = (ImageView) findViewById(R.id.userPicture);
        Drawable d = new BitmapDrawable(getResources(), User.image);
        userPic.setImageDrawable(d);
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                StatisticsActivity.this.startActivity(mainIntent);
            }
        });

        //final TextView eventsJoined = (TextView) findViewById(R.id.numberjoined);

        final TextView eventsPart = (TextView) findViewById(R.id.totalEventsAttend);

        final Date now  = new Date();


        // Iterate through user's events and foreach event : get it's ID and number of points
        Firebase userRef = User.firebaseRef.child("users").child(User.uid).child("events");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    for (DataSnapshot child : data.getChildren()) {
                        if (child.getKey().compareToIgnoreCase("points") == 0)
                            if (Integer.parseInt(child.getValue().toString()) != 0)
                            {
                                EventStats evStat = new EventStats();
                                evStat.id = data.getKey();
                                evStat.points = Integer.parseInt(child.getValue().toString());
                                userEvents.add(evStat);
                                Log.v("Add " + evStat.id,evStat.points + " ");
                                String text = eventsPart.getText().toString();
                                String [] splitT = text.split(":");
                                splitT[1] = splitT[1].replaceAll(" ","");
                                int auxiliarPoints = Integer.parseInt(splitT[1]);
                                auxiliarPoints++;
                                eventsPart.setText(splitT[0] + ": " + auxiliarPoints);
                            }
                    }
                }
                getDateAndSport();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    // Left side menu
    public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_chats);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, StatisticsActivity.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), StatisticsActivity.this);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_list, menu);
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

        return super.onOptionsItemSelected(item);
    }

    private BarDataSet getDataSet() {

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        Log.v(pointsPerMonth[4] + "", "BGPL");

        BarEntry v1e1 = new BarEntry(pointsPerMonth[0], 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(pointsPerMonth[1], 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(pointsPerMonth[2], 2); // Mar
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(pointsPerMonth[3], 3); // Apr
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(pointsPerMonth[4], 4); // May
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(pointsPerMonth[5], 5); // Jun
        valueSet1.add(v1e6);
        BarEntry v1e7 = new BarEntry(pointsPerMonth[6], 6); // Jul
        valueSet1.add(v1e7);
        BarEntry v1e8 = new BarEntry(pointsPerMonth[7], 7); // Aug
        valueSet1.add(v1e8);
        BarEntry v1e9 = new BarEntry(pointsPerMonth[8], 8); // Sep
        valueSet1.add(v1e9);
        BarEntry v1e10 = new BarEntry(pointsPerMonth[9], 9); // Oct
        valueSet1.add(v1e10);
        BarEntry v1e11 = new BarEntry(pointsPerMonth[10], 10); // Nov
        valueSet1.add(v1e11);
        BarEntry v1e12 = new BarEntry(pointsPerMonth[11], 11); // Dec
        valueSet1.add(v1e12);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Points per month");
        barDataSet1.setColors(ColorTemplate.JOYFUL_COLORS);
        return barDataSet1;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APR");
        xAxis.add("MAY");
        xAxis.add("JUN");
        xAxis.add("JUL");
        xAxis.add("AUG");
        xAxis.add("SEP");
        xAxis.add("OCT");
        xAxis.add("NOV");
        xAxis.add("DEC");
        return xAxis;
    }

}

