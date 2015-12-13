package larc.ludicon.Activities;

import android.app.Activity;
import android.app.Application;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import larc.ludicon.R;
import larc.ludicon.UserInfo.User;
import larc.ludicon.Utils.CloudConnection.CloudConnection;
import larc.ludicon.Utils.CloudConnection.ParseConnection;
import larc.ludicon.Utils.ConnectionChecker.IConnectionChecker;
import larc.ludicon.Utils.ConnectionChecker.ConnectionChecker;
import larc.ludicon.Utils.MessageDialog;


import com.facebook.FacebookSdk;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONObject;

import java.util.Arrays;

public class IntroActivity extends Activity {

    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private ProfilePictureView profilePictureView;
    private IConnectionChecker connectionChecker;
    private TextView greeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if( isNetworkConnected() == false ) {
            openNoInternetConnectionDialog();
        }
        // Facebook init
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {

                                        Profile profile = Profile.getCurrentProfile();
                                        // If user has no shared preferences
                                        if (User.getId(getApplicationContext()) == "" && profile != null) {
                                            User.setInfo(profile.getFirstName(), profile.getLastName(), profile.getId(), object.optString("email"), getApplicationContext());
                                            User.setPassword("facebook", getApplicationContext());
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                        updateUI();
                    }

                    @Override
                    public void onCancel() {
                        updateUI();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        openNoInternetConnectionDialog();
                        updateUI();
                    }
                });

        setContentView(R.layout.activity_intro);

        LoginButton login_button = (LoginButton)findViewById(R.id.login_button);
        login_button.setReadPermissions(Arrays.asList("public_profile, email, user_friends"));

        profilePictureView = (ProfilePictureView) findViewById(R.id.profilePictureIntro);



        profilePictureView.setVisibility(View.INVISIBLE);
        greeting = (TextView) findViewById(R.id.greeting);
        greeting.setVisibility(View.INVISIBLE);

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // If user has no shared preferences
                                if(User.getId(getApplicationContext()) == "") {

                                    Profile profile = Profile.getCurrentProfile();
                                    User.setInfo(profile.getFirstName(), profile.getLastName(), profile.getId(), object.optString("email"), getApplicationContext());
                                    // TODO - add Birthday
                                    User.setPassword("facebook",getApplicationContext());
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
                updateUI();
            }
        };

        updateUI();
    }

    /**
     * Method that jumps to the MainActivity
     */
    public void jumpToMainActivity(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 5 seconds
                Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(goToNextActivity);
                finish();
            }
        }, 5000); // Delay time for transition to next activity -> insert any time wanted here instead of 5000
    }


    public boolean isNetworkConnected() {
        if (connectionChecker == null) {
            connectionChecker = new ConnectionChecker();
        }
        return connectionChecker.isNetworkAvailable(getApplicationContext());
    }

    // Update UI
    private void updateUI() {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();
        if (accessToken != null && profile != null) // If user has successfully logged in
        {

            profilePictureView.setDrawingCacheEnabled(true);
            profilePictureView.setProfileId(profile.getId());
            User.profilePictureView = profilePictureView;


//            ImageView fbImage = ( ( ImageView)profilePictureView.getChildAt(0));
//            Bitmap    bitmap  = ( (BitmapDrawable) fbImage.getDrawable()).getBitmap();
//            User.setImage(bitmap);

            profilePictureView.setVisibility(View.VISIBLE);
            LoginButton login_button = (LoginButton) findViewById(R.id.login_button);
            login_button.setVisibility(View.INVISIBLE);

            greeting.setVisibility(View.VISIBLE);
            greeting.setText(getString(R.string.hello_user, profile.getFirstName()));

            jumpToMainActivity();
            

        }
        else { // Login FAILED
                    greeting.setText("");
        }
    }
    public void openNoInternetConnectionDialog() {
        DialogFragment newFragment = new MessageDialog();
        newFragment.show(getFragmentManager(), "message");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_intro, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }
}
