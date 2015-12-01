package larc.ludicon.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import larc.ludicon.R;
import larc.ludicon.SharedPreferences.UserIdSave;
import larc.ludicon.SqlConnection.User;
import larc.ludicon.Utils.ConnectionChecker.IConnectionChecker;
import larc.ludicon.Utils.ConnectionChecker.ConnectionChecker;
import larc.ludicon.Utils.UserCredentials;


import com.facebook.FacebookSdk;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.util.Arrays;

public class IntroActivity extends Activity {

    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private ProfilePictureView profilePictureView;
    private IConnectionChecker connectionChecker;
    private UserCredentials credentials;
    private TextView greeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                                        // If user has no shared preferences
                                        if(UserIdSave.getIdFromSharedPref(getApplicationContext()) == "" ) {
                                            Profile profile = Profile.getCurrentProfile();
                                            UserIdSave.setInfotoSharedPref(profile.getFirstName(), profile.getLastName(), profile.getId(), object.optString("email"), getApplicationContext());
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
                                if(UserIdSave.getIdFromSharedPref(getApplicationContext()) == "") {

                                    Profile profile = Profile.getCurrentProfile();
                                    UserIdSave.setInfotoSharedPref(profile.getFirstName(), profile.getLastName(), profile.getId(),object.optString("email"), getApplicationContext());
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

        if (accessToken != null && profile != null) // if user has successfully logged in
        {
            profilePictureView.setDrawingCacheEnabled(true);
            profilePictureView.setProfileId(profile.getId());
            profilePictureView.setVisibility(View.VISIBLE);

            LoginButton login_button = (LoginButton) findViewById(R.id.login_button);
            login_button.setVisibility(View.INVISIBLE);

            greeting.setVisibility(View.VISIBLE);
            greeting.setText(getString(R.string.hello_user, profile.getFirstName()));

            credentials = new UserCredentials(profile.getName(), "facebook");
            UserCredentials.setUserCredentialsInstance(credentials);

            if(credentials != null) { // Not the first log in

                AccessToken.getCurrentAccessToken().getPermissions();

                // Check internet connection
                if(isNetworkConnected()){
                    // TODO : Check if registered in database. If not -> add it.
                    jumpToMainActivity();

                }
                else{
                    // TODO something if Network not connected
                }
            }

        } else {
            credentials = null;
            UserCredentials.setUserCredentialsInstance(credentials);
            greeting.setText(null);
        }
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
