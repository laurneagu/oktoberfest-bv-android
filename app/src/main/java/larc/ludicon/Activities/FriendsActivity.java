package larc.ludicon.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.AccessToken;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import larc.ludicon.Adapters.LeftPanelItemClicker;
import larc.ludicon.Adapters.LeftSidePanelAdapter;
import larc.ludicon.R;
import larc.ludicon.UserInfo.User;
import larc.ludicon.Utils.Event;

public class FriendsActivity extends Activity {

    String auxiliarURL;
    Boolean ok;

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    final ArrayList<FriendItem> friends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        TextView header = (TextView) findViewById(R.id.hello_message_activity);
        header.setText("Friends");


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
                FriendsActivity.this.startActivity(mainIntent);
            }
        });

        Runnable getFirebaseInfo = getFirebaseInfoThread();
        Thread FirebaseInfoThread = new Thread(getFirebaseInfo);
        FirebaseInfoThread.start();

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                            /* handle the result */
                        try{
                            JSONArray friendsList = response.getJSONObject().getJSONArray("data");

                            for (int l=0; l < friendsList.length(); l++) {
                                FriendItem friend =  new FriendItem();
                                friend.name = friendsList.getJSONObject(l).getString("name");
                                friend.uid = "facebook:" + friendsList.getJSONObject(l).getString("id");
                                friends.add(friend);
                            }
                            // friends contains all the names of my Facebook friends who use Ludicon
                            // Display in ListView

                            synchronized (waitForFriends) {
                                try {
                                    waitForFriends.notify(); // ready, notify thread to get data from firebase
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();



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
                                    if (data.getKey().toString().equalsIgnoreCase("sports"))
                                        friends.get(index).numberOfSports = data.getChildrenCount() + " sports";
                                }


                            }

                            if (index == friends.size()-1){ // if it is the last one, you can start the ui
                                MyCustomAdapter adapter = new MyCustomAdapter(friends,getApplicationContext());
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



    class FriendItem
    {
        public String name;
        public String uid;
        public String ImageUrl;
        public String numberOfSports;
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
            ImageButton chatButton;
            TextView numberSports;
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

                view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("uid", list.get(position).uid);
                        startActivity(intent);
                    }
                });

                holder = new ViewHolder();
                holder.textName = (TextView) view.findViewById(R.id.list_item_string);
                holder.imageView = (ImageView) view.findViewById(R.id.profileImageView);
                holder.chatButton = (ImageButton) view.findViewById(R.id.chat_btn);
                holder.numberSports = (TextView) view.findViewById(R.id.numberSports);

                view.setTag(holder);
            }
            else {
                holder = (ViewHolder)view.getTag();
            }


            // Set name in TextView
            holder.textName.setText(list.get(position).name);

            // Set Image in ImageView
            DatabaseReference userRef = User.firebaseRef.child("users").child(list.get(position).uid).child("profileImageURL");
            Log.v("Position", position + "");
//

            Picasso.with(context).load(list.get(position).ImageUrl).into(holder.imageView);

            holder.numberSports.setText("plays " + list.get(position).numberOfSports);

            // Buttons behaviour
            holder.chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
