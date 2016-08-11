package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.Sport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

//import com.parse.ParseObject;
//import com.parse.ParseQuery;
//import com.parse.ParseUser;


public class AskPreferences extends Activity {
    private ImageView logo;
    MyCustomAdapter dataAdapter = null;
    public ArrayList<Sport> sports = new ArrayList<Sport>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_preferences);
        logo = (ImageView) findViewById(R.id.logo);
        logo.setImageResource(R.drawable.logo);
        //Generate list View from ArrayList

        displayListView();

        // Next button
        Button logout = (Button) findViewById(R.id.button2);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference sportsRef = User.firebaseRef.child("users").child(User.uid).child("sports");
                Map<String, Object> map = new HashMap<String, Object>();
                for(Sport s : sports){
                    if(s.isChecked){
                        map.put(s.name,s.id);
                    }
                }
                sportsRef.setValue(map);
                
                jumpToRangeActivity();
            }
        });

    }

    public void jumpToRangeActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 5 seconds
                Intent goToNextActivity = new Intent(getApplicationContext(), AskRange.class); //AskPreferences.class);
                startActivity(goToNextActivity);
                finish();
            }
        }, 0); // Delay time for transition to next activity -> insert any time wanted here instead of 5000
    }

    private void displayListView() {
        //Array list of sports : name, id, isChecked, icon
        DatabaseReference sportRef = User.firebaseRef.child("sports"); // check user
        sportRef.addListenerForSingleValueEvent(new ValueEventListener() { // get sports
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot sport: snapshot.getChildren()) {
                    String uri = "@drawable/" + sport.getKey();
                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                    Drawable res1 = getResources().getDrawable(imageResource);
                    uri = "@drawable/desaturated_" + sport.getKey();
                    imageResource = getResources().getIdentifier(uri, null, getPackageName());
                    Drawable res2 = getResources().getDrawable(imageResource);

                    sports.add(new Sport(sport.getKey(), sport.child("id").getValue().toString(),
                            false, ((BitmapDrawable) res1).getBitmap(), ((BitmapDrawable) res1).getBitmap()));
                }




                dataAdapter = new MyCustomAdapter(getApplicationContext(), R.layout.sport_info, sports);
                GridView gridView = (GridView) findViewById(R.id.gridView1);
                // Assign adapter to ListView
                gridView.setAdapter(dataAdapter);
/*
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // When clicked, show a toast with the TextView text
                        Sport sport = (Sport) parent.getItemAtPosition(position);
                        Toast.makeText(getApplicationContext(),
                                "Clicked on Row: " + sport.name,
                                Toast.LENGTH_LONG).show();
                    }
                });*/
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                //User.firebaseRef.child("msge").setValue("The read failed: " + firebaseError.getMessage());
            }
        });


//        //Query in Sports Table and add all of them to sportsList with ( name, id, isChecked = false )
//        ParseQuery <ParseObject> sportsQuery = ParseQuery.getQuery("Sport");
//        try {
//            List<ParseObject> results = sportsQuery.find();
//            for ( ParseObject pObj : results)
//            {
//                    sportsList.add(new Sport( (String)(pObj.get("name")),pObj.getObjectId(),false));
//            }
//        }
//        catch(Exception exc)
//        {}
//        // Query in User Table and see which sports are preferred by User and mark them with isChecked = true
//        try {
//            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
//            userQuery.whereEqualTo("email", User.getEmail(getApplicationContext()));
//            List<ParseUser> userResults = userQuery.find();
//            for ( ParseUser pU : userResults )
//            {
//                List<String> userSportList = pU.getList("sports");
//                for ( String str : userSportList )
//                {
//                    for ( Sport s : sportsList )
//                    {
//                        if ( str.equalsIgnoreCase(s.id) )
//                                s.isChecked = true;
//                    }
//                }
//            }
//        }
//        catch(Exception exc)
//        {}
        //create an ArrayAdaptor from the Sport Array


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
                holder.text = (TextView) convertView.findViewById(R.id.code);
                holder.box = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.image = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);

                holder.box.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Sport sport = (Sport) cb.getTag();
                        sport.setSelected(cb.isChecked());
                        RelativeLayout rl = (RelativeLayout)cb.getParent();
                        sport.setSelected(cb.isChecked());
                        if(cb.isChecked()) {
                            rl.setBackgroundColor(Color.parseColor("#777777"));
                            ((ImageView)rl.getChildAt(1)).setImageBitmap(sport.icon);
                            cb.setAlpha(1);
                        } else {
                            rl.setBackgroundColor(Color.parseColor("#bbbbbb"));
                            ((ImageView)rl.getChildAt(1)).setImageBitmap(sport.desaturated_icon);
                            cb.setAlpha((float) 0.5);
                        }
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Sport sport = sportsList.get(position);
            holder.text.setText(sport.name);
            holder.text.setTextColor(getResources().getColor(R.color.white));
            holder.box.setText("");
            holder.box.setChecked(sport.isChecked);
            holder.box.setTag(sport);
            if(holder.box.isChecked()) {
                holder.rl.setBackgroundColor(Color.parseColor("#777777"));
                holder.image.setImageBitmap(sport.icon);
                holder.box.setAlpha(1);
            } else {

                holder.rl.setBackgroundColor(Color.parseColor("#bbbbbb"));
                holder.image.setImageBitmap(sport.desaturated_icon);
                holder.box.setAlpha((float)0.5);
            }

            return convertView;

        }

    }


}