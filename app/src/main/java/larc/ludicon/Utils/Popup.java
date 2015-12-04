package larc.ludicon.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import larc.ludicon.R;
import larc.ludicon.UserInfo.User;

/**
 * Created by Ciprian on 12/1/2015.
 */
public class Popup extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow);
        // Get Display width + height
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        // Set the Popup Window proportional to width,height
        getWindow().setLayout((int)(width*0.8),(int)(height*0.8));
        TextView firstName = (TextView)findViewById(R.id.textView1);
        firstName.setText(User.getFirstName(getApplicationContext()));
        TextView lastName = (TextView)findViewById(R.id.textView2);
        lastName.setText(User.getLastName(getApplicationContext()));
        TextView email = (TextView)findViewById(R.id.textView3);
        email.setText(User.getEmail(getApplicationContext()));
    }

}
