package larc.ludicon.Activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import larc.ludicon.R;
import larc.ludicon.UserInfo.User;

public class AskRange extends AppCompatActivity {

    private ImageView logo;
    private TextView progressText;
    private SeekBar seekBar;
    private ImageView pseudoSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_range);

        logo = (ImageView) findViewById(R.id.logo);
        logo.setImageResource(R.drawable.logo);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setAlpha(0);
        progressText = (TextView) findViewById(R.id.textView5);
        progressText.setText("10 km");
        pseudoSeekBar = (ImageView) findViewById(R.id.visibleSeekBar);
        String uri = "@drawable/range10";
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        pseudoSeekBar.setImageDrawable(res);

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
                progressText.setText(barOffset * 10 + " km");
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
                //progressText.setText(progress + " km");
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
