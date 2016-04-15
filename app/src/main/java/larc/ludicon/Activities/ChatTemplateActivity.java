package larc.ludicon.Activities;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import larc.ludicon.ChatUtils.Chat;
import larc.ludicon.ChatUtils.ChatListAdapter;
import larc.ludicon.R;
import larc.ludicon.UserInfo.User;

public class ChatTemplateActivity extends ListActivity {


    private static final String FIREBASE_URL = "https://ludicon.firebaseio.com/";

    private String mUsername;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private ChatListAdapter mChatListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //TODO Receive intent with the uid of the other user and if this is the first Connection between those two
        Intent intent = getIntent();
        final String otherUserUid  = intent.getStringExtra("uid");
        final boolean firstConnection = intent.getBooleanExtra("firstConnection", true);
        Log.v("UID + firstConnection", otherUserUid + " - " + firstConnection);
        final String chatID = intent.getStringExtra("chatID");
       // final String otherUserName = intent.getStringExtra("otherName");

        // Make sure we have a current Username
        setupUsername();

        //setTitle("Chatting with " + otherUserName);

        final List<String> ID = new ArrayList<>();


        if( firstConnection == true ){


            // Create general Chat for these two
            Firebase fireRef = new Firebase(FIREBASE_URL).child("chat");
            Firebase keyRef = fireRef.push();
            final String newChatID = keyRef.getKey();

            Map<String,String> map = new HashMap<>();
            map.put("Users","");
            map.put("Messages","");
            keyRef.setValue(map);
            Firebase addUserUIDs = keyRef.child("Users");

            Map<String,String> userUIDMap = new HashMap<>();
            userUIDMap.put(User.uid, "");
            userUIDMap.put(otherUserUid, "");
            addUserUIDs.setValue(userUIDMap);

            Date now = new Date();
            String formattedDate = String.format(new Locale("English"),"%tc", now);
            // Create our 'model', a Chat object
            Firebase newChat = keyRef.child("Messages").push();
            Chat chat = new Chat("Bun venit in chat!", "Ludicon",formattedDate);
            newChat.setValue(chat);

            // TODO Create child to "users -> userUID -> chats" for each user
            // For the first User
            Firebase refCurrentUser = new Firebase(FIREBASE_URL).child("users").child(User.uid).child("chats");
            refCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Map<String, Object> map = new HashMap<>();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        map.put(data.getKey(), data.getValue());
                    }
                    map.put(otherUserUid,newChatID);
                    // NOTE: I need userRef to set the map value
                    Firebase userRef = new Firebase(FIREBASE_URL).child("users").child(User.uid).child("chats");
                    userRef.updateChildren(map);
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });

            // For the second User
            Firebase refOtherUser = new Firebase(FIREBASE_URL).child("users").child(otherUserUid).child("chats");
            refOtherUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Map<String, Object> map = new HashMap<>();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        map.put(data.getKey(), data.getValue());
                    }
                    map.put(User.uid,newChatID);
                    // NOTE: I need userRef to set the map value
                    Firebase userRef = new Firebase(FIREBASE_URL).child("users").child(otherUserUid).child("chats");
                    userRef.updateChildren(map);
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });


            // Setup our Firebase mFirebaseRef
            mFirebaseRef = new Firebase(FIREBASE_URL).child("chat").child(newChatID).child("Messages");
        }
        if ( firstConnection == false )
             mFirebaseRef = new Firebase(FIREBASE_URL).child("chat").child(chatID).child("Messages");


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

        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 50 messages at a time
        mChatListAdapter = new ChatListAdapter(mFirebaseRef.limit(50), this, R.layout.chat_message, mUsername);
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        /*
        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(ChatTemplateActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatTemplateActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
      */
    }

    @Override
    public void onStop() {
        super.onStop();
//        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
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

            String formattedDate = String.format(new Locale("English"),"%tc", now );
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, mUsername,formattedDate);

            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(chat);

            inputText.setText("");
        }
    }
}
