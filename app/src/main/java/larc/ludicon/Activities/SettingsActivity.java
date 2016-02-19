package larc.ludicon.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
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
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import larc.ludicon.Adapters.LeftPanelItemClicker;
import larc.ludicon.Adapters.LeftSidePanelAdapter;
import larc.ludicon.R;
import larc.ludicon.UserInfo.User;

/**
 * Created by Laur User on 12/29/2015.
 */
public class SettingsActivity extends Activity {

    MyCustomAdapter dataAdapter = null;

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;


    private TextView progressText;
    private SeekBar seekBar;
    private ImageView pseudoSeekBar;
    private Button saveButton;
    private ArrayList<Sport> sportsList = new ArrayList<Sport>();
    Firebase rangeRef = User.firebaseRef.child("users").child(User.uid).child("range");
    Firebase userSports = User.firebaseRef.child("users").child(User.uid).child("sports");
    final Firebase sportRed = User.firebaseRef.child("sports"); // check user
    private int savedProgress = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mDrawerList = (ListView) findViewById(R.id.leftMenu);
        initializeLeftSidePanel();

        User.setImage();

        // User picture and name for HEADER MENU
        TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText(User.getFirstName(getApplicationContext()) + " " + User.getLastName(getApplicationContext()));

        ImageView userPic = (ImageView) findViewById(R.id.userPicture);
        Drawable d = new BitmapDrawable(getResources(), User.image);
        userPic.setImageDrawable(d);

        //TODO Get Range from FireBase and put it in savedProgress


        rangeRef.addListenerForSingleValueEvent(new ValueEventListener() { // get all sports
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                savedProgress = Integer.parseInt(snapshot.getValue().toString());
                int barOffset = (savedProgress + 5) / 10;
                if (barOffset < 1) {
                    barOffset = 1;
                } else if (barOffset > 9) {
                    barOffset = 9;
                }
                progressText = (TextView) findViewById(R.id.progressText);
                progressText.setText(barOffset * 10 + " km");
                seekBar = (SeekBar) findViewById(R.id.seekBar2);
                seekBar.setProgress(barOffset * 10);
                seekBar.setAlpha(0);


                pseudoSeekBar = (ImageView) findViewById(R.id.visibleSeekBar2);

                String uri = "@drawable/range" + barOffset + "0";
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                pseudoSeekBar.setImageDrawable(res);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        int barOffset = (savedProgress + 5) / 10;
        if (barOffset < 1) {
            barOffset = 1;
        } else if (barOffset > 9) {
            barOffset = 9;
        }
        progressText = (TextView) findViewById(R.id.progressText);
        progressText.setText(barOffset * 10 + " km");
        seekBar = (SeekBar) findViewById(R.id.seekBar2);
        seekBar.setProgress(savedProgress);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                int barOffset = (progress + 5) / 10;
                if (barOffset < 1) {
                    barOffset = 1;
                } else if (barOffset > 9) {
                    barOffset = 9;
                }
                rangeRef.setValue(barOffset * 10);
                progressText.setText(barOffset * 10 + " km");
                pseudoSeekBar = (ImageView) findViewById(R.id.visibleSeekBar2);

                String uri = "@drawable/range" + barOffset + "0";
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                pseudoSeekBar.setImageDrawable(res);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //progressText.setText(barOffset * 10 + " km");
            }
        });

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

        /*// Save button
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Save Range + Sports in FireBase

                Map<String, Object> map = new HashMap<String, Object>();
                for (Sport s : sportsList) {
                    if (s.isChecked) {
                        map.put(s.name, s.id);
                    }
                }
                userSports.setValue(map);



            }
        });
        */

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

    private void displayListView() {
        //Array list of sports : name, id, isChecked, icon
        // Asa iau datele din cloud


        final HashMap<String, Boolean> exist = new HashMap<String, Boolean>();
        userSports.addListenerForSingleValueEvent(new ValueEventListener() { // get user sports
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot sport : snapshot.getChildren()) {
                    String uri = "@drawable/" + sport.getKey().toLowerCase().replace(" ", "");
                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                    Drawable res = getResources().getDrawable(imageResource);
                    sportsList.add(new Sport(sport.getKey(), sport.getValue().toString(),
                            true, ((BitmapDrawable)res).getBitmap()));
                    exist.put(sport.getKey(), true);
                }

                sportRed.addListenerForSingleValueEvent(new ValueEventListener() { // get the rest of the sports
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot sport : snapshot.getChildren()) {
                            if (!exist.containsKey(sport.getKey())) {
                                String uri = "@drawable/" + sport.getKey();
                                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                                Drawable res = getResources().getDrawable(imageResource);
                                sportsList.add(new Sport(sport.child("name").getValue().toString(), sport.child("id").getValue().toString(),
                                        false, ((BitmapDrawable)res).getBitmap()));
                            }
                        }

                        //create an ArrayAdaptor from the Sport Array
                        dataAdapter = new MyCustomAdapter(getApplicationContext(), R.layout.sport_info, sportsList);
                        GridView gridView = (GridView) findViewById(R.id.gridView);
                        // Assign adapter to ListView
                        gridView.setAdapter(dataAdapter);


                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

            @Override
            public void onCancelled(FirebaseError firebaseError) {
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
                holder.image = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);

                holder.box.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Sport sport = (Sport) cb.getTag();
                        sport.setSelected(cb.isChecked());

                        // Update firebase
                        Map<String, Object> map = new HashMap<String, Object>();
                        for (Sport s : sportsList) {
                            if (s.isChecked) {
                                map.put(s.name, s.id);
                            }
                        }
                        userSports.setValue(map);
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
            holder.image.setImageBitmap(sport.icon);

            return convertView;

        }

    }

    // Left side menu

    public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_settings);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, SettingsActivity.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), SettingsActivity.this);

        final ImageButton showPanel = (ImageButton) findViewById(R.id.showPanel);
        showPanel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        // Toggle efect on left side panel
        mDrawerToggle = new android.support.v4.app.ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

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


        // Left side panel
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
