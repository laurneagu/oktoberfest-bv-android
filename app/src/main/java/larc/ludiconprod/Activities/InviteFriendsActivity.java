package larc.ludiconprod.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import larc.ludiconprod.Adapters.InviteFriendsAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Friend;

/**
 * Created by ancuta on 8/4/2017.
 */

public class InviteFriendsActivity extends Activity {

    static public InviteFriendsAdapter inviteFriendsAdapter;
    static public ArrayList<Friend> friendsList=new ArrayList<Friend>();
    Boolean isFirstTime=false;
    ListView friendsListView;
    private View v;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.invite_friends_activity);
        Friend friend=new Friend();
        friend.userName="Add Offline Friend";
        friendsList.add(friend);

        inviteFriendsAdapter = new InviteFriendsAdapter(friendsList, this, this, getResources(), this);
        updateListOfFriends();
    }

    public void updateListOfFriends() {

        friendsListView = (ListView) findViewById(R.id.inviteFriendsListView);
        if (!isFirstTime) {
        

            friendsListView.setAdapter(inviteFriendsAdapter);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (inviteFriendsAdapter != null) inviteFriendsAdapter.notifyDataSetChanged();
    }

}
