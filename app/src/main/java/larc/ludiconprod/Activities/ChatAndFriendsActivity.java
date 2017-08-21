package larc.ludiconprod.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    private View v;
    private DatabaseReference mDatabaseReferenceRef;
    CharSequence Titles[] = {"CONVERSATIONS", "FRIENDS"};
    int Numboftabs = 2;
    ArrayList<Chat> chatList=new ArrayList<>();

    public ChatAndFriendsActivity() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = inflater.getContext();
        v = inflater.inflate(R.layout.chat_and_friends_activity, container, false);
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


            DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(getActivity()).id).child("chats");
            firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot chats : dataSnapshot.getChildren()) {
                            Chat chat = new Chat();
                            chat.chatId = chats.getKey();
                            if (chats.hasChild("event_id")) {
                                chat.eventId = chats.child("event_id").getValue().toString();
                            }
                            String names="";
                            for (DataSnapshot users : chats.child("users").getChildren()) {
                                if(!users.getKey().equalsIgnoreCase(Persistance.getInstance().getUserInfo(getActivity()).id)){
                                    names+=users.child("name").getValue().toString()+",";
                                    if(chat.eventId == null){
                                        chat.image=users.child("image").getValue().toString();
                                    }
                                }

                            }
                            chat.participantName=names;

                            chat.lastMessageTime = Integer.valueOf(chats.child("last_message_date").getValue().toString());
                            String lastMessage=chats.getRef().child("messages").orderByKey().limitToLast(1).getRef().getKey().toString();
                            //chat.lastMessage=chats.child("messages").child(lastMessage).child("message").getValue().toString();
                            chatList.add(chat);
                        }
                        System.out.println(chatList.toArray().toString());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("eu");
                }
            });

            System.out.println("eu");





        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

}
