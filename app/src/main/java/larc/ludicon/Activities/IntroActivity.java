package larc.ludicon.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Handler;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

//import com.batch.android.Batch;
//import com.batch.android.Config;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.HttpMethod;

import larc.ludicon.R;
import larc.ludicon.UserInfo.User;
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

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class IntroActivity extends Activity {

    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private ProfilePictureView profilePictureView;
    private IConnectionChecker connectionChecker;
    private TextView greeting;
    private ImageView logo;
    private ImageView background;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Batch Init - NOW IS IN UseParse*/
       // Batch.Push.setGCMSenderId("458732166636");
       // Batch.setConfig(new Config("DEV56C87CCE0350BE0F6C4A19C18E5"));


        // Clear notification Stack
        NotificationManager notifManager= (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();

        /* Firebase Context */
        Firebase.setAndroidContext(this);
        /* Firebase Reference */
        User.firebaseRef = new Firebase("https://ludicon.firebaseio.com/");


        //firebaseRef.child("message").setValue("Do you have data? You'll love Firebase.");


        if (isNetworkConnected() == false) {
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

        // TODO relative to the phone screen, not hardoded
        background = (ImageView) findViewById(R.id.bg);
        background.setImageResource(R.drawable.intro_bg);
        logo = (ImageView) findViewById(R.id.logo);
        logo.setImageResource(R.drawable.logo);

        LoginButton login_button = (LoginButton) findViewById(R.id.login_button);
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
                                if (User.getId(getApplicationContext()) == "") {

                                    Profile profile = Profile.getCurrentProfile();
                                    User.setInfo(profile.getFirstName(), profile.getLastName(), profile.getId(), object.optString("email"), getApplicationContext());
                                    // TODO - add Birthday
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
        };

        updateUI();
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
        }, 1000); // Delay time for transition to next activity -> insert any time wanted here instead of 5000
    }

    public void jumpToPrefActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 5 seconds
                Intent goToNextActivity = new Intent(getApplicationContext(), AskPreferences.class); //AskPreferences.class);
                startActivity(goToNextActivity);
                finish();
            }
        }, 1000); // Delay time for transition to next activity -> insert any time wanted here instead of 5000
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
        final Profile profile = Profile.getCurrentProfile();
        if (accessToken != null && profile != null) // If user has successfully logged in
        {
            profilePictureView.setDrawingCacheEnabled(true);
            profilePictureView.setProfileId(profile.getId());

            User.profilePictureView = profilePictureView;


//            ImageView fbImage = ( ( ImageView)profilePictureView.getChildAt(0));
//            Bitmap    bitmap  = ( (BitmapDrawable) fbImage.getDrawable()).getBitmap();
//            User.setImage(bitmap);

            // Firebase Login - insert data to dataBase (for a new user)
            User.firebaseRef.authWithOAuthToken("facebook", accessToken.getToken(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {



                    final Map<String, Object> map = new HashMap<String, Object>();

                    //  provider
                    map.put("provider", authData.getProvider());

                    //email
                    if (authData.getProviderData().containsKey("email")) {
                        map.put("email", authData.getProviderData().get("email").toString());
                    }

                    //profileImageURL
                    if (authData.getProviderData().containsKey("profileImageURL")) {
                        map.put("profileImageURL", authData.getProviderData().get("profileImageURL").toString());
                    }

                    String id = "NO_ID";
                    //id
                    if (authData.getProviderData().containsKey("id")) {
                        map.put("id", authData.getProviderData().get("id").toString());
                        id = authData.getProviderData().get("id").toString();
                    }

                    HashMap<String, Object> cachedInfo = (HashMap<String, Object>) authData.getProviderData().get("cachedUserProfile");

                    //picture
                    if (cachedInfo.containsKey("picture")) {
                        map.put("picture", cachedInfo.get("picture").toString());
                    }

                    //name
                    if (cachedInfo.containsKey("name")) {
                        map.put("name", cachedInfo.get("name").toString());
                        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
                        prefs.edit().putString("username", cachedInfo.get("name").toString()).commit();
                    }

                    //first_name
                    if (cachedInfo.containsKey("first_name")) {
                        map.put("firstName", cachedInfo.get("first_name").toString());
                    }

                    //last_name
                    if (cachedInfo.containsKey("last_name")) {
                        map.put("lastName", cachedInfo.get("last_name").toString());
                    }

                    //gender
                    if (cachedInfo.containsKey("gender")) {
                        map.put("gender", cachedInfo.get("gender").toString());
                    }

                    //age_range
                    if (cachedInfo.containsKey("age_range")) {
                        map.put("ageRange", cachedInfo.get("age_range").toString());
                    }

                    //timezone
                    if (cachedInfo.containsKey("timezone")) {
                        map.put("timezone", cachedInfo.get("timezone").toString());
                    }

                    //points
                    map.put("points","0");

                    //lastLogIn - GMT
                    DateFormat df = DateFormat.getDateTimeInstance();
                    df.setTimeZone(TimeZone.getTimeZone("gmt"));
                    String gmtTime = df.format(new Date());
                    map.put("lastLogInTime", gmtTime);


                    final String uid = authData.getUid();
                    User.uid = uid;

                    //profilePictureView.setVisibility(View.VISIBLE);
                    ImageView profilePicture = (ImageView) findViewById(R.id.profileImageView);
                    profilePicture.setImageBitmap(profilePictureView.getDrawingCache());

                    LoginButton login_button = (LoginButton) findViewById(R.id.login_button);
                    login_button.setVisibility(View.INVISIBLE);

                    greeting.setVisibility(View.VISIBLE);
                    greeting.setText(getString(R.string.hello_user, profile.getFirstName()));

                    // Check user exists
                    Log.v("UID",uid);
                    Firebase userRef = User.firebaseRef.child("users").child(uid); // check user
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            //  for (DataSnapshot sport : snapshot.getChildren()) {
                            // }

                            if (snapshot.getValue() == null) { // new user
                                User.firebaseRef.child("mesg").child(User.uid).child("status").setValue("User Nou");
                                User.firebaseRef.child("users").child(uid).setValue(map);

                                jumpToPrefActivity();

                            } else { // old user
                                User.firebaseRef.child("mesg").child(User.uid).child("status").setValue("User vechi");
                                User.firebaseRef.child("users").child(uid).updateChildren(map);
                                jumpToMainActivity();
                            }





                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            //User.firebaseRef.child("msge").setValue("The read failed: " + firebaseError.getMessage());
                        }
                    });

                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    // TODO Log out from facebook account
                }
            });





        } else { // Login FAILED
            User.firebaseRef.unauth();
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
        if (resultCode == RESULT_OK) {
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
