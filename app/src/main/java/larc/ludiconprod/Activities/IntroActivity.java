package larc.ludiconprod.Activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.batch.android.Batch;
//import com.batch.android.Config;

import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.ConnectionChecker.IConnectionChecker;
import larc.ludiconprod.Utils.ConnectionChecker.ConnectionChecker;
import larc.ludiconprod.Utils.MessageDialog;
import larc.ludiconprod.Utils.util.DateManager;


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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class IntroActivity extends Activity {

    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private ProfilePictureView profilePictureView;
    private IConnectionChecker connectionChecker;
    private TextView greeting;
    private ImageView logo;
    private ImageView background;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Batch Init - NOW IS IN UseParse*/
       // Batch.Push.setGCMSenderId("458732166636");
       // Batch.setConfig(new Config("DEV56C87CCE0350BE0F6C4A19C18E5"));


        // Clear notification Stack
        NotificationManager notifManager= (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();

        /* DatabaseReference Context */
        //DatabaseReference.setAndroidContext(this);
        /* DatabaseReference Reference */
        User.firebaseRef = FirebaseDatabase.getInstance().getReference();


        //firebaseRef.child("message").setValue("Do you have data? You'll love DatabaseReference.");


        if (isNetworkConnected() == false) {
            openNoInternetConnectionDialog();
        }

        // Clear current Location
        SharedPreferences.Editor editor = getSharedPreferences("UserDetails", 0).edit();
        editor.putString("current_latitude", "-1");
        editor.putString("current_longitude", "-1");
        editor.commit();




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
        //logo.setImageResource(R.drawable.logo);

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
        }, 1); // Delay time for transition to next activity -> insert any time wanted here instead of 5000
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

        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final Profile profile = Profile.getCurrentProfile();
        if (accessToken != null && profile != null) // If user has successfully logged in
        {
            profilePictureView.setDrawingCacheEnabled(true);
            profilePictureView.setProfileId(profile.getId());

            User.profilePictureView = profilePictureView;

            LoginButton login_button = (LoginButton) findViewById(R.id.login_button);
            login_button.setVisibility(View.INVISIBLE);


//            ImageView fbImage = ( ( ImageView)profilePictureView.getChildAt(0));
//            Bitmap    bitmap  = ( (BitmapDrawable) fbImage.getDrawable()).getBitmap();
//            User.setImage(bitmap);

            // DatabaseReference Login - insert data to dataBase (for a new user)
            mAuth = FirebaseAuth.getInstance();
            AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());


            final AccessToken at = accessToken;

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.

                            if (!task.isSuccessful()) {
                                Toast.makeText(IntroActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            GraphRequest request = GraphRequest.newMeRequest(at, new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                                    String email = user.optString("email");
                                    String id = user.optString("id");
                                    String name = user.optString("name");
                                    String firstName = user.optString("first_name");
                                    String lastName = user.optString("last_name");
                                    String gender = user.optString("gender");
                                    String age_range = user.optString("age_range");
                                    String timezone = user.optString("timezone");
                                    String picture = user.optString("picture");


                                    FirebaseUser userFirebase = task.getResult().getUser();
                                    final Map<String, Object> map = new HashMap<String, Object>();


                                    try {
                                        JSONObject picurljson = user.getJSONObject("picture");
                                        JSONObject picurljsondata = picurljson.getJSONObject("data");
                                        String url = picurljsondata.optString("url");
                                        //profileImageURL
                                        if (!url.isEmpty()) {
                                            map.put("profileImageURL",url);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    //  provider
                                    map.put("provider", userFirebase.getProviderId());

                                    //email
                                    if (!email.isEmpty()) {
                                        map.put("email", email);
                                    }

                                    if (!id.isEmpty()) {
                                        map.put("id", id);
                                    }

                                    //picture
                                    if (!picture.isEmpty()) {
                                        picture = picture.replace("\"", "");
                                        picture = picture.replace("\\", "");
                                        map.put("picture", picture);
                                    }

                                    //name
                                    if (!name.isEmpty()) {
                                        map.put("name", name);
                                        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
                                        prefs.edit().putString("username", name.toString()).commit();
                                    }

                                    //first_name
                                    if (!firstName.isEmpty()) {
                                        map.put("firstName", firstName);
                                    }

                                    //last_name
                                    if (!lastName.isEmpty()) {
                                        map.put("lastName", lastName);
                                    }

                                    //gender
                                    if (!gender.isEmpty()) {
                                        map.put("gender", gender);
                                    }

                                    //age_range
                                    if (!age_range.isEmpty()) {
                                        age_range = age_range.replace("\"", "");
                                        age_range = age_range.replace("\\", "");
                                        map.put("ageRange", age_range.toString());
                                    }

                                    //timezone
                                    if (!timezone.isEmpty()) {
                                        map.put("timezone", timezone);
                                    }

                                    //points
                                    map.put("points", "0");


                                    
                                    //lastLogIn - GMT
                                    DateFormat df = DateFormat.getDateTimeInstance();
                                    df.setTimeZone(TimeZone.getTimeZone("gmt"));
                                    String gmtTime = df.format(new Date());
                                    map.put("lastLogInTime", DateManager.convertFromTextToSeconds(gmtTime));


                                    //final String uid = userFirebase.getUid();
                                    final String uid = "facebook:" + id;
                                    Log.v("ID", uid);
                                    User.uid = uid;

                                    //profilePictureView.setVisibility(View.VISIBLE);
                                    ImageView profilePicture = (ImageView) findViewById(R.id.profileImageView);
                                    profilePicture.setImageBitmap(profilePictureView.getDrawingCache());

                                    greeting.setVisibility(View.VISIBLE);
                                    greeting.setText(getString(R.string.hello_user, profile.getFirstName()));


                                    // Check user exists
                                    Log.v("UID",uid);
                                    DatabaseReference userRef = User.firebaseRef.child("users").child(uid); // check user
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {

                                            //  for (DataSnapshot sport : snapshot.getChildren()) {
                                            // }

                                            if (snapshot.getValue() == null) { // new user

                                                final Map<String, Object> mapSports = new HashMap<String, Object>();
                                                mapSports.put("other",9);
                                                map.put("sports",mapSports);
                                                map.put("range", 100);

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
                                        public void onCancelled(DatabaseError firebaseError) {
                                            //User.firebaseRef.child("msge").setValue("The read failed: " + firebaseError.getMessage());
                                        }
                                    });

                                }
                            });


                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,link,email,gender,first_name,last_name, age_range,timezone,picture.width(300).height(300)");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }


                    });




        } else { // Login FAILED
            FirebaseAuth.getInstance().signOut();
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