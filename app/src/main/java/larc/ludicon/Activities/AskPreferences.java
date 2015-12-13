package larc.ludicon.Activities;

import larc.ludicon.Activities.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import larc.ludicon.R;
import larc.ludicon.Utils.Popup;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
                // TODO INSERT PARSE PREFERENCES UPDATE METHOD
                // TODO USE sportsList below(array with sports containing name, id, isChecked )
                ArrayList<Sport> sportsList = dataAdapter.sportsList;

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        checkButtonClick(); // Button which checks if it's ok
    }

    private void displayListView() {

        // TODO Query in Sports Table and add all of them to sportsList with ( name, id, isChecked = false )
        // TODO Query in User Table and see which sports are preferred by User and mark them with isChecked = true
        //Array list of sports : name, id, isChecked
        ArrayList <Sport> sportsList = new ArrayList<Sport>();
        sportsList.add(new Sport("Fotbal","1",false));
        sportsList.add(new Sport("Ping Pong","2",false));
        sportsList.add(new Sport("Tenis","3",false));
        sportsList.add(new Sport("Baschet","4",false));
        sportsList.add(new Sport("Alergare","5",false));
        sportsList.add(new Sport("Volei","6",false));
        sportsList.add(new Sport("Sah","7",false));

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