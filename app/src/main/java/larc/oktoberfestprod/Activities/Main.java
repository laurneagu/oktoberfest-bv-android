package larc.oktoberfestprod.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.io.Serializable;

import larc.oktoberfestprod.BottomBarHelper.BottomBar;
import larc.oktoberfestprod.BottomBarHelper.OnTabReselectListener;
import larc.oktoberfestprod.BottomBarHelper.OnTabSelectListener;
import larc.oktoberfestprod.Dialogs.PointsReceivedDialog;
import larc.oktoberfestprod.Layer.DataPersistence.ChatPersistence;
import larc.oktoberfestprod.R;
import me.anwarshahriar.calligrapher.Calligrapher;


public class Main extends FragmentActivity implements Serializable {
    public static BottomBar bottomBar;
    public static boolean exit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_tabs);

        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "fonts/Quicksand-Medium.ttf", true);

        // Initialize bottom bar
        this.bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        // Go to chat list activity when notification is thrown
        ChatPersistence chatPersistence = ChatPersistence.getInstance();
        String chatNotificationStatus = chatPersistence.getChatNotificationStatus(this);

        if (chatNotificationStatus.equalsIgnoreCase("1")) {
            bottomBar.setDefaultTab(R.id.tab_friends);
            // reset chat notification status
            chatPersistence.setChatNotificationStatus(this, "0");
        }

        int sel = super.getIntent().getIntExtra("Tab", -1);
        if (sel != -1) {
            bottomBar.setDefaultTab(sel);
        }

/*
        ActivitiesActivity main = new ActivitiesActivity();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, main).commit();
                */

        if (getIntent().getBooleanExtra("setChatTab", false)) {
            bottomBar.setDefaultTab(R.id.tab_friends);
        }

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_activities) {
                    ChatAndFriendsActivity.isOnChatPage = false;
                    ActivitiesActivity main = new ActivitiesActivity();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, main).commit();
                } else
                    if (tabId == R.id.tab_profile) {
                        ChatAndFriendsActivity.isOnChatPage = false;
                        ActivitiesActivity.isOnActivityPage = false;
                        MyProfileActivity myProfileActivity = new MyProfileActivity();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame, myProfileActivity).commit();
                    } else
                        if (tabId == R.id.tab_coupons) {
                            ChatAndFriendsActivity.isOnChatPage = false;
                            ActivitiesActivity.isOnActivityPage = false;
                            CouponsActivity coupons = new CouponsActivity();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame, coupons).commit();
                        } else
                            if (tabId == R.id.tab_leaderboard) {
                                ChatAndFriendsActivity.isOnChatPage = false;
                                ActivitiesActivity.isOnActivityPage = false;
                                LeaderboardActivity leaderboard = new LeaderboardActivity();
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.frame, leaderboard).commit();
                            } else
                                if (tabId == R.id.tab_friends) {
                                    ActivitiesActivity.isOnActivityPage = false;
                                    ChatAndFriendsActivity chatFriends = new ChatAndFriendsActivity();
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frame, chatFriends).commit();
                                }/*else if(tabId==R.id.tab_food){
                    SettingsActivity settings=new SettingsActivity();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame,settings).commit();
                }else if(tabId==R.id.tab_friends){
                    ChatListActivity chatFriends=new ChatListActivity();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame,chatFriends).commit();
                }
                */

            }
        });


        if (super.getIntent().getBooleanExtra("FirstTime", false)) {
            PointsReceivedDialog dialog = new PointsReceivedDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("ludicoins", 100);
            bundle.putInt("points", 0);
            bundle.putString("message", "Profile complete!");
            bundle.putInt("level", 1);
            dialog.setArguments(bundle);
            dialog.show(this.getFragmentManager(), "tag");
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame);
        if (currentFragment.getClass().equals(ActivitiesActivity.class)) {
            if (exit) {
                finish(); // finish activity
            } else {
                Toast.makeText(this, "Press back again to exit.",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3000);

            }
        } else {
            bottomBar.setDefaultTab(R.id.tab_activities);
        }
    }
}
