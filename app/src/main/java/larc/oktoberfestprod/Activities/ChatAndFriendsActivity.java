package larc.oktoberfestprod.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import larc.oktoberfestprod.Adapters.ChatAndFriends.ConversationsAdapter;
import larc.oktoberfestprod.Adapters.ChatAndFriends.FriendsAdapter;
import larc.oktoberfestprod.Controller.HTTPResponseController;
import larc.oktoberfestprod.Controller.Persistance;
import larc.oktoberfestprod.R;
import larc.oktoberfestprod.Utils.Chat;
import larc.oktoberfestprod.Utils.ChatAndFriends.ChatAndFriendsViewPagerAdapter;
import larc.oktoberfestprod.Utils.Friend;
import larc.oktoberfestprod.Utils.ui.SlidingTabLayout;

import static larc.oktoberfestprod.Activities.ActivitiesActivity.deleteCachedInfo;
import static larc.oktoberfestprod.Activities.ChatActivity.isOnChat1to1;
import static larc.oktoberfestprod.Activities.Main.bottomBar;

/**
 * Created by ancuta on 8/18/2017.
 */

public class ChatAndFriendsActivity extends Fragment implements Response.ErrorListener {
    ViewPager pager;
    private Context mContext;
    ChatAndFriendsViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    ConversationsAdapter chatAdapter;
    public static FriendsAdapter friendsAdapter;
    public static ArrayList<Friend> friends = new ArrayList<>();
    private View v;
    CharSequence Titles[] = {"CONVERSATIONS", "FOLLOWING"};
    int Numboftabs = 2;
    ArrayList<Chat> chatList = new ArrayList<>();
    static public ChatAndFriendsActivity currentFragment;
    FragmentActivity activity;
    public static ListView chatListView;
    public static ListView friendsListView;
    public Boolean isFirstTimeSetChat = false;
    public static Boolean isFirstTimeSetFriends = false;
    public static ArrayList<CountDownTimer> threadsList = new ArrayList<>();
    public static ChatAndFriendsActivity currentChatAndFriends;
    public int counterOfChats = 0;
    public String keyOfLastChat;
    public Double valueOfLastChat;
    public int numberOfChatsPage;
    ProgressBar progressBarChats;
    public static ProgressBar progressBarFriends;
    int numberOfTotalChatsArrived;
    Boolean isLastPage = false;
    Boolean addedSwipe = false;
    Boolean addedSwipeFriends = false;
    public static Boolean isOnChatPage = true;
    String lastMessageSeen;
    Boolean isAlreadyProcess = false;
    public static int NumberOfRefreshFriends = 0;
    private int dp56;
    ProgressBar chatLoading;
    public boolean shouldRequestPage = false;

