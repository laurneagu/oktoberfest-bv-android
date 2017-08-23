package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import larc.ludiconprod.Adapters.ChatAndFriends.ConversationsAdapter;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Chat;
import larc.ludiconprod.Utils.ChatAndFriends.ChatAndFriendsViewPagerAdapter;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;

/**
 * Created by ancuta on 8/18/2017.
 */

public class ChatAndFriendsActivity extends Fragment {
    ViewPager pager;
    private Context mContext;
    ChatAndFriendsViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    ConversationsAdapter chatAdapter;
    private View v;
    CharSequence Titles[] = {"CONVERSATIONS", "FRIENDS"};
    int Numboftabs = 2;
    ArrayList<Chat> chatList=new ArrayList<>();
    static public ChatAndFriendsActivity currentFragment;
    Activity activity;
    public static ListView chatListView;
    public Boolean isFirstTimeSetChat=false;
    public static ArrayList<CountDownTimer> threadsList=new ArrayList<>();
    public int counterOfChats=0;
    public String keyOfLastChat;
    public int valueOfLastChat;
    public int numberOfChatsPage;
    ProgressBar progressBarChats;
    int numberOfTotalChatsArrived;
    Boolean isLastPage=false;
    Boolean addedSwipe=false;
    public static Boolean isOnChatPage=true;

