package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;

import larc.ludiconprod.Adapters.InviteFriendsAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Friend;

/**
 * Created by ancuta on 8/4/2017.
 */

public class InviteFriendsActivity extends Activity {

    static public InviteFriendsAdapter inviteFriendsAdapter;
    static public ArrayList<Friend> friendsList = new ArrayList<Friend>();
    static public ArrayList<Friend> participantList = new ArrayList<Friend>();
    static public int numberOfOfflineFriends = 0;
    static public Boolean isFirstTimeInviteFriends = false;
    Button saveInvitedFriends;
    ListView friendsListView;
    RelativeLayout backButton;
    Boolean mustRedirect=false;

    private View v;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.invite_friends_activity);

        final Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
        final Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.ttf");
        RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.tool_bar);
        TextView title = (TextView) toolbar.findViewById(R.id.titleText);
        title.setTypeface(typeFace);
        mustRedirect=getIntent().getBooleanExtra("mustRedirect",false);
        backButton = (RelativeLayout) findViewById(R.id.backButton);
        TextView titleText = (TextView) findViewById(R.id.titleText);
        friendsListView = (ListView) findViewById(R.id.inviteFriendsListView);
        saveInvitedFriends = (Button) findViewById(R.id.saveInvitedFriends);
        titleText.setText("Invite Friends");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mustRedirect){
                    HashMap<String, String> params = new HashMap<String, String>();
                    HashMap<String, String> headers = new HashMap<String, String>();
                    HashMap<String, String> urlParams = new HashMap<String, String>();
                    headers.put("authKey", Persistance.getInstance().getUserInfo(InviteFriendsActivity.this).authKey);

                    //set urlParams

                    urlParams.put("eventId", ActivityDetailsActivity.eventID);
                    urlParams.put("userId", Persistance.getInstance().getUserInfo(InviteFriendsActivity.this).id);
                    HTTPResponseController.getInstance().getEventDetails(params, headers, InviteFriendsActivity.this, urlParams);
                    finish();
                }else {
                    Intent intent = new Intent();
                    setResult(CreateNewActivity.ASK_FRIENDS_DONE, intent);
                    finish();
                }
            }
        });

        if (getIntent().getBooleanExtra("isCustomInvite", false)) {
            saveInvitedFriends.setVisibility(View.VISIBLE);
            saveInvitedFriends.setEnabled(true);
            saveInvitedFriends.setClickable(true);
            isFirstTimeInviteFriends = false;
            saveInvitedFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(InviteFriendsActivity.this, "Inviting friends...", Toast.LENGTH_SHORT).show();
                    HashMap<String, String> params = new HashMap<String, String>();
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("authKey", Persistance.getInstance().getUserInfo(InviteFriendsActivity.this).authKey);
                    params.put("userId", Persistance.getInstance().getUserInfo(InviteFriendsActivity.this).id);
                    params.put("eventId", getIntent().getStringExtra("eventId"));
                    int counterOfInvitedFriends = 0;
                    if (InviteFriendsActivity.numberOfOfflineFriends != 0) {
                        params.put("numberOfOffliners", String.valueOf(InviteFriendsActivity.numberOfOfflineFriends));
                    }
                    if (InviteFriendsActivity.friendsList.size() > 0) {
                        for (int i = 0; i < InviteFriendsActivity.friendsList.size(); i++) {
                            if (InviteFriendsActivity.friendsList.get(i).isInvited) {
                                params.put("invitedParticipants[" + counterOfInvitedFriends + "]", InviteFriendsActivity.friendsList.get(i).userID);
                                counterOfInvitedFriends++;
                            }
                        }
                    }
                    HTTPResponseController.getInstance().createEvent(params, headers, InviteFriendsActivity.this, getIntent().getStringExtra("eventId"), null,false);
                    saveInvitedFriends.setEnabled(false);

                }
            });
        } else {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) saveInvitedFriends.getLayoutParams();
            params.height = 0;
            params.bottomMargin = 0;
            params.topMargin = 0;
            saveInvitedFriends.setLayoutParams(params);
            saveInvitedFriends.setVisibility(View.INVISIBLE);
            saveInvitedFriends.setEnabled(false);
            saveInvitedFriends.setClickable(false);
        }


        if (!isFirstTimeInviteFriends && !getIntent().getBooleanExtra("isEdit", false) && !getIntent().getBooleanExtra("isParticipant", false)) {
            Friend friend = new Friend();
            friend.userName = "ADD OFFLINE FRIEND";
            friendsList.add(friend);
            getFriends("0");
            inviteFriendsAdapter = new InviteFriendsAdapter(friendsList, this, this, getResources(), this);
        } else
            if (!isFirstTimeInviteFriends && getIntent().getBooleanExtra("isEdit", false) && !getIntent().getBooleanExtra("isParticipant", false)) {
                Friend friend = new Friend();
                friend.userName = "ADD OFFLINE FRIEND";
                friendsList.add(friend);
                getInvitedFriends("0");
                inviteFriendsAdapter = new InviteFriendsAdapter(friendsList, this, this, getResources(), this);
            } else
                if (!isFirstTimeInviteFriends && getIntent().getBooleanExtra("isParticipant", false)) {
                    inviteFriendsAdapter = new InviteFriendsAdapter(participantList, this, this, getResources(), this);
                    ActivityDetailsActivity.ifFirstTimeGetParticipants = true;
                    titleText.setText("Participants");

                } else {
                    friendsListView.setAdapter(inviteFriendsAdapter);

                }

        updateListOfFriends();


    }

    public void updateListOfFriends() {
        if (!isFirstTimeInviteFriends) {
            isFirstTimeInviteFriends = true;
            friendsListView.setAdapter(inviteFriendsAdapter);
        }

        int last = friendsListView.getLastVisiblePosition();
        int count = inviteFriendsAdapter.getCount();
        if (last + 1 < count) {
            friendsListView.smoothScrollToPosition(last + 1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (inviteFriendsAdapter != null) inviteFriendsAdapter.notifyDataSetChanged();
    }


    public void getFriends(String pageNumber) {

        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> urlParams = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(InviteFriendsActivity.this).authKey);
        urlParams.put("userId", Persistance.getInstance().getUserInfo(InviteFriendsActivity.this).id);
        urlParams.put("pageNumber", pageNumber);

        HTTPResponseController.getInstance().getFriends(params, headers, InviteFriendsActivity.this, urlParams, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    public void getInvitedFriends(String pageNumber) {

        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> urlParams = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(InviteFriendsActivity.this).authKey);
        urlParams.put("eventId", getIntent().getStringExtra("eventId"));
        urlParams.put("userId", Persistance.getInstance().getUserInfo(InviteFriendsActivity.this).id);
        urlParams.put("pageNumber", pageNumber);

        HTTPResponseController.getInstance().getInvitedFriends(params, headers, InviteFriendsActivity.this, urlParams);
    }

    @Override
    public void onBackPressed() {
        if(mustRedirect){
            HashMap<String, String> params = new HashMap<String, String>();
            HashMap<String, String> headers = new HashMap<String, String>();
            HashMap<String, String> urlParams = new HashMap<String, String>();
            headers.put("authKey", Persistance.getInstance().getUserInfo(this).authKey);

            //set urlParams
           if(ActivityDetailsActivity.eventID != null) {
               urlParams.put("eventId", ActivityDetailsActivity.eventID);
           }else{
               urlParams.put("eventId", getIntent().getStringExtra("eventId"));
           }
            urlParams.put("userId", Persistance.getInstance().getUserInfo(this).id);
            HTTPResponseController.getInstance().getEventDetails(params, headers, this, urlParams);
            finish();
        }else {
            Intent intent = new Intent();
            setResult(CreateNewActivity.ASK_FRIENDS_DONE, intent);
            finish();
        }
    }

}
