package larc.ludicon.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.logging.Handler;

import larc.ludicon.Adapters.LeftPanelItemClicker;
import larc.ludicon.Adapters.LeftSidePanelAdapter;
import larc.ludicon.R;
import larc.ludicon.UserInfo.User;

public class ChatListActivity extends Activity {

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog dialog;
    private int TIMEOUT = 80;

    private static final String FIREBASE_URL = "https://ludicon.firebaseio.com/";

    public class Chat1to1{
        String userUID;
        String chatID;
        String userName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat_list);

        // Left side panel initializing
        mDrawerList = (ListView) findViewById(R.id.leftMenu);
        initializeLeftSidePanel();

        User.setImage();

        dialog = ProgressDialog.show(ChatListActivity.this, "", "Loading. Please wait", true);

        // User picture and name for HEADER MENU
        TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText(User.getFirstName(getApplicationContext()) + " " + User.getLastName(getApplicationContext()));

        TextView hello_message = (TextView) findViewById(R.id.hello_message_activity);
        hello_message.setText("Chats");
        ImageView userPic = (ImageView) findViewById(R.id.userPicture);
        Drawable d = new BitmapDrawable(getResources(), User.image);
        userPic.setImageDrawable(d);
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                ChatListActivity.this.startActivity(mainIntent);
            }
        });

        Firebase firebaseRef = new Firebase(FIREBASE_URL).child("users").child(User.uid).child("chats");
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                List<Chat1to1> chatList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Chat1to1 chat = new Chat1to1();
                    chat.userUID = data.getKey().toString();
                    chat.chatID = data.getValue().toString();
                    chatList.add(chat);
                }
                MyCustomAdapter adapter = new MyCustomAdapter(chatList, getApplicationContext());
                ListView listView = (ListView) findViewById(R.id.chat_list);
                listView.setAdapter(adapter);

                // Dismiss loading dialog after  2 * TIMEOUT * chatList.size() ms
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
                timer.schedule(delayedThreadStartTask, TIMEOUT * 6 * chatList.size());

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {

        private List<Chat1to1> list = new ArrayList<>();
        private Context context;
        //private final Map<String,Boolean> states = new HashMap<String,Boolean>();


        public MyCustomAdapter(List<Chat1to1> list, Context context) {
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
                view = inflater.inflate(R.layout.chat1to1_layout, null);
            }

            final TextView textName = (TextView) view.findViewById(R.id.friend_name);
            final ImageView imageView = (ImageView) view.findViewById(R.id.friend_photo);
            Button chatButton = (Button) view.findViewById(R.id.gotoChat);

            // Set friend's name and image

            Firebase firebaseRef = new Firebase(FIREBASE_URL).child("users").child(list.get(position).userUID);
            firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            for (DataSnapshot data : snapshot.getChildren()) {
                                if ((data.getKey()).compareTo("name") == 0) {
                                    String name = data.getValue().toString();
                                    Log.v("Name", "Set name:" + name + "to position:" + position);
                                    textName.setText(name);
                                }
                                if ((data.getKey()).compareTo("profileImageURL") == 0)
                                    if (data.getValue() != null) {
                                        //new DownloadImageTask(imageView).execute(data.getValue().toString());
                                        Picasso.with(context).load(data.getValue().toString()).into(imageView);
                                    } else {
                                        imageView.setImageResource(R.drawable.logo);
                                    }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

            try{
                Thread.sleep(TIMEOUT,1);
            }
            catch(InterruptedException exc ){}
            // Buttons behaviour
            chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Firebase userRef = User.firebaseRef.child("users").child(list.get(position).userUID).child("name");
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Intent intent = new Intent(getApplicationContext(), ChatTemplateActivity.class);
                            intent.putExtra("uid", list.get(position).userUID);
                            intent.putExtra("firstConnection", false);
                            intent.putExtra("chatID", list.get(position).chatID);
                            startActivity(intent);
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

    // Left side menu

    public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_chats);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, ChatListActivity.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), ChatListActivity.this);

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

    // Delete the history stack and point to Main activity
    @Override
    public void onBackPressed() {
        Intent toMain = new Intent(this,MainActivity.class);
        toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMain);
    }

}