    public ChatAndFriendsActivity() {
        currentFragment=this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = inflater.getContext();
        v = inflater.inflate(R.layout.chat_and_friends_activity, container, false);
        activity=getActivity();
        threadsList.clear();
        isOnChatPage=true;
        try {

            super.onCreate(savedInstanceState);
            adapter = new ChatAndFriendsViewPagerAdapter(getActivity().getSupportFragmentManager(), Titles, Numboftabs);

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
            chatAdapter = new ConversationsAdapter(chatList, getActivity().getApplicationContext(), getActivity(), getResources(), currentFragment);
            getFirstPage();



            final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(getActivity()).id).child("chats");
            firebaseRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    if (dataSnapshot.hasChild("last_message_date") && isFirstTimeSetChat && isOnChatPage) {
                        final Chat chat = new Chat();
                        chat.chatId = dataSnapshot.getKey();
                        if (dataSnapshot.hasChild("event_id")) {
                            chat.eventId = dataSnapshot.child("event_id").getValue().toString();
                        }
                        String names = "";
                        for (DataSnapshot users : dataSnapshot.child("users").getChildren()) {
                            if (!users.getKey().equalsIgnoreCase(Persistance.getInstance().getUserInfo(activity).id)) {
                                names += users.child("name").getValue().toString() + ",";
                                if (chat.eventId == null) {
                                    chat.image = users.child("image").getValue().toString();
                                }
                            }

                        }
                        chat.participantName = names;

                        chat.lastMessageTime = Integer.valueOf(dataSnapshot.child("last_message_date").getValue().toString());
                        DatabaseReference lastMessageRef = dataSnapshot.child("messages").getRef();
                        Query lastMessage = lastMessageRef.orderByKey().limitToLast(1);
                        lastMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    chat.lastMessage = child.child("message").getValue().toString();
                                }
                                chatList.add(0,chat);
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
                    if (dataSnapshot.hasChild("last_message_date")&& isOnChatPage) {
                        for(int i=0;i < chatList.size();i++){
                            if(dataSnapshot.getKey().equalsIgnoreCase(chatList.get(i).chatId)){
                                    chatList.remove(i);
                                if(chatList.size() > 0) {
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
                        String names = "";
                        for (DataSnapshot users : dataSnapshot.child("users").getChildren()) {
                            if (!users.getKey().equalsIgnoreCase(Persistance.getInstance().getUserInfo(activity).id)) {
                                names += users.child("name").getValue().toString() + ",";
                                if (chat.eventId == null) {
                                    chat.image = users.child("image").getValue().toString();
                                }
                            }

                        }
                        chat.participantName = names;

                        chat.lastMessageTime = Integer.valueOf(dataSnapshot.child("last_message_date").getValue().toString());
                        DatabaseReference lastMessageRef = dataSnapshot.child("messages").getRef();
                        Query lastMessage = lastMessageRef.orderByKey().limitToLast(1);
                        lastMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    chat.lastMessage = child.child("message").getValue().toString();
                                }
                                chatList.add(0,chat);
                                setAdapter();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

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






        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    public void setAdapter(){

        chatAdapter.notifyDataSetChanged();
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refreshChat);
        chatListView = (ListView) v.findViewById(R.id.chat_listView);
        progressBarChats=(ProgressBar)v.findViewById(R.id.progressBarChats);

        if(!isFirstTimeSetChat) {
            chatListView.setAdapter(chatAdapter);
            isFirstTimeSetChat=true;
        }
        TextView noConversationTV=(TextView) v.findViewById(R.id.noConversationTV);
        TextView joinActivitiesTV=(TextView)v.findViewById(R.id.joinActivitiesTV);
        Button discoverActivitiesButton=(Button)v.findViewById(R.id.discoverActivitiesButton);
        ImageView chatImage=(ImageView)v.findViewById(R.id.chatImage);
        if(chatList.size() == 0){
            noConversationTV.setVisibility(View.VISIBLE);
            joinActivitiesTV.setVisibility(View.VISIBLE);
            discoverActivitiesButton.setVisibility(View.VISIBLE);
            chatImage.setVisibility(View.VISIBLE);
        }else{
            noConversationTV.setVisibility(View.INVISIBLE);
            joinActivitiesTV.setVisibility(View.INVISIBLE);
            discoverActivitiesButton.setVisibility(View.INVISIBLE);
            chatImage.setVisibility(View.INVISIBLE);
        }
        if(chatListView != null) {
            chatListView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    if (v != null && chatListView.getChildCount() > 0) {
                        if (event.getAction() == android.view.MotionEvent.ACTION_UP) {


                            if (chatListView.getLastVisiblePosition() == chatListView.getAdapter().getCount() - 1 &&
                                    chatListView.getChildAt(chatListView.getChildCount() - 1).getBottom() <= chatListView.getHeight()) {

                                // mSwipeRefreshLayout1.setRefreshing(true);
                                if(!isLastPage) {
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

                            for(int i=0;i < threadsList.size();i++){
                                threadsList.get(i).cancel();
                            }
                            threadsList.clear();
                    counterOfChats=0;
                    keyOfLastChat=null;
                    valueOfLastChat=0;
                    numberOfChatsPage=0;
                    numberOfTotalChatsArrived=0;
                    isLastPage=false;
                    addedSwipe=false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    getFirstPage();
                }
            });
            addedSwipe = true;
        }
    }

    public void getFirstPage(){
        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(getActivity()).id).child("chats");
        Query query=firebaseRef.orderByChild("last_message_date") .limitToLast(4);
        numberOfChatsPage=1;
        chatList.clear();
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberOfTotalChatsArrived = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot chats : dataSnapshot.getChildren()) {
                    if (chats.hasChild("last_message_date")) {
                        final Chat chat = new Chat();
                        chat.chatId = chats.getKey();
                        if (chats.hasChild("event_id")) {
                            chat.eventId = chats.child("event_id").getValue().toString();
                        }
                        String names = "";
                        for (DataSnapshot users : chats.child("users").getChildren()) {
                            if (!users.getKey().equalsIgnoreCase(Persistance.getInstance().getUserInfo(getActivity()).id)) {
                                names += users.child("name").getValue().toString() + ",";
                                if (chat.eventId == null) {
                                    chat.image = users.child("image").getValue().toString();
                                }
                            }

                        }
                        chat.participantName = names;

                        chat.lastMessageTime = Integer.valueOf(chats.child("last_message_date").getValue().toString());
                        DatabaseReference lastMessageRef = chats.child("messages").getRef();
                        Query lastMessage = lastMessageRef.orderByKey().limitToLast(1);
                        lastMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    chat.lastMessage = child.child("message").getValue().toString();
                                }
                                if(counterOfChats > 0 || numberOfTotalChatsArrived < 4) {
                                    chatList.add(0, chat);
                                    setAdapter();
                                    counterOfChats++;
                                }
                                else{
                                    counterOfChats++;
                                    keyOfLastChat=chat.chatId;
                                    valueOfLastChat=chat.lastMessageTime;
                                }
                                if (numberOfTotalChatsArrived < 4) {
                                    if (numberOfTotalChatsArrived == counterOfChats) {
                                        isLastPage = true;
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void getPage(){
        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(getActivity()).id).child("chats");
        Query query=firebaseRef.orderByChild("last_message_date") .limitToLast(4).endAt(valueOfLastChat,keyOfLastChat);
        counterOfChats=0;
            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    numberOfTotalChatsArrived = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot chats : dataSnapshot.getChildren()) {

                        if (chats.hasChild("last_message_date")) {
                            final Chat chat = new Chat();
                            chat.chatId = chats.getKey();
                            if (chats.hasChild("event_id")) {
                                chat.eventId = chats.child("event_id").getValue().toString();
                            }
                            String names = "";
                            for (DataSnapshot users : chats.child("users").getChildren()) {
                                if (!users.getKey().equalsIgnoreCase(Persistance.getInstance().getUserInfo(getActivity()).id)) {
                                    names += users.child("name").getValue().toString() + ",";
                                    if (chat.eventId == null) {
                                        chat.image = users.child("image").getValue().toString();
                                    }
                                }

                            }
                            chat.participantName = names;

                            chat.lastMessageTime = Integer.valueOf(chats.child("last_message_date").getValue().toString());
                            DatabaseReference lastMessageRef = chats.child("messages").getRef();
                            Query lastMessage = lastMessageRef.orderByKey().limitToLast(1);
                            lastMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        chat.lastMessage = child.child("message").getValue().toString();
                                    }
                                    if (counterOfChats > 0 || numberOfTotalChatsArrived < 4) {
                                        chatList.add(numberOfChatsPage * 3, chat);
                                        setAdapter();
                                        counterOfChats++;
                                    } else {
                                        counterOfChats++;
                                        keyOfLastChat = chat.chatId;
                                        valueOfLastChat = chat.lastMessageTime;

                                        progressBarChats.setAlpha(0f);
                                    }
                                    if(counterOfChats == 4){
                                        numberOfChatsPage++;
                                    }
                                    if (numberOfTotalChatsArrived < 4) {
                                        if (numberOfTotalChatsArrived == counterOfChats) {
                                            progressBarChats.setAlpha(0f);
                                            isLastPage = true;
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

}
