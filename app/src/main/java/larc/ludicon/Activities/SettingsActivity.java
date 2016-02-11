package larc.ludicon.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import larc.ludicon.Adapters.LeftPanelItemClicker;
import larc.ludicon.Adapters.LeftSidePanelAdapter;
import larc.ludicon.R;
import larc.ludicon.UserInfo.User;

/**
 * Created by Laur User on 12/29/2015.
 */
public class SettingsActivity  extends Activity {

    MyCustomAdapter dataAdapter = null;

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        displayListView();

        TextView hello_message = (TextView) findViewById(R.id.hello_message_activity);
        hello_message.setText("Settings");

        ImageButton showPanel = (ImageButton) findViewById(R.id.showPanel);
        showPanel.setBackground(null);
        showPanel.setBackgroundResource(R.drawable.back_arr);
        showPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        // Logout button
        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // logout from facebook
                LoginManager.getInstance().logOut();
                //clear UserInfo when logout
                User.clear(getApplicationContext());
                //go back to IntroActivity
                Intent goToIntro = new Intent(getApplicationContext(), IntroActivity.class);
                goToIntro.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goToIntro);
            }
        });

    }
    public static void setSportsList ( ArrayList<Sport> sourceList, ArrayList<Sport> destList)
    {
        for ( Sport s : sourceList)
            destList.add(s);
    }
    private void displayListView() {
        //Array list of sports : name, id, isChecked
        // Asa iau datele din cloud
        Firebase sportRed = User.firebaseRef.child("sports"); // chech user
        sportRed.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                ArrayList<Sport> sportsList = new ArrayList<Sport>();

                for (DataSnapshot sport: snapshot.getChildren()) {
                   sportsList.add(new Sport(sport.getKey(),sport.child("id").getValue().toString(),false));
                }

                //create an ArrayAdaptor from the Sport Array
                dataAdapter = new MyCustomAdapter(getApplicationContext(), R.layout.sport_info, sportsList);
                ListView listView = (ListView) findViewById(R.id.listView);
                // Assign adapter to ListView
                listView.setAdapter(dataAdapter);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // When clicked, show a toast with the TextView text
                        Sport sport = (Sport) parent.getItemAtPosition(position);
                        Toast.makeText(getApplicationContext(),
                                "Clicked on Row: " + sport.name,
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //User.firebaseRef.child("msge").setValue("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Sport> {

        private ArrayList<Sport> sportsList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Sport> sList) {
            super(context, textViewResourceId, sList);
            this.sportsList = new ArrayList<Sport>();
            this.sportsList.addAll(sList);
        }

        private class ViewHolder {
            ImageView image;
            TextView text;
            CheckBox box;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.sport_info, null);

                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.code);
                holder.box = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.box.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Sport sport = (Sport) cb.getTag();
                        sport.setSelected(cb.isChecked());
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Sport sport = sportsList.get(position);
            holder.text.setText("");
            holder.box.setText(sport.name);
            holder.box.setChecked(sport.isChecked);
            holder.box.setTag(sport);

            return convertView;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
