/*
package larc.ludiconprod.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

import com.facebook.login.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.Sport;
import larc.ludiconprod.Utils.util.Utils;

*/
/**
 * Created by Laur User on 12/29/2015.
 *//*

public class SettingsActivity extends Fragment {

    MyCustomAdapter dataAdapter = null;
    private static View v;
    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ImageButton saveButton;
    private Button logOutButton;

    private TextView progressText;
    private SeekBar seekBar;
     private ArrayList<Sport> sportsList = new ArrayList<Sport>();
    private HashMap<String, Drawable> regularSportIcons = new HashMap<String, Drawable>();
    private HashMap<String, Drawable> desaturatedSportIcons = new HashMap<String, Drawable>();
    DatabaseReference rangeRef = User.firebaseRef.child("users").child(User.uid).child("range");
    DatabaseReference userSports = User.firebaseRef.child("users").child(User.uid).child("sports");
    DatabaseReference user = User.firebaseRef.child("users").child(User.uid);
    final DatabaseReference sportRed = User.firebaseRef.child("sports"); // check user
    private int savedProgress = 0;
    int progress = 0;
    EditText ageText;
    Spinner spinner;
    String sex;
    int counterOfSportsSelected=0;
    boolean spinnerSelected=false;

    final private List<String> changeInSports = new ArrayList<String>();
    public SettingsActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.settings, container,false);
        try {
            //super.onCreate(savedInstanceState);

            // Hide App bar
            // If the Android version is lower than Jellybean, use this call to hide
            // the status bar.
            if (Build.VERSION.SDK_INT < 16) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            // remove title
            //getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
           // getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                   // WindowManager.LayoutParams.FLAG_FULLSCREEN);

            //setContentView(R.layout.settings);

           // initializeLeftSidePanel();

           // User.setImage();

            //get sex from spinner

            spinner = (Spinner) v.findViewById(R.id.sexSpinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.chooseSex_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(spinnerSelected){
                        saveButton.setAlpha(0.9f);
                    }

                sex = parent.getItemAtPosition(position).toString();
                    spinnerSelected = true;

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                    spinnerSelected = false;
                }
            });
            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    spinnerSelected=false;
                    if(snapshot.child("custom-user-data").hasChild("Sex")) {
                        String s = snapshot.child("custom-user-data").child("Sex").getValue().toString();
                        if (s.equals("M")) {
                            spinner.setSelection(0);
                        } else if ( s.equals("F")) {
                            spinner.setSelection(1);
                        }
                    }else {
                        if(snapshot.hasChild("gender")){
                            String s = snapshot.child("gender").getValue().toString();
                            if(s.equals("male")){
                                spinner.setSelection(0);
                            }
                            else{
                                spinner.setSelection(1);
                            }
                        }

                        else{
                            spinner.setSelection(0);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            //set age to AgeTextField
            ageText=(EditText) v.findViewById(R.id.Age);
            ageText.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if(verifyAge() && counterOfSportsSelected >= 1){

                        saveButton.setAlpha((float) 0.9);
                        saveButton.setEnabled(true);
                    }
                    else{
                        saveButton.setAlpha((float) 0.3);
                        saveButton.setEnabled(false);
                    }
                }
            });
            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(snapshot.child("custom-user-data").hasChild("Age")){
                        String s = snapshot.child("custom-user-data").child("Age").getValue().toString();
                        ageText.setText(s);
                        ageText.setSelection(s.length());
                    }else {
                        if (snapshot.hasChild("ageRange")) {
                            String s = snapshot.child("ageRange").getValue().toString();
                            if (s.length() > 3 && s.length() < 8) {   //verify if ageRange is:"{min:xx}" or {max:xx}

                                ageText.setText(s.substring(5, s.length() - 1));
                                ageText.setSelection(s.substring(5, s.length() - 1).length());
                            } else if (s.length() >= 8) {                  //verify if ageRange is:"{min:xx,max:xx}"
                                ageText.setText(s.substring(5, 7));
                                ageText.setSelection(s.substring(5, 7).length());
                            } else {
                                ageText.setText(s);
                                ageText.setSelection(s.length());
                            }


                        } else {
                            ageText.setText("");
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });



            // Set as italic the explanations for fields
            TextView pickSports = (TextView)v.findViewById(R.id.textView7);
            pickSports.setTypeface(pickSports.getTypeface(), Typeface.ITALIC);

            TextView pickArea = (TextView)v.findViewById(R.id.textView9);
            pickArea.setTypeface(pickArea.getTypeface(), Typeface.ITALIC);

            initializeSportIcons();

            rangeRef.addListenerForSingleValueEvent(new ValueEventListener() { // get all sports
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    savedProgress = Integer.parseInt(snapshot.getValue().toString());
                    progressText = (TextView) v.findViewById(R.id.progressText);
                    progressText.setText(savedProgress + " km");
                    seekBar = (SeekBar)v.findViewById(R.id.seekBar2);
                    seekBar.setProgress(savedProgress);
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });

            progressText = (TextView) v.findViewById(R.id.progressText);
            progressText.setText(savedProgress + " km");
            seekBar = (SeekBar) v.findViewById(R.id.seekBar2);
            seekBar.setProgress(savedProgress);
            seekBar.setProgressDrawable(getResources()
                    .getDrawable(R.drawable.progress_bar));

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                    if(seekBar.getProgress() >= 1) {
                        progress = progressValue;
                        //rangeRef.setValue(progress);
                        progressText.setText(progress + " km");
                    }
                    else{
                        progressText.setText("1" + " km");
                        progress=1;
                    }


                    if(changeInSports.contains("R" + progress)){

                        changeInSports.remove("R" + progress);


                    }
                    else {


                        changeInSports.add("R" + progress);

                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if(savedProgress != progress && verifyAge() && counterOfSportsSelected >= 1){
                        saveButton.setAlpha(0.9f);
                        saveButton.setEnabled(true);
                    }
                }
            });

            displayListView();

            TextView hello_message = (TextView) v.findViewById(R.id.hello_message_activity);
            hello_message.setText("");

            //// Create event in header menu
            saveButton= (ImageButton)v.findViewById(R.id.header_button);
            saveButton.setVisibility(View.VISIBLE);
            saveButton.setBackgroundResource(R.drawable.save);
            saveButton.getLayoutParams().height =100;
            saveButton.getLayoutParams().width = 100 ;

            // Initial state - nothing to save yet.
            saveButton.setAlpha((float)0.3);
            saveButton.setEnabled(false);

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //TODO Save Range + Sports in FireBase
                                user.child("custom-user-data").child("Age").setValue(Integer.parseInt(ageText.getText().toString()));
                                user.child("custom-user-data").child("Sex").setValue(sex);
                                rangeRef.setValue((progress == 0 ? savedProgress : progress));
                                Map<String, Object> map = new HashMap<String, Object>();
                                for (Sport s : sportsList) {
                                    if (s.isChecked) {
                                        map.put(s.name, s.id);
                                    }
                                }
                                userSports.setValue(map);
                                Toast.makeText(getActivity().getApplicationContext(), "Updates on sports saved!", Toast.LENGTH_SHORT).show();
                                saveButton.setAlpha((float) 0.3);
                                saveButton.setEnabled(false);
                                changeInSports.clear();
                                Intent intent = new Intent(getActivity().getApplicationContext(), Main.class);
                                startActivity(intent);

                    }
                    catch (Exception exception){
                        Toast.makeText(getActivity().getApplicationContext(),"Introduceti o varsta valida",Toast.LENGTH_LONG).show();
                    }
                }
            });

            logOutButton= (Button)v.findViewById(R.id.logOutButton);
            logOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginManager.getInstance().logOut();
                    //Intent intent = new Intent(getActivity().getApplicationContext(), IntroActivity.class);
                    //startActivity(intent);
                }
            });




        }
        catch(Exception e) {
            Utils.quit();
        }
        return v;
    }

    final String[] sports = new String[]{"football", "volleyball", "basketball", "squash", "pingpong", "tennis",
            "cycling", "jogging", "gym", "other"};

    private void initializeSportIcons() {
        int imageResource;


        for (String sport : sports) {
            imageResource = getResources().getIdentifier("@drawable/" + sport, null, getActivity().getPackageName());
            regularSportIcons.put(sport, getResources().getDrawable(imageResource));
            //imageResource = getResources().getIdentifier("@drawable/desaturated_" + sport, null, getPackageName());
            //desaturatedSportIcons.put(sport, getResources().getDrawable(imageResource));
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
                                ((BitmapDrawable) regularSportIcons.get(sport.getKey())).getBitmap()
                               ));
                        exist.put(sport.getKey(), true);
                        counterOfSportsSelected++;
                    }

                    int count = 0;
                    for (String sport : sports) {
                        if (!exist.containsKey(sport)) {
                            sportsList.add(new Sport(sport, Integer.toString(count) , false,
                                    ((BitmapDrawable) regularSportIcons.get(sport)).getBitmap()));
                        }
                        count ++;
                    }

                    //create an ArrayAdaptor from the Sport Array
                    dataAdapter = new MyCustomAdapter(getActivity().getApplicationContext(), R.layout.sport_info, sportsList);
                    GridView gridView = (GridView) v.findViewById(R.id.gridView);
                    // Assign adapter to ListView
                    gridView.setAdapter(dataAdapter);


                    */
