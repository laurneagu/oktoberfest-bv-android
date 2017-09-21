package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;

/**
 * Created by ancuta on 7/10/2017.
 */

public class IntroActivity extends Activity {
    int i = 0;

    Button loginButton;
    Button registerButton;
    TextView infoTextView;
    TextView termsAndPrivacyPolicy;
    static public ImageView profileImage;
    private ProfileTracker profileTracker;
    private CallbackManager callbackManager;
    private ProfilePictureView profilePictureView;
    ImageView logo;
    Boolean go = false;
    Bitmap image;
    Profile profile;
    JSONObject jsonObject;
    LoginResult loginRslt;
    LoginButton facebookButton;

    public void animateProfileImage(Boolean isMain) {
        String image;
        if (isMain) {
            image = Persistance.getInstance().getUserInfo(IntroActivity.this).profileImage;
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("ProfileImage", 0);
            image = sharedPreferences.getString("ProfileImage", "0");
        }

        if (image != null && !image.equals("0")) {
            Bitmap bitmap = decodeBase64(image);
            IntroActivity.profileImage.setImageBitmap(bitmap);
            IntroActivity.profileImage.setAlpha(0.3f);
            IntroActivity.profileImage.animate().alpha(1f).setDuration(1000);
        }


    }


    public void goToActivity() {
        if (!go) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("email", Persistance.getInstance().getUserInfo(IntroActivity.this).email);
            params.put("password", Persistance.getInstance().getUserInfo(IntroActivity.this).password);
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("apiKey", "b0a83e90-4ee7-49b7-9200-fdc5af8c2d33");
            HTTPResponseController.getInstance().returnResponse(params, headers, IntroActivity.this, "http://207.154.236.13/api/login/");





        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void setImageForProfile(Activity activity, String image) {

        SharedPreferences.Editor editor = activity.getSharedPreferences("ProfileImage", 0).edit();
        editor.putString("ProfileImage", image);
        editor.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        FacebookSdk.sdkInitialize(getApplicationContext());


        setContentView(R.layout.intro_activity);
        logo = (ImageView) findViewById(R.id.logo);
        logo.setImageResource(R.drawable.logo);
        // set font
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.ttf");


        facebookButton = (LoginButton) findViewById(R.id.facebookButton);
        /*facebookButton.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        facebookButton.setLoginBehavior(LoginBehavior.NATIVE_ONLY);*/
        facebookButton.setLoginBehavior(LoginBehavior.WEB_ONLY);
        facebookButton.setTypeface(typeFace);
        facebookButton.setReadPermissions(Arrays.asList("public_profile, email, user_friends"));
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setTypeface(typeFace);
        termsAndPrivacyPolicy = (TextView) findViewById(R.id.termsAndPrivacyPolicy);


        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setTypeface(typeFace);
        infoTextView = (TextView) findViewById(R.id.textView);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        logo.animate().translationY(-300f).setDuration(1000);
        callbackManager = CallbackManager.Factory.create();
        if (Persistance.getInstance().getUserInfo(this).id == null) {
            facebookButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
            infoTextView.setVisibility(View.VISIBLE);
            termsAndPrivacyPolicy.setVisibility(View.VISIBLE);
            facebookButton.animate().alpha(1f).setDuration(1000);
            loginButton.animate().alpha(1f).setDuration(1000);
            registerButton.animate().alpha(1f).setDuration(1000);
            infoTextView.animate().alpha(1f).setDuration(1000);
            go = true;
        }


        facebookLogin();
        System.out.println(Persistance.getInstance().getUserInfo(IntroActivity.this).facebookId + " fbid");
        if (Persistance.getInstance().getUserInfo(IntroActivity.this).facebookId != null && Persistance.getInstance().getUserInfo(IntroActivity.this).facebookId.equals("")) {
            goToActivity();
        } else
            if (!go) {
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                final ArrayList<String> friends = new ArrayList<String>();
                                JSONArray friendsList;
                                try {
                                    friendsList = response.getJSONObject().getJSONArray("data");
                                    for (int l = 0; l < friendsList.length(); l++) {
                                        friends.add(friendsList.getJSONObject(l).getString("id"));
                                    }

                                    String firstName = Persistance.getInstance().getUserInfo(IntroActivity.this).firstName;
                                    String lastName = Persistance.getInstance().getUserInfo(IntroActivity.this).lastName;
                                    String email = Persistance.getInstance().getUserInfo(IntroActivity.this).email;
                                    String password = Persistance.getInstance().getUserInfo(IntroActivity.this).password;
                                    HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("firstName", firstName);
                                    params.put("lastName", lastName);
                                    params.put("email", email);
                                    params.put("password", password);
                                    params.put("isCustom", "1");
                                    for (int i = 0; i < friends.size(); i++) {
                                        params.put("fbFriends[" + i + "]", friends.get(i));
                                    }
                                    HashMap<String, String> headers = new HashMap<String, String>();
                                    headers.put("apiKey", "b0a83e90-4ee7-49b7-9200-fdc5af8c2d33");
                                    HTTPResponseController.getInstance().returnResponse(params, headers, IntroActivity.this, "http://207.154.236.13/api/register/");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                ).executeAsync();
            }


        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
        termsAndPrivacyPolicy.setTypeface(typeFace);
        infoTextView.setTypeface(typeFace);
        termsAndPrivacyPolicy.setText(Html.fromHtml("By continuing you agree to our<br/><font color='#d4498b'>Terms</font> &amp; <font color='#d4498b'>Privacy Policy</font>"), TextView.BufferType.SPANNABLE);
    }

    public ArrayList<String> updateFriends() {
        final ArrayList<String> friends = new ArrayList<String>();

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                            /* handle the result */
                        try {
                            JSONArray friendsList = response.getJSONObject().getJSONArray("data");

                            for (int l = 0; l < friendsList.length(); l++) {
                                friends.add(friendsList.getJSONObject(l).getString("id"));


                            }
                            setupProfile(friends, jsonObject, loginRslt, facebookButton);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

        return friends;
    }

    public void facebookLogin() {
        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(final LoginResult loginResult) {
                final GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {

                                jsonObject = object;
                                loginRslt = loginResult;


                                if (Profile.getCurrentProfile() == null) {
                                    mProfileTracker = new ProfileTracker() {
                                        @Override
                                        protected void onCurrentProfileChanged(Profile profile1, Profile profile2) {
                                            profile = profile2;
                                            mProfileTracker.stopTracking();
                                            updateFriends();
                                        }
                                    };
                                } else {
                                    profile = Profile.getCurrentProfile();
                                    updateFriends();
                                }


                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                facebookButton.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                registerButton.setVisibility(View.VISIBLE);
                infoTextView.setVisibility(View.VISIBLE);
                termsAndPrivacyPolicy.setVisibility(View.VISIBLE);

            }

            @Override
            public void onError(FacebookException error) {
                facebookButton.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                registerButton.setVisibility(View.VISIBLE);
                infoTextView.setVisibility(View.VISIBLE);
                termsAndPrivacyPolicy.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setupProfile(ArrayList<String> friends, JSONObject object, LoginResult loginResult, LoginButton facebookButton) {
        String firstName = profile.getFirstName();
        String lastName = profile.getLastName();
        String email = object.optString("email");
        String password = loginResult.getAccessToken().getUserId();
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("email", email);
        params.put("password", password);
        params.put("isCustom", "1");
        for (int i = 0; i < friends.size(); i++) {
            params.put("fbFriends[" + i + "]", friends.get(i));
        }
        final HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("apiKey", "b0a83e90-4ee7-49b7-9200-fdc5af8c2d33");
        Picasso.with(IntroActivity.this)
                .load("https://graph.facebook.com/" + password + "/picture?type=large")
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        image = bitmap;
                        String imageString = ProfileDetailsActivity.encodeToBase64(image, Bitmap.CompressFormat.JPEG, 100);
                        setImageForProfile(IntroActivity.this, imageString);
                        logo.animate().translationY(300f);
                        logo.animate().translationY(-300f).setDuration(1000);
                        HTTPResponseController.getInstance().returnResponse(params, headers, IntroActivity.this, "http://207.154.236.13/api/register/");

                    }


                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        System.out.println("bitmapfailed");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                        logo.animate().translationY(300f);
                        logo.animate().translationY(-300f).setDuration(1000);
                        HTTPResponseController.getInstance().returnResponse(params, headers, IntroActivity.this, "http://207.154.236.13/api/register/");
                        System.out.println("bitmaponprepare");
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        facebookButton.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
        registerButton.setVisibility(View.INVISIBLE);
        infoTextView.setVisibility(View.INVISIBLE);
        termsAndPrivacyPolicy.setVisibility(View.INVISIBLE);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

}
