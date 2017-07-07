package larc.ludiconprod.Activities;


import android.app.ListActivity;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import larc.ludiconprod.R;
import larc.ludiconprod.Utils.ChatUtils.Chat;
import larc.ludiconprod.Utils.ChatUtils.ChatListAdapter;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.util.DateManager;

public class GeneralChatActivity extends ListActivity {


    private static final String FIREBASE_URL = "https://ludicon.firebaseio.com/";

    private String mUsername;
    private DatabaseReference mDatabaseReferenceRef;
    private ValueEventListener mConnectedListener;
    private ChatListAdapter mChatListAdapter;

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

        // Make sure we have a mUsername
        setupUsername();

        /*
        setTitle("Chatting as " + mUsername);
        */

        // Setup our DatabaseReference mDatabaseReferenceRef
        // TODO User to User Chat
        mDatabaseReferenceRef = FirebaseDatabase.getInstance().getReference().child("globalChat");

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

        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("chat");
        //firebaseRef.push();

        DatabaseReference newRef = firebaseRef.push();
        newRef.setValue(1);
        Log.v("impossible", newRef.getKey());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 50 messages at a time
        mChatListAdapter = new ChatListAdapter(mDatabaseReferenceRef.limitToFirst(50), this, R.layout.chat_message, mUsername,true);
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mDatabaseReferenceRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(GeneralChatActivity.this, "Connected to DatabaseReference", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GeneralChatActivity.this, "Disconnected from DatabaseReference", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                // No-op
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mDatabaseReferenceRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
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
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, mUsername, DateManager.getTimeNowInSeconds(), User.uid);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mDatabaseReferenceRef.push().setValue(chat);
            inputText.setText("");
        }
    }
}
