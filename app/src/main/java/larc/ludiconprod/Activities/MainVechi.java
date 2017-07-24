/*
package larc.ludiconprod.Activities;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import larc.ludiconprod.BottomBarHelper.*;
import larc.ludiconprod.Layer.DataPersistence.ChatPersistence;
import larc.ludiconprod.R;

*/
/**
 * Created by Razvan on 28.06.2017.
 *//*


public class MainVechi extends FragmentActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_tabs);

        // Initialize bottom bar
        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        // Go to chat list activity when notification is thrown
        ChatPersistence chatPersistence = ChatPersistence.getInstance();
        String chatNotificationStatus = chatPersistence.getChatNotificationStatus(this);

        if(chatNotificationStatus.equalsIgnoreCase("1")){
            bottomBar.setDefaultTab(R.id.tab_friends);
            // reset chat notification status
            chatPersistence.setChatNotificationStatus(this, "0");
        }

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if(tabId == R.id.tab_recents) {
                    MainActivityVechi main = new MainActivityVechi();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, main).commit();
                }else if(tabId == R.id.tab_nearby){
                    RankingsNewActivity rankings=new RankingsNewActivity();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame,rankings).commit();
                }else if(tabId==R.id.tab_food){
                    SettingsActivity settings=new SettingsActivity();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame,settings).commit();
                }else if(tabId==R.id.tab_friends){
                    ChatListActivity chatFriends=new ChatListActivity();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame,chatFriends).commit();
                }
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
}
*/
