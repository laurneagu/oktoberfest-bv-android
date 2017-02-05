package larc.ludiconprod.Activities;

/**
 * Created by Ciprian on 7/23/2016.
 */

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Locale;

import larc.ludiconprod.Adapters.LeftPanelItemClicker;
import larc.ludiconprod.Adapters.LeftSidePanelAdapter;
import larc.ludiconprod.ChatUtils.Chat;
import larc.ludiconprod.ChatUtils.ChatListAdapter;
import larc.ludiconprod.UserInfo.User;

import larc.ludiconprod.R;
import larc.ludiconprod.Utils.util.DateManager;

public class GroupChatTemplate extends ListActivity {


    private static final String FIREBASE_URL = "https://ludicon.firebaseio.com/";

    private String mUsername;
    private DatabaseReference mDatabaseReferenceRef;
    private ValueEventListener mConnectedListener;
    private ChatListAdapter mChatListAdapter;

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public static boolean isForeground = false;

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
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_chat);

        // Left side panel
        mDrawerList = (ListView) findViewById(R.id.leftMenu);
        initializeLeftSidePanel();

        User.setImage();

        // User picture and name for HEADER MENU
        Typeface segoeui = Typeface.createFromAsset(getAssets(), "fonts/seguisb.ttf");

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
                GroupChatTemplate.this.startActivity(mainIntent);
            }
        });

        //TODO Receive intent with event ID and enter messages
        Intent intent = getIntent();
        final String eventID = intent.getStringExtra("eventID");

        mDatabaseReferenceRef = FirebaseDatabase.getInstance().getReference().child("events").child(eventID).child("chat");

        TextView hello_message = (TextView) findViewById(R.id.hello_message_activity);
        hello_message.setText("Event Chat");

        // Make sure we have a current Username
        setupUsername();

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        isForeground = true;

        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 50 messages at a time
        // TODO - Don't use ChatListAdapter

        mChatListAdapter = new ChatListAdapter(mDatabaseReferenceRef.limitToFirst(50), this, R.layout.chat_message, mUsername, true);
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isForeground = true;
    }

    @Override
    public void onStop() {
        super.onStop();
//        mDatabaseReferenceRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }

    private void setupUsername() {
        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
        mUsername = prefs.getString("username", null);
        if (mUsername == null) {

            prefs.edit().putString("username", "cartof").commit();
        }
    }

    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {

            Date now = new Date();

            //DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("ENGLISH"));
            //String formattedDate = df.format(now);

            String formattedDate = String.format(new Locale("English"), "%tb", now) + " " +
                    String.format(new Locale("English"), "%td", now) + ", " +
                    String.format(new Locale("English"), "%tR", now);
            // Create our 'model', a Chat object
            Log.v("Username", mUsername);
            Chat chat = new Chat(input, mUsername, DateManager.getTimeNowInSeconds(), User.uid);

            // Create a new, auto-generated child of that chat location, and save our chat data there
            mDatabaseReferenceRef.push().setValue(chat);

            inputText.setText("");
        }
    }


    public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_chat);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, GroupChatTemplate.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), GroupChatTemplate.this);

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