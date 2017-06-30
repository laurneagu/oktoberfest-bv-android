package larc.ludiconprod.Activities;

import android.app.Activity;
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
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import larc.ludiconprod.Adapters.LeftPanelItemClicker;
import larc.ludiconprod.Adapters.LeftSidePanelAdapter;
import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.ChatUtils.Chat1to1;
import larc.ludiconprod.Utils.FriendUtils.FriendItem;
import larc.ludiconprod.Utils.MainPageUtils.ViewPagerAdapter;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;
import larc.ludiconprod.Utils.util.ChatNotifier;
import larc.ludiconprod.Utils.util.DateManager;
import larc.ludiconprod.Utils.util.Utils;

import java.util.*;

public class ChatListActivity extends Fragment {

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog dialog;
    private int TIMEOUT = 1;
    public static boolean isForeground = false;
    ProgressDialog progress;
    private View v;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"CONVERSATIONS", "FRIENDS"};
    int Numboftabs = 2;
    boolean addedSwipe = false;
    boolean addedSwipe2 = false;
    final List<Chat1to1> chatList = new ArrayList<>();
     String chatUID;
    final ArrayList<FriendItem> friends = new ArrayList<>();
    final ArrayList<String> friendsUIDs = new ArrayList<>();


    private static final String FIREBASE_URL = "https://ludicon.firebaseio.com/";

    public ChatListActivity(){

    }

    @Override
    public void onStart() {
        super.onStart();
        isForeground = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isForeground = false;
    }

    private MyCustomAdapterChat adapterChat;
    private ChatListActivity.MyCustomAdapterFriends adapterFriends;

    @Override
    public void onResume() {
        super.onResume();
        updateList();
        isForeground = true;

        if (adapterChat != null)
            adapterChat.notifyDataSetChanged();
    }

    Object waitForFriends = new Object();

    String friendUID = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.activity_chat_list, container,false);
        try {
            //super.onCreate(savedInstanceState);

            // Hide App bar
            // If the Android version is lower than Jellybean, use this call to hide
            // the status bar.
            if (Build.VERSION.SDK_INT < 16) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            if (android.os.Build.VERSION.SDK_INT >= 11) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }

            // remove title
            // requestWindowFeature(Window.FEATURE_NO_TITLE);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            //  WindowManager.LayoutParams.FLAG_FULLSCREEN);

            //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //setContentView(R.layout.activity_chat_list);

            // Left side panel initializing
            // mDrawerList = (ListView) v.findViewById(R.id.leftMenu);
            // initializeLeftSidePanel();
            // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
            adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), Titles, Numboftabs);

            // Assigning ViewPager View and setting the adapter
            pager = (ViewPager) v.findViewById(R.id.pagerChatFriends);
            pager.setAdapter(adapter);

            // Assiging the Sliding Tab Layout View
            tabs = (SlidingTabLayout) v.findViewById(R.id.tabsChatFriends);
            tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(pager);
            /**************/

        /* Progress dialog */
            progress = new ProgressDialog(getActivity());
            progress.setTitle("Loading");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();

            //User.setImage();


            chatUID = getActivity().getIntent().getStringExtra("chatUID");
            dialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait", true);

            // User picture and name for HEADER MENU
        /*
        Typeface segoeui = Typeface.createFromAsset(getActivity().getAssets(), "fonts/seguisb.ttf");

        TextView userName = (TextView) v.findViewById(R.id.userName);
        userName.setText(User.getFirstName(getActivity().getApplicationContext()));
        userName.setTypeface(segoeui);

        TextView userSportsNumber = (TextView)v.findViewById(R.id.userSportsNumber);
        userSportsNumber.setText(User.getNumberOfSports(getActivity().getApplicationContext()));
        userSportsNumber.setTypeface(segoeui);
        */

        /*
        final ImageButton createNewChat = (ImageButton)findViewById(R.id.header_button);
        createNewChat.setVisibility(View.VISIBLE);
        createNewChat.setBackgroundResource(R.drawable.admin_add2);

        createNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friendListIntent = new Intent(getApplicationContext(), FriendsActivity.class);
                ChatListActivity.this.startActivity(friendListIntent);
            }
        });
        */
