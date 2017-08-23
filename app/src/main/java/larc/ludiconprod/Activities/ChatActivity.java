package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import larc.ludiconprod.Adapters.ChatAndFriends.MessageAdapter;
import larc.ludiconprod.BottomBarHelper.BottomBar;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Chat;
import larc.ludiconprod.Utils.Message;

/**
 * Created by ancuta on 8/22/2017.
 */

public class ChatActivity extends Activity {
    ProgressBar chatLoading;
    ArrayList<Message> messageList=new ArrayList<>();
    public int counterOfChats=0;
    public String keyOfLastChat;
    public int valueOfLastChat;
    public int numberOfChatsPage;
    ProgressBar progressBarChats;
    int numberOfTotalChatsArrived;
    Boolean isLastPage=false;
    Boolean addedSwipe=false;
    MessageAdapter messageAdapter;
    public static ListView messageListView;
    Boolean isFirstTimeSetMessage=false;
    Button sendButton;
    EditText messageInput;


    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.chat_activity);
        sendButton=(Button)findViewById(R.id.sendButton);
        messageInput=(EditText)findViewById(R.id.messageInput);
        chatLoading=(ProgressBar)findViewById(R.id.chatLoading);

        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("chats").child(getIntent().getStringExtra("chatId")).child("messages");
        firebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(isFirstTimeSetMessage) {
                    Message message = new Message();
                    message.authorId = dataSnapshot.child("author_id").getValue().toString();
                    message.date = Integer.valueOf(dataSnapshot.child("date").getValue().toString());
                    message.message = dataSnapshot.child("message").getValue().toString();
                    message.messageId = dataSnapshot.getKey();
                    messageList.add(message);
                    setAdapter();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        messageAdapter = new MessageAdapter(messageList, getApplicationContext(), this, getResources(), ChatActivity.this);
        getFirstPage();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(messageInput.getText().length() > 0){
                    final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("chats").child(getIntent().getStringExtra("chatId"));
                    firebaseRef.child("last_message_date").setValue(System.currentTimeMillis() / 1000);
                    HashMap<String,String> values=new HashMap<String, String>();
                    values.put("author_id",Persistance.getInstance().getUserInfo(ChatActivity.this).id);
                    values.put("date",String.valueOf(System.currentTimeMillis() / 1000));
                    values.put("message",messageInput.getText().toString());
                    firebaseRef.child("messages").push().setValue(values);
                    /*
                    databaseReference.child("author_id").setValue(Persistance.getInstance().getUserInfo(ChatActivity.this).id);
                    databaseReference.child("date").setValue(System.currentTimeMillis() / 1000);
                    databaseReference.child("message").setValue(messageInput.getText().toString());
                    */

                }
            }
        });







    }

    public void getFirstPage(){
        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(ChatActivity.this).id).child("chats").child(getIntent().getStringExtra("chatId")).child("messages");
        Query query=firebaseRef.limitToLast(4);
        numberOfChatsPage=1;
        messageList.clear();
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberOfTotalChatsArrived = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot messages : dataSnapshot.getChildren()) {
                    Message message = new Message();
                    message.authorId = messages.child("author_id").getValue().toString();
                    message.date = Integer.valueOf(messages.child("date").getValue().toString());
                    message.message = messages.child("message").getValue().toString();
                    message.messageId = messages.getKey();


                    if (counterOfChats > 0 || numberOfTotalChatsArrived < 4 ) {
                        messageList.add(message);
                        setAdapter();
                        chatLoading.setAlpha(0f);
                        counterOfChats++;
                    } else {
                        counterOfChats++;
                        keyOfLastChat = message.messageId;
                        valueOfLastChat = message.date;
                    }
                    if (numberOfTotalChatsArrived < 4) {
                        if (numberOfTotalChatsArrived == counterOfChats) {
                            isLastPage = true;
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }

    public void getPage() {
        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(ChatActivity.this).id).child("chats").child(getIntent().getStringExtra("chatId")).child("messages");
        Query query = firebaseRef.endAt(null, keyOfLastChat).limitToLast(4);
        counterOfChats = 0;
        query.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberOfTotalChatsArrived = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot messages : dataSnapshot.getChildren()) {
                    Message message = new Message();
                    message.authorId = messages.child("author_id").getValue().toString();
                    message.date = Integer.valueOf(messages.child("date").getValue().toString());
                    message.message = messages.child("message").getValue().toString();
                    message.messageId = messages.getKey();


                    if(counterOfChats > 0 || numberOfTotalChatsArrived < 4) {
                        if(numberOfTotalChatsArrived < 4){
                            messageList.add(counterOfChats, message);
                        }else {
                            messageList.add(counterOfChats-1, message);
                        }
                        setAdapter();
                        counterOfChats++;
                    }
                    else{
                        counterOfChats++;
                        keyOfLastChat = message.messageId;
                        valueOfLastChat = message.date;
                    }
                    if (numberOfTotalChatsArrived < 4) {
                        if (numberOfTotalChatsArrived == counterOfChats) {
                            isLastPage = true;
                        }

                    }


                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setAdapter(){
        messageAdapter.notifyDataSetChanged();
        messageListView = (ListView) findViewById(R.id.chatMessageListView);
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refreshMessage);
        if(!isFirstTimeSetMessage) {
            messageListView.setAdapter(messageAdapter);
            isFirstTimeSetMessage=true;
        }


        if (!addedSwipe) {
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if(!isLastPage) {



                        getPage();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                    addedSwipe = false;
                }
            });
            addedSwipe = true;
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,Main.class);
        intent.putExtra("setChatTab",true);
        startActivity(intent);
        finish();
    }
}
