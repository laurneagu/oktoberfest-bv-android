package larc.ludicon.Activities;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;

import larc.ludicon.Adapters.LeftPanelItemClicker;
import larc.ludicon.Adapters.LeftSidePanelAdapter;
import larc.ludicon.R;
import larc.ludicon.UserInfo.User;


public class ProfileActivity extends Activity {

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private class ComparePals{
        int first_points;
        int second_points;
        ComparePals()
        {
            first_points = 0;
            second_points = 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
                ProfileActivity.this.startActivity(mainIntent);
            }
        });

        Bundle extras = getIntent().getExtras();
        final String uid = extras.getString("uid");

        DatabaseReference userRef = User.firebaseRef.child("users").child(uid).child("name"); // check user
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.getValue() != null) {
                    TextView name = (TextView) findViewById(R.id.hello_message_activity);
                    name.setText(snapshot.getValue().toString());

                    DatabaseReference userRef = User.firebaseRef.child("users").child(uid).child("profileImageURL");
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                ImageView imageView = (ImageView) findViewById(R.id.profileImageView);
                                new DownloadImageTask(imageView).execute(snapshot.getValue().toString());
                            } else {
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                        }
                    });
                } else {
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });

        final RecyclerView listOfSports = (RecyclerView) findViewById(R.id.listOfSports);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listOfSports.setLayoutManager(layoutManager);

        DatabaseReference userSports = User.firebaseRef.child("users").child(uid).child("sports");
        final ArrayList<Drawable> sportsList = new ArrayList<>();

        ImageButton chatButton = (ImageButton)findViewById(R.id.chatbutton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference userRef = User.firebaseRef.child("users").child(uid).child("chats");
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot == null) {
                            Intent intent = new Intent(getApplicationContext(), ChatTemplateActivity.class);
                            intent.putExtra("uid", uid);
                            intent.putExtra("firstConnection", true);
                            startActivity(intent);
                        } else {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                if (data.getKey().equalsIgnoreCase(User.uid)) {
                                    Intent intent = new Intent(getApplicationContext(), ChatTemplateActivity.class);
                                    intent.putExtra("uid", uid);
                                    intent.putExtra("firstConnection", false);
                                    intent.putExtra("chatID", data.getValue().toString());
                                    startActivity(intent);
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                    }
                });
            }
        });

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
            public void onCancelled(DatabaseError firebaseError) {
            }
        });

       final ArrayList<ComparePals> compareArray = new ArrayList<>();
        for(int i = 0; i <= 7; i++) compareArray.add(new ComparePals());
        getPoints("football",0,User.uid,uid,compareArray);
        getPoints("volley",1,User.uid,uid,compareArray);
        getPoints("basketball",2,User.uid,uid,compareArray);
        getPoints("squash",3,User.uid,uid,compareArray);
        getPoints("pingpong",4,User.uid,uid,compareArray);
        getPoints("tennis",5,User.uid,uid,compareArray);
        getPoints("cycling",6,User.uid,uid,compareArray);
        getPoints("jogging",7,User.uid,uid,compareArray);

        try{
            Thread.sleep(300,1);
        }catch (InterruptedException exc) {}

        StatsPerSportAdapter myadapter = new StatsPerSportAdapter(compareArray,getApplicationContext());

        ListView mylistView = (ListView) findViewById(R.id.compareList);
        if (mylistView != null)
            mylistView.setAdapter(myadapter);
    }

    void getPoints(String sport, final int sportID, final String myUid, final String friendUid, final ArrayList<ComparePals> compareArray )
    {
        DatabaseReference pointsRef = User.firebaseRef.child("points").child(sport);
        pointsRef.addListenerForSingleValueEvent(new ValueEventListener() { // get user sports
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot != null) {
                    for (DataSnapshot sport : snapshot.getChildren()) {
                        if( sport != null )
                        {
                            if (sport.getKey().toString().equalsIgnoreCase(myUid))
                                compareArray.get(sportID).second_points = Integer.parseInt(sport.getValue().toString());
                            if (sport.getKey().toString().equalsIgnoreCase(friendUid))
                            compareArray.get(sportID).first_points = Integer.parseInt(sport.getValue().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }
    // Adapter for the statistics per sport
    public class StatsPerSportAdapter extends BaseAdapter implements ListAdapter {

        private Context context;
        ArrayList<ComparePals> compareArray ;

        public StatsPerSportAdapter(ArrayList<ComparePals> compareArray , Context context) {
            this.compareArray = compareArray;
            this.context = context;
        }

        @Override
        public int getCount() {
            return compareArray.size();
        }
        @Override
        public Object getItem(int pos) {
            return compareArray.get(pos);
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
                view = inflater.inflate(R.layout.compare_layout, null);
            }

            final TextView comparePoints = (TextView) view.findViewById(R.id.pointsCompare);
            final ImageView sportLogo = (ImageView) view.findViewById(R.id.sport_logo_compare);


            comparePoints.setText( compareArray.get(position).first_points + " points" + " VS " + compareArray.get(position).second_points + " points (YOU)");

            switch(position)
            {
                case 0 : sportLogo.setImageResource(R.drawable.football);break;
                case 1 : sportLogo.setImageResource(R.drawable.volley);break;
                case 2 : sportLogo.setImageResource(R.drawable.basketball);break;
                case 3 : sportLogo.setImageResource(R.drawable.squash);break;
                case 4 : sportLogo.setImageResource(R.drawable.pingpong);break;
                case 5 : sportLogo.setImageResource(R.drawable.tennis);break;
                case 6 : sportLogo.setImageResource(R.drawable.cycling);break;
                case 7 : sportLogo.setImageResource(R.drawable.jogging);break;
                default : break;
            }
            return view;
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // Left side menu

    public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_settings);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, ProfileActivity.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), ProfileActivity.this);

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
