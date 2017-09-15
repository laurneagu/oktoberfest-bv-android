package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import larc.ludiconprod.Adapters.ChatAndFriends.MessageAdapter;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Message;

/**
 * Created by ancuta on 8/22/2017.
 */

public class ChatActivity extends Activity {
    static public ProgressBar chatLoading;
    ArrayList<Message> messageList=new ArrayList<>();
    public int counterOfChats=0;
    public String keyOfLastChat;
    public Double valueOfLastChat;
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
    static public Boolean isOnChat1to1=false;
    Object waitForChatLoading = new Object();
    RelativeLayout backButton;
    Boolean needToScroll=true;
    int nrOfPage=0;
    int isGroupChat=0;
    Boolean createChat=false;
    String ChatId;
    ArrayList<String> unseenChats;
    ArrayList<String> otherUsersId=new ArrayList<>();
    ArrayList<String> otherUsersImage=new ArrayList<>();
    TextView titleText;
    private int dp56;


    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public void checkChatExistence(final String otherUserId,final  Activity activity){
        final DatabaseReference myNode = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(activity).id).child("talkbuddies");
        final DatabaseReference otherUserNode = FirebaseDatabase.getInstance().getReference().child("users").child(otherUserId).child("talkbuddies");
        myNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot myChatParticipants) {
                String chatId;
                if(myChatParticipants.hasChild(otherUserId)){
                    chatId=myChatParticipants.child(otherUserId).getValue().toString();
                    ChatId=chatId;
                    for(int i=0;i < unseenChats .size();i++){
                        if(ChatId.equalsIgnoreCase(unseenChats.get(i))){
                            unseenChats.remove(i);
                            break;
                        }
                    }
                    Persistance.getInstance().setUnseenChats(getApplicationContext(),unseenChats);
                    listenForChanges();
                    Runnable getPage = getFirstPage();
                    Thread listener = new Thread(getPage);
                    listener.start();
                } else {
                    chatLoading.setVisibility(View.INVISIBLE);
                    createChat=true;
                    Message firstMessage = new Message();
                    firstMessage.otherUserName = getIntent().getStringExtra("otherParticipantName");
                    firstMessage.otherUserImage = getIntent().getStringArrayListExtra("otherParticipantImage").get(0);
                    firstMessage.setTopImage = true;
                    messageList.add(0, firstMessage);
                    setAdapter();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.chat_activity);

        final Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
        final Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.ttf");

        RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.tool_bar);
        TextView title = (TextView)toolbar.findViewById(R.id.titleText);
        title.setTypeface(typeFace);

        sendButton = (Button)findViewById(R.id.sendButton);
        messageInput = (EditText)findViewById(R.id.messageInput);
        chatLoading = (ProgressBar)findViewById(R.id.chatLoading);
        backButton = (RelativeLayout) findViewById(R.id.backButton);
        titleText = (TextView) findViewById(R.id.titleText);
        if((getIntent().getStringExtra("otherParticipantName") != null)) {
            titleText.setText(getIntent().getStringExtra("otherParticipantName").substring(0, getIntent().getStringExtra("otherParticipantName").length() - 1));
        }
        //checkChatExistence(getIntent().getStringExtra("UserId"),this);
        isOnChat1to1 = true;
        createChat = false;

        unseenChats=Persistance.getInstance().getUnseenChats(getApplicationContext());

        ChatId=getIntent().getStringExtra("chatId");
        if(getIntent().getStringArrayListExtra("otherParticipantId") != null) {
            otherUsersId = getIntent().getStringArrayListExtra("otherParticipantId");
        }
        if(getIntent().getStringArrayListExtra("otherParticipantImage") != null) {
            otherUsersImage = getIntent().getStringArrayListExtra("otherParticipantImage");
        }
        isGroupChat=getIntent().getIntExtra("groupChat",0);

        for(int i=0;i < unseenChats .size();i++){
            if(ChatId.equalsIgnoreCase(unseenChats.get(i))){
                unseenChats.remove(i);
                break;
            }
        }
        Persistance.getInstance().setUnseenChats(getApplicationContext(),unseenChats);
        if(ChatId.equalsIgnoreCase("isNot")){
            checkChatExistence(getIntent().getStringExtra("UserId"),this);
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isGroupChat == 0) {
                    isOnChat1to1 = false;
                    finish();
                }
                else{
                    finish();
                }
            }
        });

        messageAdapter = new MessageAdapter(messageList, getApplicationContext(), this, getResources(), ChatActivity.this);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ChatId.equalsIgnoreCase("isNot")) {

                    if (messageInput.getText().length() > 0) {
                        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("chats").child(ChatId);

                        HashMap<String, Object> values = new HashMap<String, Object>();
                        values.put("author_id", Persistance.getInstance().getUserInfo(ChatActivity.this).id);
                        values.put("date", Double.valueOf(System.currentTimeMillis() / 1000));
                        values.put("message", messageInput.getText().toString());
                        firebaseRef.child("last_message_date").setValue(System.currentTimeMillis() / 1000);
                        firebaseRef.child("messages").push().setValue(values);
                        messageInput.setText("");
                    }
                }
                if(createChat && messageInput.getText().length() > 0){
                    final DatabaseReference chatNode = FirebaseDatabase.getInstance().getReference().child("chats");

                    HashMap<String,Object> values=new HashMap<String, Object>();
                    HashMap<String,Object> usersMap=new HashMap<String, Object>();
                    HashMap<String,Object> myValue=new HashMap<String, Object>();
                    HashMap<String,Object> otherValue=new HashMap<String, Object>();
                    myValue.put("image",Persistance.getInstance().getUserInfo(ChatActivity.this).profileImage);
                    myValue.put("name",Persistance.getInstance().getUserInfo(ChatActivity.this).firstName+" "+Persistance.getInstance().getUserInfo(ChatActivity.this).lastName);
                    otherValue.put("image",getIntent().getStringExtra("otherParticipantImage"));
                    otherValue.put("name",getIntent().getStringExtra("otherParticipantName").substring(0,getIntent().getStringExtra("otherParticipantName").length()-1));
                    usersMap.put(Persistance.getInstance().getUserInfo(ChatActivity.this).id,myValue);
                    usersMap.put(getIntent().getStringExtra("UserId"),otherValue);
                    values.put("users",usersMap);
                    String chat =chatNode.push().getKey();
                    ChatId=chat;
                    chatNode.child(chat).setValue(values);
                    FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(ChatActivity.this).id).child("talkbuddies").child(getIntent().getStringExtra("UserId")).setValue(chat);
                    FirebaseDatabase.getInstance().getReference().child("users").child(getIntent().getStringExtra("UserId")).child("talkbuddies").child(Persistance.getInstance().getUserInfo(ChatActivity.this).id).setValue(chat);

                    listenForChangesWhenCreated();
                    final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("chats").child(ChatId);
                    HashMap<String, String> valuesCreate = new HashMap<String, String>();
                    valuesCreate.put("author_id", Persistance.getInstance().getUserInfo(ChatActivity.this).id);
                    valuesCreate.put("date", String.valueOf(System.currentTimeMillis() / 1000));
                    valuesCreate.put("message", messageInput.getText().toString());
                    firebaseRef.child("last_message_date").setValue(System.currentTimeMillis() / 1000);
                    firebaseRef.child("messages").push().setValue(valuesCreate);
                    messageInput.setText("");
                }
            }
        });
        RelativeLayout goToUserProfile=(RelativeLayout)findViewById(R.id.goToUserProfile);

        if(!ChatId.equalsIgnoreCase("isNot") && isGroupChat == 0) {
            listenForChanges();
            Runnable getPage = getFirstPage();
            Thread listener = new Thread(getPage);
            listener.start();
            ViewGroup.LayoutParams params = goToUserProfile.getLayoutParams();
            params.height =(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
            params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
            goToUserProfile.setLayoutParams(params);
            goToUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChatActivity.this, UserProfileActivity.class);
                    intent.putExtra("UserId", otherUsersId.get(0));
                    startActivity(intent);
                }
            });

        }
        if(!ChatId.equalsIgnoreCase("isNot") && isGroupChat == 1){
            if(otherUsersId != null) {
                otherUsersId.clear();
            }
            if(otherUsersImage != null) {
                otherUsersImage.clear();
            }

            final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(ChatActivity.this).id).child("chats").child(ChatId).child("users");
            firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String names="";
                    int counterOfNames = 0;
                    for(DataSnapshot users : dataSnapshot.getChildren()){
                        if(!users.getKey().equalsIgnoreCase(Persistance.getInstance().getUserInfo(ChatActivity.this).id)){
                            if(users.hasChild("image")) {
                                otherUsersImage.add(users.child("image").getValue().toString());
                            }else{
                                otherUsersImage.add("");
                            }
                            otherUsersId.add(users.getKey().toString());
                            if(counterOfNames == 0) {
                                if (users.hasChild("name")) {
                                    names += users.child("name").getValue().toString() + ",";
                                    counterOfNames++;
                                } else {
                                    names += "Unknown" + ",";
                                    counterOfNames++;
                                }
                            }else if(counterOfNames == 1){
                                if (users.hasChild("name")) {
                                    if(dataSnapshot.getChildrenCount() > 3) {
                                        names += users.child("name").getValue().toString() + "....";
                                    }else{
                                        names += users.child("name").getValue().toString() + ",";
                                    }
                                    counterOfNames++;
                                } else {
                                    if(dataSnapshot.getChildrenCount() > 3) {
                                        names += "Unknown" + "....";
                                    }else{
                                        names += "Unknown" + ",";
                                    }
                                    counterOfNames++;
                                }
                            }

                        }
                    }if(names.length() > 0) {
                        titleText.setText(names.substring(0, names.length() - 1));
                    }
                    listenForChanges();
                    Runnable getPage = getFirstPage();
                    Thread listener = new Thread(getPage);
                    listener.start();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        findViewById(R.id.internetRefresh).setAlpha(0);

        final float scale = super.getResources().getDisplayMetrics().density;
        this.dp56 = (int) (56 * scale + 0.5f);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (!connected) {
                    onInternetLost();
                } else {
                    onInternetRefresh();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void onInternetRefresh() {
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.noInternetLayout);
        ll.getLayoutParams().height = 0;
        ll.setLayoutParams(ll.getLayoutParams());
    }

    private void onInternetLost() {
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.noInternetLayout);

        ll.getLayoutParams().height = this.dp56;
        ll.setLayoutParams(ll.getLayoutParams());
    }

    public void listenForChanges(){
        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("chats").child(ChatId).child("messages");
        firebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (isFirstTimeSetMessage && isOnChat1to1 && !ChatId.equalsIgnoreCase("isNot") && !createChat) {
                    Message message = new Message();
                    message.authorId = dataSnapshot.child("author_id").getValue().toString();
                    message.date = Double.valueOf(dataSnapshot.child("date").getValue().toString());
                    message.message = dataSnapshot.child("message").getValue().toString();
                    message.messageId = dataSnapshot.getKey();
                    for(int i=0;i < otherUsersId.size();i++) {
                        if(message.authorId.equalsIgnoreCase(otherUsersId.get(i))) {
                            message.otherUserImage =otherUsersImage.get(i);
                        }
                    }
                    messageList.add(message);
                    DatabaseReference seenRef=FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(ChatActivity.this).id).child("chats").child(ChatId).child("seen");
                    seenRef.setValue(message.messageId);
                    needToScroll=true;
                    setAdapter();
                }
                synchronized (waitForChatLoading) {
                    try {
                        waitForChatLoading.notify(); // ready, notify thread to get data from firebase
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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



    }

    public Runnable getFirstPage(){
        {
            return new Runnable() {
                public void run() {
                synchronized (waitForChatLoading) {
                    try {
                        waitForChatLoading.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(ChatActivity.this).id).child("chats").child(ChatId).child("messages");
                Query query = firebaseRef.limitToLast(21);
                numberOfChatsPage = 1;
                messageList.clear();
                if (!ChatId.equalsIgnoreCase("isNot")) {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            numberOfTotalChatsArrived = (int) dataSnapshot.getChildrenCount();
                            for (DataSnapshot messages : dataSnapshot.getChildren()) {
                                Message message = new Message();
                                message.authorId = messages.child("author_id").getValue().toString();
                                message.date = Double.valueOf(messages.child("date").getValue().toString());
                                message.message = messages.child("message").getValue().toString();
                                message.messageId = messages.getKey();
                                for(int i=0;i < otherUsersId.size();i++) {
                                    if(message.authorId.equalsIgnoreCase(otherUsersId.get(i))) {
                                        message.otherUserImage =otherUsersImage.get(i);
                                    }
                                }


                                if (counterOfChats > 0 || numberOfTotalChatsArrived < 21) {
                                    messageList.add(message);

                                    chatLoading.setAlpha(0f);
                                    counterOfChats++;
                                } else {
                                    counterOfChats++;
                                    keyOfLastChat = message.messageId;
                                    valueOfLastChat = message.date;
                                }
                                if (numberOfTotalChatsArrived < 21) {
                                    if (numberOfTotalChatsArrived == counterOfChats) {

                                        Message firstMessage = new Message();
                                        if(otherUsersId.size() == 1 && isGroupChat == 0) {
                                            firstMessage.otherUserName = getIntent().getStringExtra("otherParticipantName");
                                            firstMessage.otherUserImage =otherUsersImage.get(0);
                                            firstMessage.setTopImage = true;
                                            messageList.add(0, firstMessage);
                                        }else{
                                            firstMessage.otherUserName = "";
                                            Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.ph_group);
                                            firstMessage.otherUserImage =encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
                                            firstMessage.setTopImage = true;
                                            messageList.add(0, firstMessage);
                                        }
                                            isLastPage = true;

                                    }

                                }
                                if (numberOfTotalChatsArrived == counterOfChats) {
                                    DatabaseReference seenRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(ChatActivity.this).id).child("chats").child(ChatId).child("seen");
                                    seenRef.setValue(messageList.get(messageList.size() - 1).messageId);
                                    setAdapter();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                }
            };

        }
    }


    public void listenForChangesWhenCreated(){

        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("chats").child(ChatId).child("messages");
        firebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(isOnChat1to1 && createChat) {
                    Message message = new Message();
                    message.authorId = dataSnapshot.child("author_id").getValue().toString();
                    message.date = Double.valueOf(dataSnapshot.child("date").getValue().toString());
                    message.message = dataSnapshot.child("message").getValue().toString();
                    message.messageId = dataSnapshot.getKey();
                    message.otherUserImage = getIntent().getStringExtra("otherParticipantImage");
                    messageList.add(message);
                    DatabaseReference seenRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(ChatActivity.this).id).child("chats").child(ChatId).child("seen");
                    seenRef.setValue(message.messageId);
                    needToScroll = true;
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

    }

    public void getPage() {
        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(ChatActivity.this).id).child("chats").child(ChatId).child("messages");
        Query query = firebaseRef.endAt(null, keyOfLastChat).limitToLast(21);
        counterOfChats = 0;
        query.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberOfTotalChatsArrived = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot messages : dataSnapshot.getChildren()) {
                    Message message = new Message();
                    message.authorId = messages.child("author_id").getValue().toString();
                    message.date = Double.valueOf(messages.child("date").getValue().toString());
                    message.message = messages.child("message").getValue().toString();
                    message.messageId = messages.getKey();
                    for(int i=0;i < otherUsersId.size();i++) {
                        if(message.authorId.equalsIgnoreCase(otherUsersId.get(i))) {
                            message.otherUserImage =otherUsersImage.get(i);
                            break;
                        }
                    }


                    if(counterOfChats > 0 || numberOfTotalChatsArrived < 21) {
                        if(numberOfTotalChatsArrived < 21){
                            messageList.add(counterOfChats, message);
                        }else {
                            messageList.add(counterOfChats-1, message);
                        }

                        counterOfChats++;
                    }
                    else{
                        counterOfChats++;
                        keyOfLastChat = message.messageId;
                        valueOfLastChat = message.date;
                    }
                    if (numberOfTotalChatsArrived < 21) {
                        if (numberOfTotalChatsArrived == counterOfChats) {

                            Message firstMessage=new Message();
                            if(otherUsersId.size() == 1 && isGroupChat == 0) {
                                firstMessage.otherUserName = getIntent().getStringExtra("otherParticipantName");
                                firstMessage.otherUserImage =otherUsersImage.get(0);
                                firstMessage.setTopImage = true;
                                messageList.add(0, firstMessage);
                            }else{
                                firstMessage.otherUserName = "";
                                Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.ph_group);
                                firstMessage.otherUserImage =encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
                                firstMessage.setTopImage = true;
                                messageList.add(0, firstMessage);
                            }
                            isLastPage = true;
                        }

                    }
                    if(numberOfTotalChatsArrived == counterOfChats){
                        needToScroll=false;
                        nrOfPage++;
                        setAdapter();

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





            messageListView.post(new Runnable() {
                @Override
                public void run() {
                    if (needToScroll) {

                        // Select the last row so it will scroll into view...
                        messageListView.smoothScrollToPosition(messageAdapter.getCount() - 1);
                    }
                    else{
                        messageListView.setSelection(messageAdapter.getCount() -nrOfPage*20);
                    }
                    messageListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
                }
            });







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
        if(isGroupChat == 0) {
            /*Intent intent = new Intent(ChatActivity.this, Main.class);
            intent.putExtra("setChatTab", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
            isOnChat1to1 = false;
            finish();
        }
        else{
            finish();
        }
    }
}
