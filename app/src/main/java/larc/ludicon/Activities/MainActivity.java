package larc.ludicon.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import larc.ludicon.R;
import larc.ludicon.UserInfo.User;
import larc.ludicon.Utils.CloudConnection.CloudConnection;
import larc.ludicon.Utils.CloudConnection.ParseConnection;
import larc.ludicon.Utils.Popup;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button popupButton = (Button)findViewById(R.id.PopUpbutton);

          /* Initialize Parse connection */
        // Parse.initialize(this, "7ynxx7uuHFrR4b5tEDDv3yEOPIFhcjsdSIUfDGxh", "sVYUFfdDYLmuqhxU9pxSVvdxRioC3jurlNJb41cw");

        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Popup.class));
            }
        });

//        TODO - Here - Andrei and Ciprian merge the code for users
//        This is an example (add a user to cloud) - we should do the same:

//        ParseUser user = new ParseUser();
//        user.setUsername("bulaDPeMarte");
//        user.setPassword("MartianaMea");
//        user.setEmail("martianuxxax@marte.mr");

// other fields can be set just like with ParseObject
//        user.put("phone", "650-555-0010");
//
//        user.signUpInBackground(new SignUpCallback() {
//            public void done(ParseException e) {
//                if (e == null) {
//                    // Hooray! Let them use the app now.
//                } else {
//                    // Sign up didn't succeed. Look at the ParseException
//                    // to figure out what went wrong
//                }
//            }
//        });



        Button fakeLogin = (Button)findViewById(R.id.fakeLoginButton);
        fakeLogin.setOnClickListener(new View.OnClickListener() {
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

        return super.onOptionsItemSelected(item);
    }
}
