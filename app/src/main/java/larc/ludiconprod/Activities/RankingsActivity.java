package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import larc.ludiconprod.Adapters.LeftPanelItemClicker;
import larc.ludiconprod.Adapters.LeftSidePanelAdapter;
import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.util.Utils;

public class RankingsActivity extends Activity {

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rankings);

//        getActionBar().hide();
        // Left side panel initializing
        mDrawerList = (ListView) findViewById(R.id.leftMenu);
        initializeLeftSidePanel();

        User.setImage();

        // User picture and name for HEADER MENU
        TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText(User.getFirstName(getApplicationContext()) + " " + User.getLastName(getApplicationContext()));


        TextView hello_message = (TextView) findViewById(R.id.hello_message_activity);
        hello_message.setText("Rankings");
        ImageView userPic = (ImageView) findViewById(R.id.userPicture);
        Drawable d = new BitmapDrawable(getResources(), User.image);
        userPic.setImageDrawable(d);
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                RankingsActivity.this.startActivity(mainIntent);
            }
        });

        // DropDown for the sports

        Spinner spinner = (Spinner) findViewById(R.id.listSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rankings_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        final Map<Integer,String> sportName =  new HashMap<>();
        getSports(sportName);

        spinner.setSelection(0, true);
        processRankings(sportName.get(0));
        View v = spinner.getSelectedView();
        ((TextView)v).setTextColor(Color.parseColor("#000000"));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.v("Selected Item", sportName.get(position));
                processRankings(sportName.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        }
        catch(Exception exc) {
            Utils.quit();
        }
    }

    private class Contestant{
        int points;
        String name;
        String id;
    }
    private void processRankings(String sport)
    { try{
        DatabaseReference pointsRef = User.firebaseRef.child("points").child(sport);

        final ArrayList<Contestant>  contestants = new ArrayList<>();

        Log.v("Tag","in processRankings");
        pointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Contestant c = new Contestant();
                    c.points = 0;
                    c.id = "";
                    Log.v("Data",data.toString());
                    if ( data.getKey() != null )
                    {
                        c.points = Integer.parseInt(data.getValue().toString());
                        c.id = data.getKey().toString();
                        contestants.add(c);
                    }
                }
                showRankings(contestants);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }
    catch(Exception exc) {
        Utils.quit();
    }
    }

    private void showRankings( ArrayList<Contestant>  contestants)
    {
        Log.v("Tag", "in showRankings");
        // Sort by score
        Collections.sort(contestants, new Comparator<Contestant>() {
            @Override
            public int compare(Contestant lhs, Contestant rhs) {
                return rhs.points - lhs.points;
            }
        });

        int myRank = -1;
        Contestant aboveMe = new Contestant();
        Contestant belowMe = new Contestant();

        for (int i = 0; i < contestants.size(); i++ ) {
            if (contestants.get(i).id.equalsIgnoreCase(User.uid))
            {
                myRank = i + 1;

                if ( i-1 >= 0)
                    aboveMe = contestants.get(i-1);
                else aboveMe = null;

                if ( i+1 < contestants.size() )
                    belowMe = contestants.get(i+1);
                else belowMe = null;

                break;
            }
        }
        int sizeCon = contestants.size();
        if ( sizeCon < 10 ) {
            Contestant dummy = new Contestant();
            dummy.points = -1;
            for (int j = 1; j <= 10 - sizeCon; j++)
            {
                contestants.add(dummy);
            }
        }

        Log.v("Remanining",10 - contestants.size() + "");

        final TextView myRankText = (TextView)findViewById(R.id.myRank);

        // I don't have any points
        if ( myRank == -1)
            myRankText.setText("-");
        else myRankText.setText(myRank+"");

        final TextView firstPlace = (TextView)findViewById(R.id.locul1);
        //Get name for firstPlace
            if ( contestants.get(0).points != -1 ) {
                final int p = contestants.get(0).points;
                DatabaseReference nameRef = User.firebaseRef.child("users").child(contestants.get(0).id).child("name");
                nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot != null && snapshot.getValue() != null) {
                            String[] splits = snapshot.getValue().toString().split(" ");
                            firstPlace.setText(splits[0]);
                            for (int i = 1; i < splits.length && i == 1; i ++)
                                firstPlace.append(" " + splits[i]);
                            firstPlace.append("\n");
                            boolean hasMoreThan3 = false;
                            for (int i = 2; i < splits.length; i ++) {
                                hasMoreThan3 = true;
                                firstPlace.append(splits[i] + " ");
                            }
                            if ( hasMoreThan3 ) firstPlace.append("\n");
                            firstPlace.append("(" + p + ")");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                    }
                });
            }
        else firstPlace.setText("Pending");

        final TextView secondPlace = (TextView)findViewById(R.id.locul2);
        //Get name for firstPlace
        if ( contestants.get(1).points != -1 ) {
            final int p = contestants.get(1).points;
            DatabaseReference nameRef = User.firebaseRef.child("users").child(contestants.get(1).id).child("name");
            nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot != null && snapshot.getValue() != null) {
                        String[] splits = snapshot.getValue().toString().split(" ");
                        secondPlace.setText(splits[0]);
                        for (int i = 1; i < splits.length && i == 1; i ++)
                            secondPlace.append(" " + splits[i]);
                        secondPlace.append("\n");
                        boolean hasMoreThan3 = false;
                        for (int i = 2; i < splits.length; i ++) {
                            hasMoreThan3 = true;
                            secondPlace.append(splits[i] + " ");
                        }
                        if ( hasMoreThan3 ) secondPlace.append("\n");
                        secondPlace.append("(" + p + ")");
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });
        }
        else secondPlace.setText("Pending");

        final TextView thirdPlace = (TextView)findViewById(R.id.locul3);
        //Get name for firstPlace
        if ( contestants.get(2).points != -1 ) {
            final int p = contestants.get(2).points;
            DatabaseReference nameRef = User.firebaseRef.child("users").child(contestants.get(2).id).child("name");
            nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot != null && snapshot.getValue() != null) {
                        String[] splits = snapshot.getValue().toString().split(" ");
                        thirdPlace.setText(splits[0]);
                        for (int i = 1; i < splits.length && i == 1; i ++)
                            thirdPlace.append(" " + splits[i]);
                        thirdPlace.append("\n");
                        boolean hasMoreThan3 = false;
                        for (int i = 2; i < splits.length; i ++) {
                            hasMoreThan3 = true;
                            thirdPlace.append(splits[i] + " ");
                        }
                        if ( hasMoreThan3 ) thirdPlace.append("\n");
                        thirdPlace.append("(" + p + ")");
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });
        }
        else thirdPlace.setText("Pending");

        contestants.remove(0);
        contestants.remove(0);
        contestants.remove(0);

        RankingAdapter myadapter = new RankingAdapter(contestants, getApplicationContext());

        ListView mylistView = (ListView) findViewById(R.id.rankingsList);
        if (mylistView != null)
            mylistView.setAdapter(myadapter);

    }

    // Adapter for the ranking per sport
    public class RankingAdapter extends BaseAdapter implements ListAdapter {

        private Context context;
        ArrayList<Contestant>  contestants;

        public RankingAdapter(ArrayList<Contestant> contestants,Context context) {
            this.contestants = contestants;
            this.context = context;
        }

        @Override
        public int getCount() {
            return contestants.size();
        }
        @Override
        public Object getItem(int pos) {
            return contestants.get(pos);
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
                view = inflater.inflate(R.layout.ranking_layout, null);
            }

            final TextView place = (TextView) view.findViewById(R.id.placeNumber);
            final TextView name = (TextView) view.findViewById(R.id.nameText);

            if ( position ==6 ) place.setText("10.");
            else
                place.setText("  " + (position + 4) + ".");
            if ( contestants.get(position).points != -1 ) {
                DatabaseReference nameRef = User.firebaseRef.child("users").child(contestants.get(position).id).child("name");
                nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot != null && snapshot.getValue() != null) {
                            name.setText(snapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                    }
                });
            }
            else name.setText("Pending");

            return view;
        }
    }

    private void getSports(Map<Integer,String> sportName)
    {
        sportName.put(0,"football");
        sportName.put(1,"volleyball");
        sportName.put(2,"basketball");
        sportName.put(3,"squash");
        sportName.put(4,"pingpong");
        sportName.put(5,"tennis");
        sportName.put(6,"cycling");
        sportName.put(7,"jogging");
        sportName.put(8,"gym");
        sportName.put(9,"other");
    }

    // Left side menu
    public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_chats);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, RankingsActivity.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), RankingsActivity.this);

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
        getMenuInflater().inflate(R.menu.menu_rankings, menu);
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
}
