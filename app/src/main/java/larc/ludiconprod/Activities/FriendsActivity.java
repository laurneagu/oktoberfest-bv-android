package larc.ludiconprod.Activities;

import android.app.Activity;

import java.io.Console;
import java.util.*;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.GraphRequest;
import com.facebook.AccessToken;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.internal.Logger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import larc.ludiconprod.Adapters.LeftPanelItemClicker;
import larc.ludiconprod.Adapters.LeftSidePanelAdapter;
import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.FriendUtils.FriendItem;
import larc.ludiconprod.Utils.util.Utils;

public class FriendsActivity extends Activity {

    String auxiliarURL;
    Boolean ok;

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    final ArrayList<FriendItem> friends = new ArrayList<>();
    final ArrayList<String> friendsUIDs = new ArrayList<>();
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide App bar
        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_friends);

        mDrawerList = (ListView) findViewById(R.id.leftMenu);
        initializeLeftSidePanel();

        try {
            User.setImage();
        }
        catch(Exception e){
            Log.d("friends", e.getMessage());
        }
        // User picture and name for HEADER MENU
        Typeface segoeui = Typeface.createFromAsset(getAssets(), "fonts/seguisb.ttf");

        /* Progress dialog */
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();


        TextView userName = (TextView) findViewById(R.id.userName);
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
                FriendsActivity.this.startActivity(mainIntent);
            }
        });

        Runnable getFirebaseInfo = getFirebaseInfoThread();
        Thread FirebaseInfoThread = new Thread(getFirebaseInfo);
        FirebaseInfoThread.start();

            DatabaseReference friendRef = User.firebaseRef.child("users").child(User.uid).child("friends");
            friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for(DataSnapshot data : snapshot.getChildren())
                    {
                        FriendItem friend = new FriendItem();
                        if (Boolean.parseBoolean(data.getValue().toString()) == true) {
                            friend.uid = data.getKey();
                            friends.add(friend);
                            friendsUIDs.add(friend.uid);
                        }
                    }

                    synchronized (waitForFriends) {
                        try {
                            waitForFriends.notify(); // ready, notify thread to get data from firebase
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });
        }

    Object waitForFriends = new Object();

    public Runnable getFirebaseInfoThread() {
        return new Runnable() {
            public void run() {

                synchronized (waitForFriends){
                    try {
                        waitForFriends.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                final List<Integer> toRemoveNoMoreFriends = new ArrayList<>();

                for(int i = 0; i < friends.size() ; ++i){
                    final int index = i;
                    DatabaseReference userSports = User.firebaseRef.child("users").child(friends.get(i).uid);
                    userSports.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot != null) {
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    if (data.getKey().toString().equalsIgnoreCase("profileImageURL"))
                                        friends.get(index).ImageUrl = data.getValue().toString();

                                    if (data.getKey().toString().equalsIgnoreCase("sports")) {
                                        int numOfSports =(int)data.getChildrenCount();
                                        if(numOfSports == 1){
                                            friends.get(index).numberOfSports = numOfSports  + " sport";
                                        }
                                        else {
                                            friends.get(index).numberOfSports = numOfSports + " sports";
                                        }
                                    }

                                    if (data.getKey().toString().equalsIgnoreCase("name"))
                                        friends.get(index).name = data.getValue().toString();

                                    if(data.getKey().toString().equalsIgnoreCase("friends")) {
                                        friends.get(index).friendsString = data.getValue().toString();
                                    }
                                }

                                if(friends.get(index).ImageUrl ==null){
                                    toRemoveNoMoreFriends.add(index);
                                }
                            }

                            if (index == friends.size()-1){ // if it is the last one, you can start the ui
                                progress.dismiss();
                                int iRemovedCount=0;
                                // Remove empty indexes
                                for(Integer indexToRemove : toRemoveNoMoreFriends){
                                    friends.remove(friends.get(indexToRemove-iRemovedCount));
                                    iRemovedCount++;
                                }

                                // Sort friends alphabetically
                                Collections.sort(friends);

                                adapter = new MyCustomAdapter(friends,getApplicationContext());
                                ListView listView = (ListView) findViewById(R.id.friends_listView);
                                Log.v("TAG",friends.size()+"");
                                listView.setAdapter(adapter);
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                        }
                    });
                }
        }

        };
    }

    private MyCustomAdapter adapter;
    @Override
    public void onResume(){
        super.onResume();

        if (adapter != null)
            adapter.notifyDataSetChanged();

    }

    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {

        private ArrayList<FriendItem> list = new ArrayList<>();
        private Context context;

        public MyCustomAdapter(ArrayList<FriendItem> list, Context context) {
            this.list = list;
            this.context = context;
        }

        class ViewHolder {
            TextView textName;
            ImageView imageView;
            ImageButton moreButton;
            Button chatButton;
            TextView numberSports;
            TextView numberMutuals;
        };

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
                view = inflater.inflate(R.layout.friends_layout, null);

                holder = new ViewHolder();
                holder.textName = (TextView) view.findViewById(R.id.list_item_string);
                holder.imageView = (ImageView) view.findViewById(R.id.profileImageView);
                holder.chatButton = (Button) view.findViewById(R.id.chat_btn);
                holder.numberSports = (TextView) view.findViewById(R.id.numberSports);
                holder.numberMutuals = (TextView) view.findViewById(R.id.numberoOfMutuals);

                view.setTag(holder);
            }
            else {
                holder = (ViewHolder)view.getTag();
            }

            view.setBackgroundColor(Color.parseColor("#FFFFFF"));

            // Set name in TextView
            holder.textName.setText(list.get(position).name);

            // Set Image in ImageView
            DatabaseReference userRef = User.firebaseRef.child("users").child(list.get(position).uid).child("profileImageURL");
            Log.v("Position", position + "");

            Picasso.with(context).load(list.get(position).ImageUrl).into(holder.imageView);
            holder.imageView.setBackgroundResource(R.drawable.defaultpicture);

            // Number of sports
            holder.numberSports.setText(list.get(position).numberOfSports);

            // Calculate number of mutuals
            if (list.get(position).friendsString != null) {
                String[] friends = list.get(position).friendsString.split(",");
                int numOfMutuals=0;
                for(int i = 0 ; i < friends.length ; i ++){
                    // current friend is still a friend of the friend :)
                    if (friends[i].contains("true")){
                        String friendsUid = friends[i].replace("true","").trim();
                        friendsUid = friendsUid.replace("=","");

                        // is also my friend
                        if(friendsUIDs.contains(friendsUid)) numOfMutuals ++;
                    }
                }
                if (numOfMutuals == 0){
                    holder.numberMutuals.setText("no mutual friends");
                }
                else if (numOfMutuals == 1){
                    holder.numberMutuals.setText("1 mutual friend");
                }
                else {
                    holder.numberMutuals.setText(numOfMutuals + " mutual friends");
                }
            }
            else {
                holder.numberMutuals.setText("no mutual friends");
            }

            final View currentView = view;
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    currentView.setBackgroundColor(Color.parseColor("#D3D3D3"));
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("uid", list.get(position).uid);
                    startActivity(intent);
                }
            });

            final Button chatButton = holder.chatButton;
            // Buttons behaviour
            holder.chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chatButton.setBackground(getResources().getDrawable(R.drawable.settings_icon_notselected));
                    DatabaseReference userRef = User.firebaseRef.child("users").child(list.get(position).uid).child("chats");
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            // check if it's the first connection
                            boolean firstConnection = true;
                            String chatID = "";
                            for (DataSnapshot data : snapshot.getChildren()) {
                                if (data.getKey().equalsIgnoreCase(User.uid)) {
                                    firstConnection = false;
                                    chatID = data.getValue().toString();
                                }
                            }
                            Intent intent = new Intent(getApplicationContext(), ChatTemplateActivity.class);
                            intent.putExtra("uid", list.get(position).uid);
                            intent.putExtra("firstConnection", firstConnection);
                            intent.putExtra("otherName", list.get(position).name);
                            intent.putExtra("chatID", chatID);

                            chatButton.setBackground(getResources().getDrawable(R.drawable.settings_icon_selected));

                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                        }
                    });
                }
            });
            return view;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
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

    // Left side menu

    public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_settings);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, FriendsActivity.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), FriendsActivity.this);

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

    // Delete the history stack and point to Main activity
    @Override
    public void onBackPressed() {
        Intent toMain = new Intent(this,MainActivity.class);
        toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMain);
    }
}
