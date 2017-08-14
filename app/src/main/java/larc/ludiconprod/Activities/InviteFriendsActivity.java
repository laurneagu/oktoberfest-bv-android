package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import larc.ludiconprod.Adapters.InviteFriendsAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Friend;

/**
 * Created by ancuta on 8/4/2017.
 */

public class InviteFriendsActivity extends Activity {

    static public InviteFriendsAdapter inviteFriendsAdapter;
    static public ArrayList<Friend> friendsList=new ArrayList<Friend>();
    static public int numberOfOfflineFriends=0;
    static Boolean isFirstTimeInviteFriends=false;
    ListView friendsListView;
    ImageButton backButton;

    private View v;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.invite_friends_activity);
        backButton=(ImageButton) findViewById(R.id.backButton);
        backButton.setBackgroundResource(R.drawable.ic_nav_up);
        TextView titleText=(TextView) findViewById(R.id.titleText);
        friendsListView = (ListView) findViewById(R.id.inviteFriendsListView);
        titleText.setText("Invite Friends");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(CreateNewActivity.ASK_FRIENDS_DONE, intent);
                finish();
            }
        });





        if(!isFirstTimeInviteFriends && !getIntent().getBooleanExtra("isEdit",false)) {
            Friend friend=new Friend();
            friend.userName="Add Offline Friend";
            friendsList.add(friend);
            getFriends("0");
            inviteFriendsAdapter = new InviteFriendsAdapter(friendsList, this, this, getResources(), this);
        }else if(!isFirstTimeInviteFriends && getIntent().getBooleanExtra("isEdit",false)) {
            Friend friend = new Friend();
            friend.userName = "Add Offline Friend";
            friendsList.add(friend);
            getInvitedFriends("0");
            inviteFriendsAdapter = new InviteFriendsAdapter(friendsList, this, this, getResources(), this);
        }else{
            friendsListView.setAdapter(inviteFriendsAdapter);
        }

        updateListOfFriends();


    }

    public void updateListOfFriends() {


        if (!isFirstTimeInviteFriends) {

            isFirstTimeInviteFriends=true;
            friendsListView.setAdapter(inviteFriendsAdapter);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (inviteFriendsAdapter != null) inviteFriendsAdapter.notifyDataSetChanged();
    }


    public void getFriends(String pageNumber){

        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> urlParams = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(InviteFriendsActivity.this).authKey);
        urlParams.put("userId",Persistance.getInstance().getUserInfo(InviteFriendsActivity.this).id);
        urlParams.put("pageNumber",pageNumber);

        HTTPResponseController.getInstance().getFriends(params, headers,InviteFriendsActivity.this,urlParams);
    }
    public void getInvitedFriends(String pageNumber){

        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> urlParams = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(InviteFriendsActivity.this).authKey);
        urlParams.put("eventId",getIntent().getStringExtra("eventId"));
        urlParams.put("userId",Persistance.getInstance().getUserInfo(InviteFriendsActivity.this).id);
        urlParams.put("pageNumber",pageNumber);

        HTTPResponseController.getInstance().getInvitedFriends(params, headers,InviteFriendsActivity.this,urlParams);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(CreateNewActivity.ASK_FRIENDS_DONE, intent);
        finish();
    }

}
