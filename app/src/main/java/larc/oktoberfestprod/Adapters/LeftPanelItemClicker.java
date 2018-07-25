/*
package larc.ludiconprod.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import larc.ludiconprod.Activities.ChatListActivity;
import larc.ludiconprod.Activities.CreateNewActivity;
import larc.ludiconprod.Activities.FriendsActivity;
import larc.ludiconprod.Activities.RankingsNewActivity;
import larc.ludiconprod.Activities.SettingsActivity;
import larc.ludiconprod.Activities.StatisticsActivity;
import larc.ludiconprod.UserInfo.User;

*/
/**
 * Created by LaurUser on 12/28/2015.
 *//*

public class LeftPanelItemClicker {
    public static void OnItemClick(ListView i_Drawerlist, final Context i_context,final Activity i_currActivity){
        i_Drawerlist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
                        switch(position) {
                            case 0:
                                view.setBackgroundColor(Color.parseColor("#D3D3D3"));
                                DatabaseReference userSports = User.firebaseRef.child("users").child(User.uid).child("sports");
                                userSports.addListenerForSingleValueEvent(new ValueEventListener() { // get user sports
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        // Get user's favourite sports
                                        ArrayList<String> favouriteSports = new ArrayList<>();
                                        for (DataSnapshot sport : snapshot.getChildren()) {
                                            if(!favouriteSports.contains(sport.getKey().toString()))
                                                favouriteSports.add(sport.getKey().toString());
                                        }
                                        Intent createNewIntent = new Intent(i_currActivity, CreateNewActivity.class);
                                        createNewIntent.putStringArrayListExtra("favourite_sports", favouriteSports);
                                        i_currActivity.startActivity(createNewIntent);
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError firebaseError) {
                                    }
                                });
                                break;
                            case 1:
                                view.setBackgroundColor(Color.parseColor("#D3D3D3"));
                                Intent statisticsNewIntent = new Intent(i_currActivity, StatisticsActivity.class);
                                i_currActivity.startActivity(statisticsNewIntent);
                                break;
                            case 2:
                                view.setBackgroundColor(Color.parseColor("#D3D3D3"));
                                Intent friendIntent = new Intent(i_currActivity, FriendsActivity.class);
                                i_currActivity.startActivity(friendIntent);
                                break;
                            case 3:
                                view.setBackgroundColor(Color.parseColor("#D3D3D3"));
                                Toast.makeText(i_context,"Will be available soon..",Toast.LENGTH_LONG).show();
                                view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                break;
                            case 4:
                                view.setBackgroundColor(Color.parseColor("#D3D3D3"));
                                Intent chatIntent = new Intent(i_currActivity, ChatListActivity.class);
                                i_currActivity.startActivity(chatIntent);
                                break;
                            case 5:
                                view.setBackgroundColor(Color.parseColor("#D3D3D3"));
                                Intent rankingIntent = new Intent(i_currActivity, RankingsNewActivity.class);
                                i_currActivity.startActivity(rankingIntent);
                                //Toast.makeText(i_context,"Will be available soon..",Toast.LENGTH_LONG).show();
                                break;
                            case 6:
                                view.setBackgroundColor(Color.parseColor("#D3D3D3"));
                                Intent helpIntent = new Intent(i_currActivity, SettingsActivity.class);
                                //helpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i_currActivity.startActivity(helpIntent);
                                break;
                        }
                    }});
    }
}*/
