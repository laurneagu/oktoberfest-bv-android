package larc.ludicon.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import larc.ludicon.R;
import larc.ludicon.UserInfo.User;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class AskPreferences extends Activity {

    MyCustomAdapter dataAdapter = null;
    public class Sport{
        public String name;
        public String id;
        public boolean isChecked;
        public Sport (String name, String id, boolean isChecked)
        {
            this.name = name;
            this.id = id;
            this.isChecked = isChecked;
        }
        public void setSelected(boolean value)
        {
            this.isChecked = value;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_preferences);
        //Generate list View from ArrayList

        displayListView();

        Button selectSportsButton = (Button)findViewById(R.id.selectSportsButton);
        selectSportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  INSERT PARSE PREFERENCES UPDATE METHOD
                // USE sportsList below(array with sports containing name, id, isChecked )
                ArrayList<Sport> sportsList = dataAdapter.sportsList;
                //List<String> sportArray = new ArrayList<String>();
                try {
                    ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                    userQuery.whereEqualTo("email", User.getEmail(getApplicationContext()));
                    List<ParseUser> userResults = userQuery.find();
                    for ( ParseObject pU : userResults )
                    {
                        for ( Sport s : sportsList )
                        {
                            if( s.isChecked == true ) {
                                pU.addUnique("sports",s.id);
                            }
                           /* else
                            {   List<String> list = new ArrayList<String>();
                                list.add(s.name);
                                pU.removeAll("sports",list);
                            }*/

                        }
                        pU.save();
                    }
                }
                catch(Exception exc)
                {}
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        checkButtonClick(); // Button which checks if it's ok
    }

    private void displayListView() {
        //Array list of sports : name, id, isChecked
        ArrayList <Sport> sportsList = new ArrayList<Sport>();

        //Query in Sports Table and add all of them to sportsList with ( name, id, isChecked = false )
        ParseQuery <ParseObject> sportsQuery = ParseQuery.getQuery("Sport");
        try {
            List<ParseObject> results = sportsQuery.find();
            for ( ParseObject pObj : results)
            {
                    sportsList.add(new Sport( (String)(pObj.get("name")),pObj.getObjectId(),false));
            }
        }
        catch(Exception exc)
        {}
        // Query in User Table and see which sports are preferred by User and mark them with isChecked = true
        try {
            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            userQuery.whereEqualTo("email", User.getEmail(getApplicationContext()));
            List<ParseUser> userResults = userQuery.find();
            for ( ParseUser pU : userResults )
            {
                List<String> userSportList = pU.getList("sports");
                for ( String str : userSportList )
                {
                    for ( Sport s : sportsList )
                    {
                        if ( str.equalsIgnoreCase(s.id) )
                                s.isChecked = true;
                    }
                }
            }
        }
        catch(Exception exc)
        {}
        //create an ArrayAdaptor from the Sport Array
        dataAdapter = new MyCustomAdapter(getApplicationContext() , R.layout.sport_info, sportsList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new OnItemClickListener() {
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
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.sport_info, null);

                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.code);
                holder.box = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.box.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Sport sport = (Sport) cb.getTag();
                        sport.setSelected(cb.isChecked());
                    }
                });
            }
            else {
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
    // Method which verifies if the items have been selected
    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<Sport> sportsList = dataAdapter.sportsList;
                for(int i=0;i<sportsList.size();i++){
                    Sport sport = sportsList.get(i);
                    if(sport.isChecked == true){
                        responseText.append("\n" + sport.name);
                    }
                }

                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();

            }
        });

    }
}