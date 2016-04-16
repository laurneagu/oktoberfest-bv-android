package larc.ludicon.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import larc.ludicon.Activities.ChatListActivity;
import larc.ludicon.Activities.GeneralChatActivity;
import larc.ludicon.Activities.CreateNewActivity;
import larc.ludicon.Activities.FriendsActivity;
import larc.ludicon.Activities.SettingsActivity;
import larc.ludicon.Activities.StatisticsActivity;

/**
 * Created by LaurUser on 12/28/2015.
 */
public class LeftPanelItemClicker {
    public static void OnItemClick(ListView i_Drawerlist, final Context i_context,final Activity i_currActivity){
        i_Drawerlist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
                        switch(position) {
                            case 0:
                                Intent createNewIntent = new Intent(i_currActivity, CreateNewActivity.class);
                                i_currActivity.startActivity(createNewIntent);
                                break;
                            case 1:
                                Intent statisticsNewIntent = new Intent(i_currActivity, StatisticsActivity.class);
                                i_currActivity.startActivity(statisticsNewIntent);
                                break;
                            case 2:
                                Intent friendIntent = new Intent(i_currActivity, FriendsActivity.class);
                                i_currActivity.startActivity(friendIntent);
                                break;
                            case 3:
                                Intent chatIntent = new Intent(i_currActivity, ChatListActivity.class);
                                i_currActivity.startActivity(chatIntent);
                                break;
                            case 4:
                                Toast.makeText(i_context, "Hei, wait for it..", Toast.LENGTH_SHORT).show();
                                break;
                            case 5:
                                Intent helpIntent = new Intent(i_currActivity, SettingsActivity.class);
                                //helpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i_currActivity.startActivity(helpIntent);
                                break;
                        }
                    }});
    }
}