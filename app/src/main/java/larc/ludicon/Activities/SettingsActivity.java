package larc.ludicon.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import larc.ludicon.Adapters.LeftPanelItemClicker;
import larc.ludicon.Adapters.LeftSidePanelAdapter;
import larc.ludicon.R;
import larc.ludicon.UserInfo.User;
import larc.ludicon.Utils.Sport;
import larc.ludicon.Utils.util.Utils;

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
    private Button saveButton;
    private ArrayList<Sport> sportsList = new ArrayList<Sport>();
    private HashMap<String, Drawable> regularSportIcons = new HashMap<String, Drawable>();
    private HashMap<String, Drawable> desaturatedSportIcons = new HashMap<String, Drawable>();
    DatabaseReference rangeRef = User.firebaseRef.child("users").child(User.uid).child("range");
    DatabaseReference userSports = User.firebaseRef.child("users").child(User.uid).child("sports");
    final DatabaseReference sportRed = User.firebaseRef.child("sports"); // check user
    private int savedProgress = 0;
    int progress = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.settings);

            initializeLeftSidePanel();

            User.setImage();

            // User picture and name for Left side Panel
            TextView userName = (TextView) findViewById(R.id.userName);
            userName.setText(User.getFirstName(getApplicationContext()) + " " + User.getLastName(getApplicationContext()));

            ImageView userPic = (ImageView) findViewById(R.id.userPicture);
            Drawable d = new BitmapDrawable(getResources(), User.image);
            userPic.setImageDrawable(d);
            userPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    SettingsActivity.this.startActivity(mainIntent);
                }
            });

            initializeSportIcons();

            rangeRef.addListenerForSingleValueEvent(new ValueEventListener() { // get all sports
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    savedProgress = Integer.parseInt(snapshot.getValue().toString());
                    progressText = (TextView) findViewById(R.id.progressText);
                    progressText.setText(savedProgress + " km");
                    seekBar = (SeekBar) findViewById(R.id.seekBar2);
                    seekBar.setProgress(savedProgress);
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });

            progressText = (TextView) findViewById(R.id.progressText);
            progressText.setText(savedProgress + " km");
            seekBar = (SeekBar) findViewById(R.id.seekBar2);
            seekBar.setProgress(savedProgress);
            seekBar.setProgressDrawable(getResources()
                    .getDrawable(R.drawable.progress_bar));

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                    progress = progressValue;
                    //rangeRef.setValue(progress);
                    progressText.setText(progress + " km");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            displayListView();

            TextView hello_message = (TextView) findViewById(R.id.hello_message_activity);
            hello_message.setText("Settings");

        // Save button
        saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Save Range + Sports in FireBase
                rangeRef.setValue((progress == 0 ? savedProgress : progress));
                Map<String, Object> map = new HashMap<String, Object>();
                for (Sport s : sportsList) {
                    if (s.isChecked) {
                        map.put(s.name, s.id);
                    }
                }
                userSports.setValue(map);
                Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();


            }
        });

        }
        catch(Exception exc) {
            Utils.quit();
        }
    }

    private void initializeSportIcons() {
        int imageResource;
        String[] sports = new String[]{"basketball", "cycling", "football", "gym", "jogging", "other",
                           "pingpong", "squash", "tennis", "volleyball"};

        for (String sport : sports) {
            imageResource = getResources().getIdentifier("@drawable/" + sport, null, getPackageName());
            regularSportIcons.put(sport, getResources().getDrawable(imageResource));
            imageResource = getResources().getIdentifier("@drawable/desaturated_" + sport, null, getPackageName());
            desaturatedSportIcons.put(sport, getResources().getDrawable(imageResource));
        }
    }

    private void displayListView() {
        //Array list of sports : name, id, isChecked, icon
        // Asa iau datele din cloud

        try {
            final HashMap<String, Boolean> exist = new HashMap<String, Boolean>();
            userSports.addListenerForSingleValueEvent(new ValueEventListener() { // get user sports
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot sport : snapshot.getChildren()) {
                        sportsList.add(new Sport(sport.getKey(), sport.getValue().toString(), true,
                                ((BitmapDrawable) regularSportIcons.get(sport.getKey())).getBitmap(),
                                ((BitmapDrawable) desaturatedSportIcons.get(sport.getKey())).getBitmap()));
                        exist.put(sport.getKey(), true);
                    }

                    sportRed.addListenerForSingleValueEvent(new ValueEventListener() { // get the rest of the sports
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            for (DataSnapshot sport : snapshot.getChildren()) {
                                if (!exist.containsKey(sport.getKey())) {
                                    sportsList.add(new Sport(sport.getKey(), sport.child("id").getValue().toString(), false,
                                            ((BitmapDrawable) regularSportIcons.get(sport.getKey())).getBitmap(),
                                            ((BitmapDrawable) desaturatedSportIcons.get(sport.getKey())).getBitmap()));
                                }
                            }

                            //create an ArrayAdaptor from the Sport Array
                            dataAdapter = new MyCustomAdapter(getApplicationContext(), R.layout.sport_info, sportsList);
                            GridView gridView = (GridView) findViewById(R.id.gridView);
                            // Assign adapter to ListView
                            gridView.setAdapter(dataAdapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            //User.firebaseRef.child("msge").setValue("The read failed: " + firebaseError.getMessage());
                        }
                    });


                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });

        }
        catch(Exception exc) {
            Utils.quit();
        }
    }

    private class MyCustomAdapter extends ArrayAdapter<Sport> {

        private ArrayList<Sport> sportsList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Sport> sList) {
            super(context, textViewResourceId, sList);
            this.sportsList = new ArrayList<>();
            this.sportsList.addAll(sList);
        }

        private class ViewHolder {
            RelativeLayout rl;
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
                holder.rl = (RelativeLayout) convertView.findViewById(R.id.irLayout);
                holder.box = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.text = (TextView) convertView.findViewById(R.id.code);
                holder.image = (ImageView) convertView.findViewById(R.id.icon);

                convertView.setTag(holder);

                holder.box.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Sport sport = (Sport) cb.getTag();
                        RelativeLayout rl = (RelativeLayout) cb.getParent();
                        sport.setSelected(cb.isChecked());
                        if (cb.isChecked()) {
                            rl.setBackgroundColor(getResources().getColor(R.color.bg1));
                            ((ImageView) rl.getChildAt(1)).setImageBitmap(sport.icon);
                            cb.setAlpha((float) 0.9);
                        } else {
                            rl.setBackgroundColor(getResources().getColor(R.color.bg2));
                            ((ImageView) rl.getChildAt(1)).setImageBitmap(sport.desaturated_icon);
                            cb.setAlpha((float) 0.7);
                        }

                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Sport sport = sportsList.get(position);
            holder.text.setText(sport.name);
            holder.box.setText("");
            holder.box.setChecked(sport.isChecked);
            holder.box.setTag(sport);
            holder.text.setTextColor(getResources().getColor(R.color.white));
            if (holder.box.isChecked()) {
                holder.rl.setBackgroundColor(getResources().getColor(R.color.bg1));
                holder.image.setImageBitmap(sport.icon);
                holder.box.setTextColor(getResources().getColor(R.color.white));
                holder.box.setAlpha((float) 0.9);
            } else {
                holder.box.setTextColor(getResources().getColor(R.color.white));
                holder.rl.setBackgroundColor(getResources().getColor(R.color.bg2));
                holder.image.setImageBitmap(sport.desaturated_icon);
                holder.box.setAlpha((float) 0.7);
            }

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
                mDrawerList.bringToFront();
                mDrawerLayout.requestLayout();
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

    // Delete the history stack and point to Main activity
    @Override
    public void onBackPressed() {
        Intent toMain = new Intent(this,MainActivity.class);
        toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMain);
    }

}