/*
        TextView hello_message = (TextView) v.findViewById(R.id.hello_message_activity);
        hello_message.setText("");
        ImageView userPic = (ImageView) v.findViewById(R.id.userPicture);
        Drawable d = new BitmapDrawable(getResources(), User.image);
        userPic.setImageDrawable(d);
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                ChatListActivity.this.startActivity(mainIntent);
            }
        });
*/

            // Delete chat notifications

        ChatNotifier chatNotifier = new ChatNotifier();
        synchronized (chatNotifier.lock) {
            for (int i = chatNotifier.chatNotificationFirstIndex; i <= chatNotifier.chatNotificationIndex; ++i) {
                chatNotifier.deleteNotification(getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE), i);
            }
            chatNotifier.chatNotificationIndex = 0;
            chatNotifier.chatNotificationFirstIndex = 0;
        }



            //continueUpdatingTimeline();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return v;
    }


    public void getChatInfo(final List<Chat1to1> chatList, final Chat1to1 chat, final long size)
    {
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(chat.userUID);
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {
                    if ((data.getKey()).compareTo("name") == 0) {
                        String name = data.getValue().toString();
                        chat.userName = name;
                    }
                    if ((data.getKey()).compareTo("profileImageURL") == 0)
                        if (data.getValue() != null) {
                            //new DownloadImageTask(imageView).execute(data.getValue().toString());
                            chat.friendPhoto = data.getValue().toString();
                        } else {
                            chat.friendPhoto = "";
                        }

                    if  ((data.getKey()).compareTo("lastLogInTime") == 0)
                        if (data.getValue() != null) {
                            Date lastOnlineDate = DateManager.convertFromSecondsToDate((long)data.getValue());
                            chat.lastTimeOnline= formatMessageDate(lastOnlineDate);
                        }
                        else{
                            chat.lastTimeOnline="";
                        }
                }

                chatList.add(chat);

                // Reach chat end of the list
                if(chatList.size()==size){
                    progress.dismiss();
                    List<Chat1to1> newChatList = chatList;

                    for(int i =0 ; i < newChatList.size() ; i++) {
                        // this is for the bug of creating conversation when tapping a friend
                        if (newChatList.get(i).lastMessageText.equalsIgnoreCase("Welcome to our chat! :)")) {
                            newChatList.remove(newChatList.get(i));
                            i--;
                        }
                    }
                    Collections.sort(newChatList);


                    adapterChat = new MyCustomAdapterChat(newChatList, getActivity().getApplicationContext());
                    ListView listView = (ListView) v.findViewById(R.id.events_listView2);
                    listView.setAdapter(adapterChat);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        if (!addedSwipe) {
            final SwipeRefreshLayout mSwipeRefreshLayout1 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh1);
            mSwipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateList();
                    mSwipeRefreshLayout1.setRefreshing(false);
                }
            });
            addedSwipe = true;
        }
    }
    public void continueUpdatingTimeline() {
        try {
            updateList();
        }
        catch (Exception e){
            e.printStackTrace();
            Utils.quit();
        }
        }
    public void updateList() {
        chatList.clear();
        friends.clear();
        friendsUIDs.clear();

        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(User.uid).child("chats");
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                final long size = snapshot.getChildrenCount();
                for (DataSnapshot data : snapshot.getChildren()) {
                    final Chat1to1 chat = new Chat1to1();
                    chat.userUID = data.getKey().toString();
                    chat.chatID = data.getValue().toString();

                    if (chatUID != null && chatUID.equalsIgnoreCase(chat.chatID)) {
                        friendUID = chat.userUID;
                    }

                    DatabaseReference chatRef = User.firebaseRef.child("chat").child(chat.chatID).child("Messages");
                    chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                for (DataSnapshot snap : data.getChildren()) {
                                    if (snap.getKey().toString().equalsIgnoreCase("author"))
                                        chat.lastMessageAuthor = snap.getValue().toString().split(" ")[0];
                                    if (snap.getKey().toString().equalsIgnoreCase("message"))
                                        chat.lastMessageText = snap.getValue().toString();
                                    if (snap.getKey().toString().equalsIgnoreCase("date"))
                                        chat.lastMessageDateString = snap.getValue().toString();
                                }
                            }
                            getChatInfo(chatList, chat, size);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

                if (chatUID != null) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), ChatTemplateActivity.class);
                    intent.putExtra("uid", friendUID);
                    intent.putExtra("firstConnection", false);
                    intent.putExtra("chatID", chatUID);
                    startActivity(intent);
                }

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

                timer.schedule(delayedThreadStartTask, TIMEOUT * 12);
                progress.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        /*Swipe */
        /*if (!addedSwipe) {
            final SwipeRefreshLayout mSwipeRefreshLayout1 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh1);
            mSwipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateList();
                    mSwipeRefreshLayout1.setRefreshing(false);
                }
            });
            addedSwipe = true;
        }
        */

                 /*Swipe */
       /* if (!addedSwipe2) {
            final SwipeRefreshLayout mSwipeRefreshLayout2 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh2);
            mSwipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateList();
                    mSwipeRefreshLayout2.setRefreshing(false);
                }
            });

            addedSwipe2 = true;
        }*/
    }
    //Object waitForFriends = new Object();
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

                Log.v("TAG",friends.size()+"Test laur");

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

                                timer.schedule(delayedThreadStartTask, TIMEOUT * 12);

                                //progress.dismiss();
                                int iRemovedCount=0;
                                // Remove empty indexes
                                for(Integer indexToRemove : toRemoveNoMoreFriends){
                                    friends.remove(friends.get(indexToRemove-iRemovedCount));
                                    iRemovedCount++;
                                }
                                for(int i=0;i<friends.size();i++){
                                    System.out.println(friends.get(i).name);
                                   // if(friends.get(i).name == null){
                                    //    friends.remove(i);
                                    //}
                                }

                                //if(friends!=null) {
                                    // Sort friends alphabetically
                                    Collections.sort(friends);
                               // }

                                adapterFriends = new ChatListActivity.MyCustomAdapterFriends(friends,getActivity().getApplicationContext());
                                ListView listView = (ListView) v.findViewById(R.id.events_listView1);
                                Log.v("TAG",friends.size()+"");
                                listView.setAdapter(adapterFriends);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                        }
                    });
                }

                if(friends.size() == 0){
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

                    timer.schedule(delayedThreadStartTask, TIMEOUT * 12);

                    //progress.dismiss();
                }
                if (!addedSwipe2) {
                    final SwipeRefreshLayout mSwipeRefreshLayout2 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh2);
                    mSwipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            updateList();
                            mSwipeRefreshLayout2.setRefreshing(false);
                        }
                    });

                    addedSwipe2 = true;
                }
            }

        };
    }


    public class MyCustomAdapterChat extends BaseAdapter implements ListAdapter {

        private List<Chat1to1> list = new ArrayList<>();
        private Context context;
        //private final Map<String,Boolean> states = new HashMap<String,Boolean>();


        class ViewHolder {
             TextView textName;
             ImageView imageView;
             Button chatButton;
             TextView lastLoginView;
             TextView lastMessage;
        };


        public MyCustomAdapterChat(List<Chat1to1> list, Context context) {
            //list.remove(0);
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

            ViewHolder holder = null;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.chat1to1_layout, null);
                view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                holder = new ViewHolder();

                holder.textName = (TextView) view.findViewById(R.id.friend_name);
                holder.imageView = (ImageView) view.findViewById(R.id.friend_photo);
                holder.lastLoginView = (TextView) view.findViewById(R.id.lastOnline);
                holder.lastMessage = (TextView) view.findViewById(R.id.lastMessage);
                //holder.chatButton = (Button) view.findViewById(R.id.gotoChat);

                final View currentView = view;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference userRef = User.firebaseRef.child("users").child(list.get(position).userUID).child("name");
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                currentView.setBackgroundColor(Color.parseColor("#D3D3D3"));

                                Intent intent = new Intent(getActivity().getApplicationContext(), ChatTemplateActivity.class);
                                intent.putExtra("uid", list.get(position).userUID);
                                intent.putExtra("firstConnection", false);
                                intent.putExtra("chatID", list.get(position).chatID);

                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError firebaseError) {
                            }
                        });
                    }
                });

                view.setTag(holder);
            }
            else {
                holder = (ViewHolder)view.getTag();
                view.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            String lastMessageAuthor = "";
            if (!list.get(position).lastMessageAuthor.equalsIgnoreCase("Ludicon")) {
                if (!list.get(position).userName.split(" ")[0].equalsIgnoreCase(list.get(position).lastMessageAuthor))
                    lastMessageAuthor = "You";
                else lastMessageAuthor = list.get(position).lastMessageAuthor;

                String lastMessageTrimmed = list.get(position).lastMessageText;
                if(lastMessageTrimmed.length() > 35)
                    lastMessageTrimmed = lastMessageTrimmed.substring(0,35) + "[...]";
                holder.lastMessage.setText(lastMessageAuthor + ": " + lastMessageTrimmed);
            }
            else
            {
                holder.lastMessage.setText("");
            }
            holder.textName.setText(list.get(position).userName);

            String lastOnline = list.get(position).lastTimeOnline;
            if(lastOnline != null) {
                lastOnline = lastOnline.replace(",", "");
                lastOnline = lastOnline.replace("PM", "");
                lastOnline = lastOnline.replace("AM", "");

                holder.lastLoginView.setText(lastOnline);
            }
            else{
                holder.lastLoginView.setText("Unknown");
            }

            if (list.get(position).friendPhoto != "") {
                //new DownloadImageTask(imageView).execute(data.getValue().toString());
                holder.imageView.setBackgroundResource(R.drawable.defaultpicture);
                Picasso.with(context).load(list.get(position).friendPhoto).into(holder.imageView);
                holder.imageView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("uid", list.get(position).userUID);
                        startActivity(intent);
                    }
                });
            } else {
                holder.imageView.setImageResource(R.drawable.logo);
            }

            return view;
        }
    }
    public class MyCustomAdapterFriends extends BaseAdapter implements ListAdapter {

        private ArrayList<FriendItem> list = new ArrayList<>();
        private Context context;

        public MyCustomAdapterFriends(ArrayList<FriendItem> list, Context context) {
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
            ChatListActivity.MyCustomAdapterFriends.ViewHolder holder = null;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.friends_layout, null);

                holder = new ChatListActivity.MyCustomAdapterFriends.ViewHolder();
                holder.textName = (TextView) view.findViewById(R.id.list_item_string);
                holder.imageView = (ImageView) view.findViewById(R.id.profileImageView);
                holder.chatButton = (Button) view.findViewById(R.id.chat_btn);
                holder.numberSports = (TextView) view.findViewById(R.id.numberSports);
                holder.numberMutuals = (TextView) view.findViewById(R.id.numberoOfMutuals);

                view.setTag(holder);
            }
            else {
                holder = (ChatListActivity.MyCustomAdapterFriends.ViewHolder)view.getTag();
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
                    Intent intent = new Intent(getActivity().getApplicationContext(), ProfileActivity.class);
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
                            Intent intent = new Intent(getActivity().getApplicationContext(), ChatTemplateActivity.class);
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


    // Left side menu
/*
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
    */

    public static DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");

    private String formatMessageDate(Date messageDate) {

        Date currDate = new Date();
        Calendar calCurrent = Calendar.getInstance();
        calCurrent.setTime(currDate);

        Calendar calMessage = Calendar.getInstance();
        calMessage.setTime(messageDate);

        String result ="";
        if (calCurrent.get(Calendar.DAY_OF_MONTH) == calMessage.get(Calendar.DAY_OF_MONTH) &&
                calCurrent.get(Calendar.MONTH) == calMessage.get(Calendar.MONTH) &&
                calCurrent.get(Calendar.YEAR) == calMessage.get(Calendar.YEAR)){

            String minutes="";
            if(messageDate.getMinutes() < 10){
                minutes =  "0" + messageDate.getMinutes();
            }
            else{
                minutes = messageDate.getMinutes() + "";
            }
            result = "Today, " + messageDate.getHours() + ":" + minutes;
        }
        else if((calCurrent.get(Calendar.DAY_OF_MONTH) == calMessage.get(Calendar.DAY_OF_MONTH) + 1) &&
                calCurrent.get(Calendar.MONTH) == calMessage.get(Calendar.MONTH) &&
                calCurrent.get(Calendar.YEAR) == calMessage.get(Calendar.YEAR)){
            String minutes="";
            if(messageDate.getMinutes() < 10){
                minutes =  "0" + messageDate.getMinutes();
            }
            else{
                minutes = messageDate.getMinutes() + "";
            }
            result = "Yesterday, " + messageDate.getHours() + ":" + minutes;
        }
        else{
            result = dateFormat.format(messageDate);
        }

        return result;
    }


   // @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_chat_list, menu);
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
    public void onBackPressed() {
        Intent toMain = new Intent(getActivity(),Main.class);
        toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMain);
    }

}
