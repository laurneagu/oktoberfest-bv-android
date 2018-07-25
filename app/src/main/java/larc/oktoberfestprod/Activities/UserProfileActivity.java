package larc.oktoberfestprod.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import larc.oktoberfestprod.Controller.HTTPResponseController;
import larc.oktoberfestprod.Controller.Persistance;
import larc.oktoberfestprod.Dialogs.ConfirmationDialog;
import larc.oktoberfestprod.R;
import larc.oktoberfestprod.User;
import larc.oktoberfestprod.Utils.util.Sport;

public class UserProfileActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    private User user = new User();
    private final HashMap<String, Integer> youPoints = new HashMap<>();
    private final HashMap<String, Integer> foePoints = new HashMap<>();
    private String userName;
    private String userImage;
    public String firstName;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();

            super.setContentView(R.layout.user_profile_activity);

            View backButton = findViewById(R.id.backButton);
            TextView titleText = (TextView) findViewById(R.id.titleText);
            titleText.setText("Player profile");

            final Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
            titleText.setTypeface(typeFace);

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            Button addFriend = (Button) super.findViewById(R.id.profileFriend);
            Button chat = (Button) super.findViewById(R.id.profileChat);

            chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
                    intent.putExtra("otherParticipantName", userName);
                    ArrayList<String> myList = new ArrayList<String>();
                    myList.add(userImage);
                    intent.putExtra("otherParticipantImage", myList);
                    intent.putExtra("chatId", "isNot");
                    intent.putExtra("groupChat", 0);
                    intent.putExtra("UserId", getIntent().getStringExtra("UserId"));
                    ArrayList<String> userIdList = new ArrayList<String>();
                    userIdList.add(getIntent().getStringExtra("UserId"));
                    intent.putExtra("otherParticipantId", userIdList);
                    UserProfileActivity.this.startActivity(intent);
                    //finish();
                }
            });


            Typeface typeFaceBold = Typeface.createFromAsset(super.getAssets(), "fonts/Quicksand-Bold.ttf");

            titleText.setTypeface(typeFace);
            ((TextView) findViewById(R.id.profileName)).setTypeface(typeFace);
            ((Button) findViewById(R.id.profileChat)).setTypeface(typeFaceBold);
            ((Button) findViewById(R.id.profileFriend)).setTypeface(typeFaceBold);
            ((TextView) findViewById(R.id.profileLevel)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.profileLevelText)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.profilePoints)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.profilePointsText)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.profilePosition)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.profilePositionText)).setTypeface(typeFace);

            ((TextView) findViewById(R.id.profilePracticeSportsLabel)).setTypeface(typeFaceBold);
            ((TextView) findViewById(R.id.profilePracticeSportsCountLabel)).setTypeface(typeFace);

            ((TextView) findViewById(R.id.profileBadgesText)).setTypeface(typeFaceBold);
            ((TextView) findViewById(R.id.profileBadgesCountLabel)).setTypeface(typeFace);

            ((TextView) findViewById(R.id.versusLabel)).setTypeface(typeFaceBold);
            ((TextView) findViewById(R.id.youLabel)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.foeLabel)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.profileYouPoints)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.profileFoePoints)).setTypeface(typeFace);

            ((TextView) findViewById(R.id.vsLabel)).setTypeface(typeFaceBold);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        this.requestInfo();
    }

    private void requestInfo() {
        View tv = findViewById(R.id.profileContent);
        tv.setAlpha(0);
        tv = findViewById(R.id.profileProgressBar);
        tv.setAlpha(1);

        User u = Persistance.getInstance().getUserInfo(this);
        String id = super.getIntent().getStringExtra("UserId");
        this.user.id = id;

        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("authKey", u.authKey);
        HTTPResponseController.getInstance().getUserProfile(params, headers, id, this, this, this);
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        try {
            User u = this.user;

            u.email = jsonObject.getString("email");
            u.firstName = jsonObject.getString("firstName");
            firstName = u.firstName;
            u.lastName = jsonObject.getString("lastName");
            u.gender = jsonObject.getString("gender");
            u.ludicoins = Integer.parseInt(jsonObject.getString("ludicoins"));
            u.countEventsAttended = Integer.parseInt(jsonObject.getString("countEventsAttended"));
            u.level = Integer.parseInt(jsonObject.getString("level"));
            u.points = Integer.parseInt(jsonObject.getString("points"));
            u.pointsToNextLevel = Integer.parseInt(jsonObject.getString("pointsToNextLevel"));
            u.pointsOfNextLevel = Integer.parseInt(jsonObject.getString("pointsOfNextLevel"));
            u.position = Integer.parseInt(jsonObject.getString("position"));
            u.range = jsonObject.getString("range");
            u.profileImage = jsonObject.getString("profileImage");

            JSONArray sports = jsonObject.getJSONArray("sports");
            u.sports.clear();
            for (int i = 0; i < sports.length(); ++i) {
                u.sports.add(new Sport(sports.getString(i)));
            }

            boolean friend = jsonObject.getBoolean("isFriend");

            this.youPoints.clear();
            this.foePoints.clear();
            try {
                JSONObject headtohead = jsonObject.getJSONObject("headtohead");
                JSONArray names = headtohead.names();
                for (int i = 0; i < names.length(); ++i) {
                    String spn = names.getString(i);
                    this.youPoints.put(spn, Integer.parseInt(headtohead.getJSONObject(spn).getString("user")));
                    this.foePoints.put(spn, Integer.parseInt(headtohead.getJSONObject(spn).getString("versus")));
                }
            } catch (JSONException e) {
            }

            this.printInfo(friend);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void printInfo(boolean friend) {
        try {
            RelativeLayout ll = (RelativeLayout) findViewById(R.id.noInternetLayout);
            ll.getLayoutParams().height = 0;
            ll.setLayoutParams(ll.getLayoutParams());

            User u = this.user;

            TextView sportsCount = (TextView) findViewById(R.id.profilePracticeSportsCountLabel);
            ImageView image = (ImageView) findViewById(R.id.profileImage);
            userImage = u.profileImage;
            if (u.profileImage != null && !u.profileImage.isEmpty()) {
                Bitmap im = IntroActivity.decodeBase64(u.profileImage);
                image.setImageBitmap(im);
            }

            TextView name = (TextView) findViewById(R.id.profileName);
            TextView level = (TextView) findViewById(R.id.profileLevel);
            TextView points = (TextView) findViewById(R.id.profilePoints);
            TextView position = (TextView) findViewById(R.id.profilePosition);

            name.setText(u.firstName + " " + u.lastName);
            userName = u.firstName + " " + u.lastName + ",";
            level.setText("" + u.level);
            points.setText("" + u.points);
            position.setText("" + u.position);

            final ArrayList<String> sportCodes = new ArrayList<>();
            for (Sport s : u.sports) {
                sportCodes.add(s.code);
            }

            LinearLayout sportsLayout = (LinearLayout) findViewById(R.id.profileSports);
            ImageView sportImage;
            ArrayList<String> allSportCodes = new ArrayList<>(Sport.getSportMap().keySet());
            Collections.sort(allSportCodes, new Comparator<String>() {
                @Override
                public int compare(String s, String t1) {
                    if (sportCodes.contains(s) && !sportCodes.contains(t1)) {
                        return -1;
                    }
                    return s.compareTo(t1);
                }
            });

            Button friendButton = (Button) super.findViewById(R.id.profileFriend);
            if (friend) {
                friendButton.setText("UNFOLLOW");
                friendButton.setOnClickListener(new View.OnClickListener() {
                    final Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
                    final Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.ttf");

                    @Override
                    public void onClick(View view) {
                        final ConfirmationDialog confirmationDialog = new ConfirmationDialog(UserProfileActivity.this);
                        confirmationDialog.show();
                        confirmationDialog.title.setText("Confirm?");
                        confirmationDialog.title.setTypeface(typeFaceBold);
                        confirmationDialog.message.setText("Are you sure you want to unfollow " + firstName + "?");
                        confirmationDialog.message.setTypeface(typeFace);
                        confirmationDialog.confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                removeFriend();
                                confirmationDialog.dismiss();
                            }
                        });
                        confirmationDialog.dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                confirmationDialog.dismiss();
                            }
                        });
                    }
                });
            } else {
                friendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addFriend();
                    }
                });
            }

            sportsCount.setText(sportCodes.size() + "/" + allSportCodes.size());
            Resources r = this.getResources();
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

            sportsLayout.removeAllViews();
            for (int i = 0; i < allSportCodes.size(); ++i) {
                String sc = allSportCodes.get(i);
                sportImage = new ImageView(this);

                sportImage.setImageResource(MyProfileActivity.findSportImageResource(sc));
                if (!sportCodes.contains(sc)) {
                    sportImage.setAlpha(0.4f);
                }
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                if (i == allSportCodes.size() - 1) {
                    lp.setMargins(0, 0, 0, 0);
                } else {
                    lp.setMargins(0, 0, px, 0);
                }

                sportsLayout.addView(sportImage, lp);
            }

            ImageView youImg = (ImageView) findViewById(R.id.profileYouImage);
            ImageView versusImg = (ImageView) findViewById(R.id.profileFoeImage);

            TextView foeLabel = (TextView) findViewById(R.id.foeLabel);
            String firstName = u.firstName;
            String[] splited = firstName.split("\\s+");
            foeLabel.setText(splited[0]);
            TextView youLabel = (TextView) findViewById(R.id.youLabel);
            User you = Persistance.getInstance().getUserInfo(this);
            String firstNameYou = you.firstName;
            String[] splitedYou = firstNameYou.split("\\s+");
            youLabel.setText(splitedYou[0]);


            if (you.profileImage != null && !you.profileImage.isEmpty()) {
                Bitmap im = IntroActivity.decodeBase64(you.profileImage);
                youImg.setImageBitmap(im);
            }
            if (u.profileImage != null && !u.profileImage.isEmpty()) {
                Bitmap im = IntroActivity.decodeBase64(u.profileImage);
                versusImg.setImageBitmap(im);
            }

            TextView youPoints = (TextView) findViewById(R.id.profileYouPoints);
            TextView foePoints = (TextView) findViewById(R.id.profileFoePoints);
            Integer yp = this.youPoints.get(Sport.GENERAL);
            Integer fp = this.foePoints.get(Sport.GENERAL);
            if (yp == null) {
                yp = 0;
            }
            if (fp == null) {
                fp = 0;
            }
            youPoints.setText("" + yp);
            foePoints.setText("" + fp);
            this.youPoints.remove(Sport.GENERAL);
            this.foePoints.remove(Sport.GENERAL);

            LinearLayout versusLayout = (LinearLayout) findViewById(R.id.profileVresus);
            versusLayout.removeAllViews();

            sportCodes.clear();
            sportCodes.addAll(this.youPoints.keySet());
            for (int i = 0; i < sportCodes.size(); ++i) {
                String sc = sportCodes.get(i);


                yp = this.youPoints.get(sc);
                fp = this.foePoints.get(sc);
                int tot = yp + fp;

                LayoutInflater.from(this).inflate(R.layout.versus_card, versusLayout);
                View c = versusLayout.getChildAt(i);

                sportImage = (ImageView) c.findViewById(R.id.image);
                TextView t = (TextView) c.findViewById(R.id.youText);

                t.setText("" + yp);
                t = (TextView) c.findViewById(R.id.foeText);
                t.setText("" + fp);

                if (tot > 0) {
                    ProgressBar p = (ProgressBar) c.findViewById(R.id.you);
                    p.setProgress(yp * 100 / tot);
                    p = (ProgressBar) c.findViewById(R.id.foe);
                    p.setProgress(fp * 100 / tot);
                } else {
                    ProgressBar p = (ProgressBar) c.findViewById(R.id.you);
                    p.setProgress(0);
                    p = (ProgressBar) c.findViewById(R.id.foe);
                    p.setProgress(0);
                }

                sportImage.setImageResource(MyProfileActivity.findSportImageResource(sc));
            }

            View tv = findViewById(R.id.profileContent);
            tv.setAlpha(1);
            tv = findViewById(R.id.profileProgressBar);
            tv.setAlpha(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onFriendResponse(JSONObject response, boolean action) {
        String name = this.user.firstName + " " + this.user.lastName;
        if (action) {
            Toast.makeText(this, "You are now following " + name + "!", Toast.LENGTH_SHORT).show();
            this.friendAdded();
        } else {
            Toast.makeText(this, "You are no following " + name + "!", Toast.LENGTH_SHORT).show();
            this.friendRemoved();
        }
    }

    public void addFriend() {
        User u = Persistance.getInstance().getUserInfo(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", u.id);
        params.put("userToRequestId", this.user.id);
        params.put("action", "1");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("authKey", u.authKey);
        HTTPResponseController.getInstance().friendRequest(params, headers, this, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onFriendResponse(response, true);
            }
        }, this);
    }

    public void removeFriend() {
        User u = Persistance.getInstance().getUserInfo(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", u.id);
        params.put("userToRequestId", this.user.id);
        params.put("action", "0");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("authKey", u.authKey);
        HTTPResponseController.getInstance().friendRequest(params, headers, this, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onFriendResponse(response, false);
            }
        }, this);
    }

    public void friendAdded() {
        final Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
        final Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.ttf");
        Button friendButton = (Button) super.findViewById(R.id.profileFriend);
        friendButton.setText("UNFOLLOW");
        friendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final ConfirmationDialog confirmationDialog = new ConfirmationDialog(UserProfileActivity.this);
                confirmationDialog.show();
                confirmationDialog.title.setText("Confirm?");
                confirmationDialog.title.setTypeface(typeFaceBold);
                confirmationDialog.message.setText("Are you sure you want to unfollow " + firstName + "?");
                confirmationDialog.message.setTypeface(typeFace);
                confirmationDialog.confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeFriend();
                        confirmationDialog.dismiss();
                    }
                });
                confirmationDialog.dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        confirmationDialog.dismiss();
                    }
                });
            }
        });
    }

    public void friendRemoved() {
        Button friendButton = (Button) super.findViewById(R.id.profileFriend);
        friendButton.setText("FOLLOW");
        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriend();
            }
        });
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //Toast.makeText(super.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        if (error instanceof NetworkError) {
            findViewById(R.id.internetRefresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setOnClickListener(null);
                    onInternetRefresh();
                }
            });
            RelativeLayout ll = (RelativeLayout) findViewById(R.id.noInternetLayout);
            final float scale = super.getResources().getDisplayMetrics().density;
            int pixels = (int) (56 * scale + 0.5f);
            ll.getLayoutParams().height = pixels;
            ll.setLayoutParams(ll.getLayoutParams());
            findViewById(R.id.profileProgressBar).setAlpha(0);
        }
    }

    public void onInternetRefresh() {
        findViewById(R.id.internetRefresh).setOnClickListener(null);
        requestInfo();
    }
}
