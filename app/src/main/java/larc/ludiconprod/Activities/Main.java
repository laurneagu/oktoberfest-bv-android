package larc.ludiconprod.Activities;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import larc.ludiconprod.BottomBarHelper.*;
import larc.ludiconprod.Layer.DataPersistence.ChatPersistence;
import larc.ludiconprod.R;





public class Main extends FragmentActivity{
    BottomBar bottomBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_tabs);

        // Initialize bottom bar
        this.bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        // Go to chat list activity when notification is thrown
        ChatPersistence chatPersistence = ChatPersistence.getInstance();
        String chatNotificationStatus = chatPersistence.getChatNotificationStatus(this);

        if(chatNotificationStatus.equalsIgnoreCase("1")){
            bottomBar.setDefaultTab(R.id.tab_friends);
            // reset chat notification status
            chatPersistence.setChatNotificationStatus(this, "0");
        }

        int sel = super.getIntent().getIntExtra("Tab", -1);
        if (sel != -1) {
            bottomBar.setDefaultTab(sel);
        }

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if(tabId == R.id.tab_activities) {
                    ActivitiesActivity main = new ActivitiesActivity();
                    getSupportFragmentManager().beginTransaction()
                           .replace(R.id.frame, main).commit();
                } else if (tabId == R.id.tab_profile) {
                    MyProfileActivity myProfileActivity = new MyProfileActivity();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, myProfileActivity).commit();
                } else if (tabId == R.id.tab_coupons) {
                    CouponsActivity coupons = new CouponsActivity();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, coupons).commit();
                }else if(tabId == R.id.tab_friends){
                    ChatAndFriendsActivity chatFriends=new ChatAndFriendsActivity();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame,chatFriends).commit();
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

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) { // Reselect action
            }
        });

        // Notifications on tabs
        //BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_nearby);
        //nearby.setBadgeCount(5);
    }
    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }
}
