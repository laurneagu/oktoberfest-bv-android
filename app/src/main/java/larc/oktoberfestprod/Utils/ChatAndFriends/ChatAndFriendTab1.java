package larc.oktoberfestprod.Utils.ChatAndFriends;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import larc.oktoberfestprod.Adapters.ChatAndFriends.ConversationsAdapter;
import larc.oktoberfestprod.Controller.Persistance;
import larc.oktoberfestprod.R;
import larc.oktoberfestprod.Utils.Chat;

/**
 * Created by ancuta on 8/18/2017.
 */

public class ChatAndFriendTab1 extends Fragment {
    View v;
    ConversationsAdapter chatAdapter;
    public ListView chatListView;
    ArrayList<Chat> chatList=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v =inflater.inflate(R.layout.chat_tab,container,false);
        chatList= Persistance.getInstance().getConversation(getActivity());
        chatAdapter = new ConversationsAdapter(chatList, getActivity().getApplicationContext(), getActivity(), getResources());
        setAdapter();
        return v;
    }


    public void setAdapter(){
        try {



            chatAdapter.notifyDataSetChanged();
            chatListView = (ListView) v.findViewById(R.id.chat_listView);
            chatListView.setAdapter(chatAdapter);
            TextView noConversationTV = (TextView) v.findViewById(R.id.noConversationTV);
            TextView joinActivitiesTV = (TextView) v.findViewById(R.id.joinActivitiesTV);
            Button discoverActivitiesButton = (Button) v.findViewById(R.id.discoverActivitiesButton);
            ImageView chatImage = (ImageView) v.findViewById(R.id.chatImage);
            noConversationTV.setVisibility(View.INVISIBLE);
            joinActivitiesTV.setVisibility(View.INVISIBLE);
            discoverActivitiesButton.setVisibility(View.INVISIBLE);
            chatImage.setVisibility(View.INVISIBLE);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


}

