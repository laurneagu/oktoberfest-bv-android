package larc.ludicon.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;

import larc.ludicon.R;
import larc.ludicon.UserInfo.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.facebook.login.LoginManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

//import com.parse.ParseObject;
//import com.parse.ParseQuery;
//import com.parse.ParseUser;


class Sport {
    public String name;
    public String id;
    public boolean isChecked;
    public Bitmap icon;

    public Sport(String name, String id, boolean isChecked, Bitmap icon) {
        this.name = name;
        this.id = id;
        this.isChecked = isChecked;
        this.icon = icon;
    }

    public void setSelected(boolean value) {
        this.isChecked = value;
    }
}

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

                Firebase sportsRef = User.firebaseRef.child("users").child(User.uid).child("sports");
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
        //Array list of sports : name, id, isChecked
        Firebase sportRef = User.firebaseRef.child("sports"); // check user
        sportRef.addListenerForSingleValueEvent(new ValueEventListener() { // get sports
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot sport: snapshot.getChildren()) {
                    byte[] imageAsBytes = Base64.decode(sport.child("icon").getValue().toString(),
                            Base64.DEFAULT);
                    sports.add(new Sport(sport.getKey(), sport.child("id").getValue().toString(),
                            false, BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)));
                }




                dataAdapter = new MyCustomAdapter(getApplicationContext(), R.layout.sport_info, sports);
                GridView gridView = (GridView) findViewById(R.id.gridView1);
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
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Sport sport = sportsList.get(position);
            holder.text.setText("");
            holder.box.setText(sport.name);
            holder.box.setChecked(sport.isChecked);
            holder.image.setImageBitmap(sport.icon);
            holder.box.setTag(sport);

            return convertView;

        }

    }


}