    public ChatAndFriendsActivity() {
        currentFragment = this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = inflater.getContext();
        v = inflater.inflate(R.layout.chat_and_friends_activity, container, false);
        while (activity == null) {
            activity = getActivity();
        }
        currentChatAndFriends = this;
        threadsList.clear();
        friends.clear();
        isFirstTimeSetFriends = false;
        isOnChatPage = true;
        NumberOfRefreshFriends = 0;
        isFirstTimeSetChat = false;
        try {

            super.onCreate(savedInstanceState);
            adapter = new ChatAndFriendsViewPagerAdapter(activity.getSupportFragmentManager(), Titles, Numboftabs);

            // Assigning ViewPager View and setting the adapter
            pager = (ViewPager) v.findViewById(R.id.pager);
            pager.setAdapter(adapter);

            // Assiging the Sliding Tab Layout View
            tabs = (SlidingTabLayout) v.findViewById(R.id.tabs);
            tabs.setDistributeEvenly(false); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(pager);
            chatAdapter = new ConversationsAdapter(chatList, activity.getApplicationContext(), activity, getResources(), currentFragment);


            final float scale = mContext.getResources().getDisplayMetrics().density;
            this.dp56 = (int) (56 * scale + 0.5f);


            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (!connected) {
                        onInternetLost();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(activity).id).child("chats");
            firebaseRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    /*if(!isOnChatPage && !isOnChat1to1) {

                        String names = "";
                        for (DataSnapshot users : dataSnapshot.child("users").getChildren()) {
                            if (!users.getKey().equalsIgnoreCase(Persistance.getInstance().getUserInfo(activity).id)) {
                                names += users.child("name").getValue().toString() + ",";
                            }

                        }
                    }*/
                    if (dataSnapshot.hasChild("last_message_date") && isFirstTimeSetChat && isOnChatPage) {
                        final Chat chat = new Chat();
                        chat.chatId = dataSnapshot.getKey();
                        if (dataSnapshot.hasChild("event_id")) {
                            chat.eventId = dataSnapshot.child("event_id").getValue().toString();
                        }
                        String names;
                        if (chat.eventId != null) {
                            names = "Group:";
                        } else {
                            names = "";
                        }
                        int counterOfNames=0;
                        for (DataSnapshot users : dataSnapshot.child("users").getChildren()) {

                            if (!users.getKey().equalsIgnoreCase(Persistance.getInstance().getUserInfo(activity).id)) {
                                if(counterOfNames == 0) {
                                    if (users.hasChild("name") && users.child("name").getValue().toString().trim().compareToIgnoreCase("") != 0) {
                                        names += users.child("name").getValue().toString() + ",";
                                        counterOfNames++;
                                    } else {
                                        names += "Unknown" + ",";
                                        counterOfNames++;
                                    }
                                }else if(counterOfNames == 1){
                                    if (users.hasChild("name") && users.child("name").getValue().toString().trim().compareToIgnoreCase("") != 0) {
                                        if(dataSnapshot.child("users").getChildrenCount() > 3) {
                                            names += users.child("name").getValue().toString() + "....";
                                        }else{
                                            names += users.child("name").getValue().toString() + ",";
                                        }
                                        counterOfNames++;
                                    } else {
                                        if(dataSnapshot.child("users").getChildrenCount() > 3) {
                                            names += "Unknown" + "....";
                                        }else{
                                            names += "Unknown" + ",";
                                        }
                                        counterOfNames++;
                                    }
                                }
                                if (users.hasChild("image")) {
                                    chat.image.add(users.child("image").getValue().toString());
                                } else {
                                    chat.image.add("");
                                }
                                chat.otherParticipantId.add(users.getKey().toString());
                            }
                        }
                        chat.participantName = names;

                        chat.lastMessageTime = Double.valueOf(dataSnapshot.child("last_message_date").getValue().toString());
                        DatabaseReference lastMessageRef = dataSnapshot.child("messages").getRef();
                        Query lastMessage = lastMessageRef.orderByKey().limitToLast(1);
                        lastMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    chat.lastMessage = child.child("message").getValue().toString();
                                    chat.lastMessageId = child.getKey().toString();
                                }
                                chatList.add(0, chat);
                                setAdapter();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {
                        if (dataSnapshot.hasChild("last_message_date") && isOnChatPage && !isAlreadyProcess) {
                            isAlreadyProcess = true;
                            for (int i = 0; i < chatList.size(); i++) {
                                if (dataSnapshot.getKey().equalsIgnoreCase(chatList.get(i).chatId)) {
                                    chatList.remove(i);
                                    if (chatList.size() > 0 && threadsList.size() > i) {
                                        threadsList.get(i).cancel();
                                        threadsList.remove(i);
                                        break;
                                    }
                                }
                            }

                            final Chat chat = new Chat();
                            chat.chatId = dataSnapshot.getKey();
                            if (dataSnapshot.hasChild("event_id")) {
                                chat.eventId = dataSnapshot.child("event_id").getValue().toString();
                            }
                            String names;
                            if (chat.eventId != null) {
                                names = "Group:";
                            } else {
                                names = "";
                            }
                            int counterOfNames=0;
                            for (DataSnapshot users : dataSnapshot.child("users").getChildren()) {

                                if (!users.getKey().equalsIgnoreCase(Persistance.getInstance().getUserInfo(activity).id)) {
                                    if(counterOfNames == 0) {
                                        if (users.hasChild("name") && users.child("name").getValue().toString().trim().compareToIgnoreCase("") != 0) {
                                            names += users.child("name").getValue().toString() + ",";
                                            counterOfNames++;
                                        } else {
                                            names += "Unknown" + ",";
                                            counterOfNames++;
                                        }
                                    }else if(counterOfNames == 1){
                                        if (users.hasChild("name") && users.child("name").getValue().toString().trim().compareToIgnoreCase("") != 0) {
                                            if(dataSnapshot.child("users").getChildrenCount() > 3) {
                                                names += users.child("name").getValue().toString() + "....";
                                            }else{
                                                names += users.child("name").getValue().toString() + ",";
                                            }
                                            counterOfNames++;
                                        } else {
                                            if(dataSnapshot.child("users").getChildrenCount() > 3) {
                                                names += "Unknown" + "....";
                                            }else{
                                                names += "Unknown" + ",";
                                            }
                                            counterOfNames++;
                                        }
                                    }
                                    if (users.hasChild("image")) {
                                        chat.image.add(users.child("image").getValue().toString());
                                    } else {
                                        chat.image.add("");
                                    }
                                    chat.otherParticipantId.add(users.getKey().toString());
                                }
                            }
                            chat.participantName = names;

                            chat.lastMessageTime = Double.valueOf(dataSnapshot.child("last_message_date").getValue().toString());
                            DatabaseReference lastMessageRef = dataSnapshot.child("messages").getRef();
                            Query lastMessage = lastMessageRef.orderByKey().limitToLast(1);
                            lastMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        chat.lastMessage = child.child("message").getValue().toString();
                                        chat.lastMessageId = child.getKey().toString();
                                    }
                                    if (chatList.size() > 0 && !chatList.get(0).chatId.equals(chat.chatId)) {
                                        chatList.add(0, chat);
                                        setAdapter();
                                        isAlreadyProcess = false;
                                    } else
                                        if (chatList.size() == 0) {
                                            chatList.add(0, chat);
                                            setAdapter();
                                            isAlreadyProcess = false;
                                        }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

            getFirstPage();


            getFriends("0");
            friendsAdapter = new FriendsAdapter(friends, activity, activity, getResources(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    public void setAdapter() {
        try {
            chatAdapter.notifyDataSetChanged();
            chatLoading = (ProgressBar) v.findViewById(R.id.chatLoading);
            progressBarChats = (ProgressBar) v.findViewById(R.id.progressBarChats);
            progressBarChats.setAlpha(0f);

            chatLoading.setAlpha(0f);
            final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refreshChat);
            chatListView = (ListView) v.findViewById(R.id.chat_listView);


            if (!isFirstTimeSetChat) {
                chatListView.setAdapter(chatAdapter);
                isFirstTimeSetChat = true;
            }
            TextView noConversationTV = (TextView) v.findViewById(R.id.noConversationTV);
            TextView joinActivitiesTV = (TextView) v.findViewById(R.id.joinActivitiesTV);
            Button discoverActivitiesButton = (Button) v.findViewById(R.id.discoverActivitiesButton);
            ImageView chatImage = (ImageView) v.findViewById(R.id.chatImage);
            if (chatList.size() == 0) {
                noConversationTV.setVisibility(View.VISIBLE);
                joinActivitiesTV.setVisibility(View.VISIBLE);
                discoverActivitiesButton.setVisibility(View.VISIBLE);
                discoverActivitiesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomBar.setDefaultTab(R.id.tab_activities);
                    }
                });
                chatImage.setVisibility(View.VISIBLE);
                chatListView.setVisibility(View.INVISIBLE);
            } else {
                noConversationTV.setVisibility(View.INVISIBLE);
                joinActivitiesTV.setVisibility(View.INVISIBLE);
                discoverActivitiesButton.setVisibility(View.INVISIBLE);
                chatImage.setVisibility(View.INVISIBLE);
                chatListView.setVisibility(View.VISIBLE);
            }
            shouldRequestPage=true;
            if(isLastPage){
                ArrayList<String> unseenChats=new ArrayList<>();
                for(int i=0;i<chatList.size();i++){
                   if(!chatList.get(i).lastMessageId.equals(chatList.get(i).lastMessageSeen)) {
                       unseenChats.add(chatList.get(i).chatId);
                   }
                }
                Persistance.getInstance().setUnseenChats(activity,unseenChats);
            }
            if (chatListView != null && shouldRequestPage) {
                chatListView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(final View v, MotionEvent event) {
                        if (v != null && chatListView.getChildCount() > 0) {
                            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {


                                if (chatListView.getLastVisiblePosition() == chatListView.getAdapter().getCount() - 1 &&
                                        chatListView.getChildAt(chatListView.getChildCount() - 1).getBottom() <= chatListView.getHeight()) {

                                    // mSwipeRefreshLayout1.setRefreshing(true);
                                    if (!isLastPage) {
                                        progressBarChats.setAlpha(1f);
                                        getPage();
                                    }
                                }
                            }
                        }
                        return false;
                    }
                });
            }
            if (!addedSwipe) {
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        for (int i = 0; i < threadsList.size(); i++) {
                            threadsList.get(i).cancel();
                        }
                        threadsList.clear();
                        counterOfChats = 0;
                        keyOfLastChat = null;
                        valueOfLastChat = 0.0;
                        numberOfChatsPage = 0;
                        numberOfTotalChatsArrived = 0;
                        isLastPage = false;
                        addedSwipe = false;
                        mSwipeRefreshLayout.setRefreshing(false);
                        getFirstPage();
                    }
                });
                addedSwipe = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFriendsAdapter() {
        try {
            friendsAdapter.notifyDataSetChanged();
            final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refreshFriends);
            friendsListView = (ListView) v.findViewById(R.id.friends_listView);
            progressBarFriends = (ProgressBar) v.findViewById(R.id.progressBarFriends);
            progressBarFriends.setAlpha(0f);

            if (!isFirstTimeSetFriends) {
                friendsListView.setAdapter(friendsAdapter);
                isFirstTimeSetFriends = true;
            }

            TextView noFriendsTV = (TextView) v.findViewById(R.id.noFriendsTV);
            TextView joinActivitiesFriendsTV = (TextView) v.findViewById(R.id.joinActivitiesFriendsTV);
            Button discoverActivitiesFriendsButton = (Button) v.findViewById(R.id.discoverActivitiesFriendsButton);
            ImageView friendsImage = (ImageView) v.findViewById(R.id.friendsImage);

            if (friends.size() == 0) {
                noFriendsTV.setVisibility(View.VISIBLE);
                joinActivitiesFriendsTV.setVisibility(View.VISIBLE);
                discoverActivitiesFriendsButton.setVisibility(View.VISIBLE);
                discoverActivitiesFriendsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomBar.setDefaultTab(R.id.tab_activities);
                    }
                });
                friendsImage.setVisibility(View.VISIBLE);
            } else {
                noFriendsTV.setVisibility(View.INVISIBLE);
                joinActivitiesFriendsTV.setVisibility(View.INVISIBLE);
                discoverActivitiesFriendsButton.setVisibility(View.INVISIBLE);
                friendsImage.setVisibility(View.INVISIBLE);
            }
            if (friendsListView != null) {
                friendsListView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(final View v, MotionEvent event) {
                        if (v != null && friendsListView.getChildCount() > 0) {
                            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                                if (friendsListView.getLastVisiblePosition() == friendsListView.getAdapter().getCount() - 1 &&
                                        friendsListView.getChildAt(friendsListView.getChildCount() - 1).getBottom() <= friendsListView.getHeight()) {
                                    progressBarFriends.setAlpha(1f);
                                    getFriends(String.valueOf(NumberOfRefreshFriends));
                                }
                            }
                        }
                        return false;
                    }
                });
            }

            if (!addedSwipeFriends) {
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        addedSwipeFriends = false;
                        NumberOfRefreshFriends = 0;
                        mSwipeRefreshLayout.setRefreshing(false);
                        getFriends("0");
                    }
                });
                addedSwipeFriends = true;

                int last = friendsListView.getLastVisiblePosition();
                int count = friendsAdapter.getCount();
                if (last + 1 < count) {
                    friendsListView.smoothScrollToPosition(last + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFirstPage() {
        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(activity).id).child("chats");
        Query query = firebaseRef.orderByChild("last_message_date").limitToLast(16);
        numberOfChatsPage = 1;
        chatList.clear();
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberOfTotalChatsArrived = 0;
                for (DataSnapshot chats : dataSnapshot.getChildren()) {
                    if(chats.hasChild("last_message_date")){
                        numberOfTotalChatsArrived++;
                    }
                }
                for (DataSnapshot chats : dataSnapshot.getChildren()) {
                    if (chats.hasChild("last_message_date")) {
                        final Chat chat = new Chat();
                        chat.chatId = chats.getKey();
                        if (chats.hasChild("event_id")) {
                            chat.eventId = chats.child("event_id").getValue().toString();
                        }
                        String names ="";

                        int counterOfNames=0;
                        if(chats.hasChild("event_info")){
                            // Format date
                            String dateShort = chats.child("event_info").child("date").getValue().toString();
                            long dateShortInt = 0;
                            try {
                                dateShortInt = Long.parseLong(dateShort);
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM  - HH:mm");
                                TimeZone tz = TimeZone.getDefault();
                                formatter.setTimeZone(tz);

                                java.util.Date date = new java.util.Date( dateShortInt * 1000);
                                dateShort = formatter.format(date);
                            }
                            catch (NumberFormatException ex){
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                df.setTimeZone(TimeZone.getTimeZone("UTC"));

                                try {
                                    java.util.Date date = df.parse(dateShort);

                                    SimpleDateFormat formatter = new SimpleDateFormat("dd MMM - HH:mm");
                                    formatter.setTimeZone(TimeZone.getDefault());
                                    dateShort = formatter.format(date);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            // Format sport
                            String sport = chats.child("event_info").child("sport_code").getValue().toString();
                            if(sport.compareToIgnoreCase("OTH") == 0){
                                sport = chats.child("event_info").child("other_sport_name").getValue().toString();
                            }

                            chat.participantName = dateShort + " - " + sport + " ";
                        }
                        else {
                            for (DataSnapshot users : chats.child("users").getChildren()) {

                                if (!users.getKey().equalsIgnoreCase(Persistance.getInstance().getUserInfo(activity).id)) {
                                    if (counterOfNames == 0) {
                                        if (users.hasChild("name") && users.child("name").getValue().toString().trim().compareToIgnoreCase("") != 0) {
                                            names += users.child("name").getValue().toString() + ",";
                                            counterOfNames++;
                                        } else {
                                            names += "Unknown" + ",";
                                            counterOfNames++;
                                        }
                                    } else if (counterOfNames == 1) {
                                        if (users.hasChild("name") && users.child("name").getValue().toString().trim().compareToIgnoreCase("") != 0) {
                                            if (chats.child("users").getChildrenCount() > 3) {
                                                names += users.child("name").getValue().toString() + "....";
                                            } else {
                                                names += users.child("name").getValue().toString() + ",";
                                            }
                                            counterOfNames++;
                                        } else {
                                            if (chats.child("users").getChildrenCount() > 3) {
                                                names += "Unknown" + "....";
                                            } else {
                                                names += "Unknown" + ",";
                                            }
                                            counterOfNames++;
                                        }
                                    }
                                    if (users.hasChild("image")) {
                                        chat.image.add(users.child("image").getValue().toString());
                                    } else {
                                        chat.image.add("");
                                    }
                                    chat.otherParticipantId.add(users.getKey().toString());
                                }
                            }
                            if (counterOfNames == 0) {
                                chat.participantName = "No participants in group ";
                            } else {
                                if (counterOfNames == 1 && chat.eventId == null) {
                                    chat.participantName = names;
                                } else {
                                    // is group
                                    chat.participantName = "Group: " + names;
                                }
                            }
                        }

                        if (chats.hasChild("seen")) {
                            chat.lastMessageSeen = chats.child("seen").getValue().toString();
                        }
                        chat.lastMessageTime = Double.valueOf(chats.child("last_message_date").getValue().toString());
                        DatabaseReference lastMessageRef = chats.child("messages").getRef();
                        Query lastMessage = lastMessageRef.orderByKey().limitToLast(1);
                        lastMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    chat.lastMessage = child.child("message").getValue().toString();
                                    chat.lastMessageId = child.getKey().toString();
                                }
                                if (counterOfChats > 0 || numberOfTotalChatsArrived < 16) {
                                    chatList.add(0, chat);
                                    counterOfChats++;
                                } else {
                                    counterOfChats++;
                                    keyOfLastChat = chat.chatId;
                                    valueOfLastChat = chat.lastMessageTime;
                                }
                                if (numberOfTotalChatsArrived < 16) {
                                    if (numberOfTotalChatsArrived == counterOfChats) {
                                        isLastPage = true;
                                    }
                                }
                                if (numberOfTotalChatsArrived == counterOfChats) {
                                    setAdapter();
                                    Persistance.getInstance().setConversation(activity,chatList);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else{

                        if (numberOfTotalChatsArrived == counterOfChats) {
                            isLastPage = true;
                            setAdapter();
                        }
                    }
                }
                if(numberOfTotalChatsArrived == 0){
                    setAdapter();
                    isLastPage=true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Cancel", databaseError.toString());
            }
        });
    }

    public void getPage() {
        if (shouldRequestPage) {
            shouldRequestPage = false;
            final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(activity).id).child("chats");
            Query query = firebaseRef.orderByChild("last_message_date").limitToLast(16).endAt(valueOfLastChat, keyOfLastChat);
            counterOfChats = 0;
            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    numberOfTotalChatsArrived = 0;
                    for (DataSnapshot chats : dataSnapshot.getChildren()) {
                        if(chats.hasChild("last_message_date")){
                            numberOfTotalChatsArrived++;
                        }
                    }
                    for (DataSnapshot chats : dataSnapshot.getChildren()) {
                        if (chats.hasChild("last_message_date")) {
                            final Chat chat = new Chat();
                            chat.chatId = chats.getKey();
                            if (chats.hasChild("event_id")) {
                                chat.eventId = chats.child("event_id").getValue().toString();
                            }

                            if(chats.hasChild("event_info")){
                                // Format date
                                String dateShort = chats.child("event_info").child("date").getValue().toString();
                                long dateShortInt = 0;
                                try {
                                    dateShortInt = Long.parseLong(dateShort);
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd MMM  - HH:mm");
                                    TimeZone tz = TimeZone.getDefault();
                                    formatter.setTimeZone(tz);

                                    java.util.Date date = new java.util.Date( dateShortInt * 1000);
                                    dateShort = formatter.format(date);
                                }
                                catch (NumberFormatException ex){
                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    df.setTimeZone(TimeZone.getTimeZone("UTC"));

                                    try {
                                        java.util.Date date = df.parse(dateShort);

                                        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM  - HH:mm");
                                        TimeZone tz = TimeZone.getDefault();
                                        formatter.setTimeZone(tz);

                                        dateShort = formatter.format(date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                // Format sport
                                String sport = chats.child("event_info").child("sport_code").getValue().toString();
                                if(sport.compareToIgnoreCase("OTH") == 0){
                                    sport = chats.child("event_info").child("other_sport_name").getValue().toString();
                                }

                                chat.participantName = dateShort + " - " + sport + " ";
                            }
                            else {
                                String names;
                                if (chat.eventId != null) {
                                    names = "Group:";
                                } else {
                                    names = "";
                                }
                                int counterOfNames = 0;
                                for (DataSnapshot users : chats.child("users").getChildren()) {
                                    if (!users.getKey().equalsIgnoreCase(Persistance.getInstance().getUserInfo(activity).id)) {
                                        if (counterOfNames == 0) {
                                            if (users.hasChild("name") && users.child("name").getValue().toString().trim().compareToIgnoreCase("") != 0) {
                                                names += users.child("name").getValue().toString() + ",";
                                                counterOfNames++;
                                            } else {
                                                names += "Unknown" + ",";
                                                counterOfNames++;
                                            }
                                        } else if (counterOfNames == 1) {
                                            if (users.hasChild("name") && users.child("name").getValue().toString().trim().compareToIgnoreCase("") != 0) {
                                                if (chats.child("users").getChildrenCount() > 3) {
                                                    names += users.child("name").getValue().toString() + "....";
                                                } else {
                                                    names += users.child("name").getValue().toString() + ",";
                                                }
                                                counterOfNames++;
                                            } else {
                                                if (chats.child("users").getChildrenCount() > 3) {
                                                    names += "Unknown" + "....";
                                                } else {
                                                    names += "Unknown" + ",";
                                                }
                                                counterOfNames++;
                                            }
                                        }

                                        if (users.hasChild("image")) {
                                            chat.image.add(users.child("image").getValue().toString());
                                        }
                                        chat.otherParticipantId.add(users.getKey().toString());
                                    }
                                }
                                chat.participantName = names;
                            }

                            if (chats.hasChild("seen")) {
                                chat.lastMessageSeen = chats.child("seen").getValue().toString();
                            }
                                chat.lastMessageTime = Double.valueOf(chats.child("last_message_date").getValue().toString());
                                DatabaseReference lastMessageRef = chats.child("messages").getRef();
                                Query lastMessage = lastMessageRef.orderByKey().limitToLast(1);
                                lastMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            chat.lastMessage = child.child("message").getValue().toString();
                                            chat.lastMessageId = child.getKey().toString();
                                        }
                                        if (counterOfChats > 0 || numberOfTotalChatsArrived < 16) {
                                            chatList.add(numberOfChatsPage * 15, chat);
                                            counterOfChats++;
                                        } else {
                                            counterOfChats++;
                                            keyOfLastChat = chat.chatId;
                                            valueOfLastChat = chat.lastMessageTime;
                                        }
                                        if (counterOfChats == 16) {
                                            numberOfChatsPage++;
                                        }
                                        if (numberOfTotalChatsArrived < 16) {
                                            if (numberOfTotalChatsArrived == counterOfChats) {
                                                isLastPage = true;
                                            }
                                        }
                                        if (numberOfTotalChatsArrived == counterOfChats) {
                                            setAdapter();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });


                    }else{
                            if (numberOfTotalChatsArrived == counterOfChats) {
                                isLastPage = true;
                                setAdapter();
                            }

                    }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }


    public void getFriends(String pageNumber) {
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> urlParams = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);
        urlParams.put("userId", Persistance.getInstance().getUserInfo(activity).id);
        urlParams.put("pageNumber", pageNumber);

        HTTPResponseController.getInstance().getFriends(params, headers, activity, urlParams, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (chatAdapter != null) chatAdapter.notifyDataSetChanged();
        if (friendsAdapter != null) friendsAdapter.notifyDataSetChanged();
    }

    private void onInternetRefresh() {
        addedSwipeFriends = false;
        NumberOfRefreshFriends = 0;
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refreshFriends);
        mSwipeRefreshLayout.setRefreshing(false);
        getFriends("0");

        for (int i = 0; i < threadsList.size(); i++) {
            threadsList.get(i).cancel();
        }
        threadsList.clear();
        counterOfChats = 0;
        keyOfLastChat = null;
        valueOfLastChat = 0.0;
        numberOfChatsPage = 0;
        numberOfTotalChatsArrived = 0;
        isLastPage = false;
        addedSwipe = false;
        mSwipeRefreshLayout.setRefreshing(false);
        getFirstPage();
    }

    private void onInternetLost() {
        v.findViewById(R.id.internetRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setOnClickListener(null);
                onInternetRefresh();
            }
        });
        RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);

        ll.getLayoutParams().height = this.dp56;
        ll.setLayoutParams(ll.getLayoutParams());
    }

    public String trimMessage(String json, String key) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
            if(trimmedString.equalsIgnoreCase("Invalid Auth Key provided.")){
                deleteCachedInfo();
                Intent intent =new Intent(activity,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(error.getMessage() != null) {
            if (error.getMessage().contains("error")) {
                String json = trimMessage(error.getMessage(), "error");
                if (json != null) {
                    Toast.makeText(super.getContext(), json, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(super.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        Log.d("Response", error.toString());
        if (error instanceof NetworkError) {
            this.onInternetLost();
        }
    }
}