/*
                    sportRed.addListenerForSingleValueEvent(new ValueEventListener() { // get the rest of the sports
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            for (DataSnapshot sport : snapshot.getChildren()) {
                                if (!exist.containsKey(sport.getKey())) {
                                    sportsList.add(new Sport(sport.getKey(), sport.child("id").getValue().toString(), false,
                                            ((BitmapDrawable) regularSportIcons.get(sport.getKey())).getBitmap()));
                                }
                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            //User.firebaseRef.child("msge").setValue("The read failed: " + firebaseError.getMessage());
                        }
                    });
                        *//*



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
    public boolean verifyAge(){
        try{
            if(!ageText.getText().toString().isEmpty()) {

                int age = Integer.parseInt(ageText.getText().toString());
                if (age > 0 && age < 150) {
                    return true;
                } else {
                    return false;
                }
            }
            else{
                return false;
            }

        }
        catch(Exception e){
            return false;
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
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
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
                            rl.setBackground(getResources().getDrawable(R.drawable.settings_icon_selected));
                            //((ImageView) rl.getChildAt(1)).setImageBitmap(sport.icon);
                            cb.setAlpha((float) 0.9);
                            counterOfSportsSelected++;
                            if(verifyAge()) {                //if already selected a sports remain to verifyAge
                                saveButton.setAlpha((float) 0.9);
                                saveButton.setEnabled(true);
                            }

                        } else {
                            rl.setBackground(getResources().getDrawable(R.drawable.settings_icon_notselected));
                            //((ImageView) rl.getChildAt(1)).setImageBitmap(sport.desaturated_icon);
                            cb.setAlpha((float) 0.7);
                            counterOfSportsSelected--;
                            if(counterOfSportsSelected == 0){
                                saveButton.setAlpha((float) 0.3);
                                saveButton.setEnabled(false);
                            }
                            else if(verifyAge())
                            {
                                saveButton.setAlpha((float) 0.9);
                                saveButton.setEnabled(true);
                            }
                        }

                        // Save button changes
                        if(changeInSports.contains(sport.id)){
                            changeInSports.remove(sport.id);

                            if(changeInSports.size() == 0){
                                saveButton.setAlpha((float)0.3);
                                saveButton.setEnabled(false);
                            }
                        }
                        else {
                            changeInSports.add(sport.id);

                            if(changeInSports.size()== 1){
                                    saveButton.setAlpha((float)1);
                                    saveButton.setEnabled(true);
                            }
                        }


                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Sport sport = sportsList.get(position);

            if(sport.name.equalsIgnoreCase("pingpong")){
                holder.text.setText("ping pong");
            }
            else {
                holder.text.setText(sport.name);
            }
            holder.box.setText("");
            holder.box.setChecked(sport.isChecked);
            holder.box.setTag(sport);
            holder.text.setTextColor(getResources().getColor(R.color.black));
            if (holder.box.isChecked()) {
                holder.rl.setBackground(getResources().getDrawable(R.drawable.settings_icon_selected));
                holder.image.setImageBitmap(sport.icon);
                holder.box.setTextColor(getResources().getColor(R.color.white));
                holder.box.setAlpha((float) 0.9);
            } else {
                holder.rl.setBackground(getResources().getDrawable(R.drawable.settings_icon_notselected));
                holder.image.setImageBitmap(sport.icon);
                //holder.image.setImageBitmap(sport.desaturated_icon);
                holder.box.setAlpha((float) 0.7);
            }

            return convertView;

        }
    }

    // Left side menu
    */
/*
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
    *//*


    // Delete the history stack and point to Main activity
    public void onBackPressed() {
        Intent toMain = new Intent(getActivity(),Main.class);
        toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMain);
    }

}
*/
