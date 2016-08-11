package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;

public class AskRange extends Activity{

    private ImageView logo;
    private TextView progressText;
    private SeekBar seekBar;
    //private ImageView pseudoSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ask_range);

        logo = (ImageView) findViewById(R.id.logo);
        logo.setImageResource(R.drawable.logo);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        progressText = (TextView) findViewById(R.id.textView5);
        progressText.setText("0 km");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                progressText.setText(progress + " km");
                seekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatabaseReference rangeRef = User.firebaseRef.child("users").child(User.uid).child("range");
                rangeRef.setValue(Integer.parseInt(progressText.getText().subSequence(0, 2).toString()));
                jumpToMainActivity();
            }
        });
    }
    /**
     * Method that jumps to the MainActivity
     */
    public void jumpToMainActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 5 seconds
                Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class); //AskPreferences.class);
                startActivity(goToNextActivity);
                finish();
            }
        }, 0); // Delay time for transition to next activity -> insert any time wanted here instead of 5000
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ask_range, menu);
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
