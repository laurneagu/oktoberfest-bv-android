package larc.ludicon.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.AccessToken;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import larc.ludicon.R;

public class FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                            /* handle the result */
                        try{
                            JSONArray friendsList = response.getJSONObject().getJSONArray("data");
                            ArrayList<String> friends = new ArrayList<>();
                            for (int l=0; l < friendsList.length(); l++) {
                                friends.add(friendsList.getJSONObject(l).getString("name"));
                            }
                            // friends contains all the names of my Facebook friends who use Ludicon

                            ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_listview, friends);
                            ListView listView = (ListView) findViewById(R.id.friends_listView);
                            listView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }
    /*
    public class Friend {
        String name;
        URL imagePath;
        public Friend(String name)
        {
            this.name = name;
        }
    }
    public class CustomListAdapter extends ArrayAdapter<String> {

        private ArrayList<Friend> friendList;

        public CustomListAdapter(Context context, ArrayList<Friend> fList) {

            super(context, R.layout.friends_layout);
            this.friendList = new ArrayList<Friend>();
            this.friendList.addAll(fList);
        }

        private class ViewHolder {
            ImageView image;
            TextView text;
            Button details;
            Button chat;
        }
        @Override
        public View getView(int position,View convertView,ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.friends_layout, null);

                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.item);
                holder.chat = (Button) convertView.findViewById(R.id.chat_button);
                holder.details = (Button) convertView.findViewById(R.id.details_button);
                holder.image = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Friend friend = friendList.get(position);
            Log.v("Aici", friend.name);
            holder.text.setText(friend.name);
            //holder.image.setImageResource(...)
            return convertView;
        };
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
