package larc.ludiconprod.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Adapters.MainActivity.AroundMeAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.Adapters.MainActivity.SimpleDividerItemDecoration;
import larc.ludiconprod.BottomBarHelper.BottomBarTab;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.Dialogs.ConfirmationDialog;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.HappeningNowLocation;
import larc.ludiconprod.Utils.Location.GPSTracker;
import larc.ludiconprod.Utils.MainPageUtils.ViewPagerAdapter;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;
import larc.ludiconprod.Utils.util.Sponsors;
import larc.ludiconprod.Utils.util.Sport;
import me.anwarshahriar.calligrapher.Calligrapher;

import static larc.ludiconprod.Activities.Main.bottomBar;

/**
 * Created by ancuta on 7/26/2017.
 */

public class ActivitiesActivity extends Fragment implements Serializable, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, Response.ErrorListener {

    ViewPager pager;
    private Context mContext;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"AROUND ME", "MY ACTIVITIES"};
    int Numboftabs = 2;
    private View v;
    boolean addedSwipeAroundMe = false;
    boolean addedSwipeMyActivity = false;
    static public boolean getFirstPageAroundMe = true;
    static public boolean getFirstPageMyActivity = true;
    static public AroundMeAdapter fradapter;
    static public MyAdapter myAdapter;
    static public ActivitiesActivity currentFragment;
    public static ArrayList<Event> aroundMeEventList = new ArrayList<Event>();
    static public ArrayList<Sponsors> sponsorsList = new ArrayList<>();
    public static final ArrayList<Event> myEventList = new ArrayList<Event>();
    ImageView heartImageAroundMe;
    TextView noActivitiesTextFieldAroundMe;
    TextView pressPlusButtonTextFieldAroundMe;
    ImageView heartImageMyActivity;
    TextView noActivitiesTextFieldMyActivity;
    TextView pressPlusButtonTextFieldMyActivity;
    static LinearLayoutManager layoutManagerAroundMe;
    static LinearLayoutManager layoutManagerMyActivities;
    ProgressBar progressBarMyEvents;
    ProgressBar progressBarAroundMe;
    public static int NumberOfRefreshMyEvents = 0;
    public static int NumberOfRefreshAroundMe = 0;
    public static RecyclerView frlistView;
    public static RecyclerView mylistView;
    Boolean isFirstTimeAroundMe = false;
    Boolean isFirstTimeMyEvents = false;
    Boolean isGetingPage = false;
    Boolean dataComeArundeMe = false;
    Boolean dataComeMy = false;
    public static ProgressBar v1;
    static public double longitude = 0;
    static public double latitude = 0;
    public static Thread startHN;
    public static Thread stopHN;
    static Boolean isOnActivityPage = false;
    static int buttonState = 0;//0:is never pressed 1:Check-in Performed 2:Check-Out Performed

    //LocationManager locationManager;
    LocationListener locationListener;
    Object startGettingLocation = new Object();
    private LocationRequest locationRequest;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    RelativeLayout happeningNowLayout;
    public static CountDownTimer buttonSetter;
    static Button checkinButton;
    private boolean noGps = false;
    public static Thread stopHappeningNow;
    public static Thread startHappeningNow;
    static Boolean HPShouldBeVisible = false;
    static public HappeningNowLocation happeningNowLocation = new HappeningNowLocation();
    static public FragmentActivity activity;
    static public int startedEventDate = Integer.MAX_VALUE;
    int pastEvent = Integer.MAX_VALUE;
    Event eventShouldHappen;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    int nrElements = 4;
    ViewGroup.LayoutParams params;

    public static boolean fromSwipe = false;
    public static boolean checkinDone = false;


    public ActivitiesActivity() {
        currentFragment = this;
        activity = getActivity();
    }

    public Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public boolean checkedLocation(Event currentEvent) {
        Boolean isInLocation = false;
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        System.out.println(location.getLatitude() + " locatie");
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Location eventLocation = new Location(LocationManager.GPS_PROVIDER);
        eventLocation.setLatitude(currentEvent.latitude);
        eventLocation.setLongitude(currentEvent.longitude);
        System.out.println(location.distanceTo(eventLocation) + " distanta");
        float distanceToEvent = location.distanceTo(eventLocation);

        if (distanceToEvent < 1000) {
            isInLocation = true;
        }
        return isInLocation;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {

            if (msg.what == 0) {


                HPShouldBeVisible = true;
                if (isFirstTimeMyEvents) {
                    //myEventList.remove(0);
                    myAdapter.notifyDataSetChanged();
                }
                System.out.println("eventStarted");

                try {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 218, getResources().getDisplayMetrics());
                            happeningNowLayout.requestLayout();
                        }
                    });


                //set happening now field
                TextView weWillplay = (TextView) v.findViewById(R.id.HNPlay);
                TextView sportField = (TextView) v.findViewById(R.id.HNPlayWhat);
                TextView ludicoins = (TextView) v.findViewById(R.id.ludicoinsHN);
                TextView points = (TextView) v.findViewById(R.id.pointsHN);
                TextView location = (TextView) v.findViewById(R.id.locationHN);
                ImageView imageViewBackground = (ImageView) v.findViewById(R.id.imageViewBackground);
                CircleImageView friends0 = (CircleImageView) v.findViewById(R.id.friends0HN);
                CircleImageView friends1 = (CircleImageView) v.findViewById(R.id.friends1HN);
                CircleImageView friends2 = (CircleImageView) v.findViewById(R.id.friends2HN);
                TextView allFriends = (TextView) v.findViewById(R.id.friendsNumberHN);
                checkinButton = (Button) v.findViewById(R.id.checkinHN);

                final Event currentEvent = Persistance.getInstance().getHappeningNow(activity);

                if(currentEvent == null) {
                    ViewGroup.LayoutParams params = happeningNowLayout.getLayoutParams();
                    params.height = 0;
                    happeningNowLayout.setLayoutParams(params);

                    return;
                }

                checkinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (buttonState == 0) {
                            if (checkedLocation(currentEvent)) {
                                HashMap<String, String> params = new HashMap<String, String>();
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);
                                params.put("eventId", currentEvent.id);
                                HTTPResponseController.getInstance().checkin(params, headers, activity);

                                checkinDone = true;

                                buttonState = 1;
                                buttonSetter = new CountDownTimer(7200000, 1000) {
                                    int minutes = 0;
                                    int seconds = 0;

                                    @Override
                                    public void onTick(long l) {
                                        System.out.println("ontick");
                                        String minutesValue = "";
                                        String secondsValue = "";
                                        seconds++;
                                        if (seconds == 60) {
                                            seconds = 0;
                                            minutes++;
                                        }
                                        if (minutes > 9) {
                                            minutesValue = String.valueOf(minutes);
                                        } else {
                                            minutesValue = "0" + String.valueOf(minutes);
                                        }
                                        if (seconds > 9) {
                                            secondsValue = String.valueOf(seconds);
                                        } else {
                                            secondsValue = "0" + String.valueOf(seconds);
                                        }
                                        //System.out.println(isOnActivityPage + " booleana " + checkinButton);
                                        if (isOnActivityPage) {
                                           // checkinButton.setText("CHECK-OUT " + minutesValue + ":" + secondsValue);
                                            checkinButton.setText("CHECK-OUT");
                                        }


                                    }

                                    @Override
                                    public void onFinish() {

                                    }
                                }.start();

                                requestLocationUpdates();
                                happeningNowLocation.startDate = String.valueOf(System.currentTimeMillis() / 1000);
                            } else {
                                Toast.makeText(activity, "Go to event location to start sweating on points!", Toast.LENGTH_LONG).show();
                            }


                        } else
                            if (buttonState == 1) {
                                final Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(),
                                        "fonts/Quicksand-Medium.ttf");
                                final Typeface typeFaceBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.ttf");
                                final ConfirmationDialog confirmationDialog = new ConfirmationDialog(getActivity());
                                confirmationDialog.show();
                                confirmationDialog.title.setText("Confirm?");
                                confirmationDialog.title.setTypeface(typeFaceBold);
                                confirmationDialog.message.setText("Are you sure you want to stop sweating on points?");
                                confirmationDialog.message.setTypeface(typeFace);
                                confirmationDialog.confirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //Call sendLocation
                                        checkinDone = false;
                                        happeningNowLocation.endDate = String.valueOf(System.currentTimeMillis() / 1000);
                                        savePoints();

                                        buttonState = 2;
                                        ViewGroup.LayoutParams params = happeningNowLayout.getLayoutParams();
                                        params.height = 0;
                                        happeningNowLayout.setLayoutParams(params);

                                        if (googleApiClient.isConnected()) {
                                            googleApiClient.disconnect();
                                        }
                                        confirmationDialog.dismiss();
                                        Persistance.getInstance().setHappeningNow(null, activity);
                                    }
                                });
                                confirmationDialog.dismiss.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        confirmationDialog.dismiss();
                                    }
                                });


                            }
                    }
                });


                Sport sport = new Sport(currentEvent.sportCode);
                String weWillPlayString = "";
                String sportName = "";

                if (currentEvent.sportCode.equalsIgnoreCase("JOG") || currentEvent.sportCode.equalsIgnoreCase("GYM") || currentEvent.sportCode.equalsIgnoreCase("CYC")) {
                    weWillPlayString = "Will go to ";
                    sportName = sport.sportName;
                } else {
                    if (currentEvent.sportCode.equalsIgnoreCase("OTH")) {
                        weWillPlayString = "Will play ";
                        sportName = currentEvent.otherSportName;
                    } else {
                        weWillPlayString = "Will play ";
                        sportName = sport.sportName;
                    }
                }

                sportName = sportName.substring(0, 1).toUpperCase() + sportName.substring(1);

                weWillplay.setText(weWillPlayString);
                sportField.setText(sportName);
                ludicoins.setText(String.valueOf(currentEvent.ludicoins));
                points.setText(String.valueOf(currentEvent.points));
                location.setText(currentEvent.placeName);
                switch (currentEvent.sportCode) {
                case "FOT":
                    imageViewBackground.setBackgroundResource(R.drawable.bg_sport_football);
                    break;
                case "BAS":
                    imageViewBackground.setBackgroundResource(R.drawable.bg_sport_basketball);
                    break;
                case "VOL":
                    imageViewBackground.setBackgroundResource(R.drawable.bg_sport_volleyball);
                    break;
                case "JOG":
                    imageViewBackground.setBackgroundResource(R.drawable.bg_sport_jogging);
                    break;
                case "GYM":
                    imageViewBackground.setBackgroundResource(R.drawable.bg_sport_gym);
                    break;
                case "CYC":
                    imageViewBackground.setBackgroundResource(R.drawable.bg_sport_cycling);
                    break;
                case "TEN":
                    imageViewBackground.setBackgroundResource(R.drawable.bg_sport_tennis);
                    break;
                case "PIN":
                    imageViewBackground.setBackgroundResource(R.drawable.bg_sport_pingpong);
                    break;
                case "SQU":
                    imageViewBackground.setBackgroundResource(R.drawable.bg_sport_squash);
                    break;
                case "OTH":
                    imageViewBackground.setBackgroundResource(R.drawable.bg_sport_others);
                    break;
                }

                if (currentEvent.numberOfParticipants - 1 >= 1) {
                    friends0.setVisibility(View.VISIBLE);
                }
                if (currentEvent.numberOfParticipants - 1 >= 2) {
                    friends1.setVisibility(View.VISIBLE);
                }
                if (currentEvent.numberOfParticipants - 1 >= 3) {
                    friends2.setVisibility(View.VISIBLE);
                }
                if (currentEvent.numberOfParticipants - 1 >= 4) {
                    allFriends.setVisibility(View.VISIBLE);
                    allFriends.setText("+" + String.valueOf(currentEvent.numberOfParticipants - 4));

                }
                for (int i = 0; i < currentEvent.participansProfilePicture.size(); i++) {
                    if (!currentEvent.participansProfilePicture.get(i).equals("") && i == 0) {
                        Bitmap bitmap = decodeBase64(currentEvent.participansProfilePicture.get(i));
                        friends0.setImageBitmap(bitmap);
                    } else
                        if (!currentEvent.participansProfilePicture.get(i).equals("") && i == 1) {
                            Bitmap bitmap = decodeBase64(currentEvent.participansProfilePicture.get(i));
                            friends1.setImageBitmap(bitmap);
                        } else
                            if (!currentEvent.participansProfilePicture.get(i).equals("") && i == 2) {
                                Bitmap bitmap = decodeBase64(currentEvent.participansProfilePicture.get(i));
                                friends2.setImageBitmap(bitmap);
                            }
                }
                } catch (Exception e) {
                    e.printStackTrace();
                    //I don't know why the fuck i am here
                }

            } else
                if (msg.what == 1) {
                    System.out.println("eventStopped");
                    buttonState = 0;

                    happeningNowLayout = (RelativeLayout) v.findViewById(R.id.generalHappeningNowLayout);
                    ViewGroup.LayoutParams params = happeningNowLayout.getLayoutParams();
                    params.height = 0;
                    happeningNowLayout.setLayoutParams(params);
                    if (buttonSetter != null) {
                        buttonSetter.cancel();
                    }

                    HPShouldBeVisible = false;

                    if (googleApiClient.isConnected()) {
                        googleApiClient.disconnect();
                    }
                    Persistance.getInstance().setHappeningNow(null, activity);
                }
        }
    };

    public void savePoints() {
        System.out.println(" save points called");
        Event currentEvent;

        currentEvent = Persistance.getInstance().getHappeningNow(getActivity());
        System.out.println(currentEvent.creatorName + " " + currentEvent.eventDate + " " + currentEvent.id);
        if (currentEvent == null) {
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);
        params.put("userId", Persistance.getInstance().getUserInfo(activity).id);
        params.put("eventId", currentEvent.id);
        System.out.println("happening id" + currentEvent.id);
        params.put("startedAt", happeningNowLocation.startDate);
        params.put("endedAt", happeningNowLocation.endDate);
        for (int i = 0; i < happeningNowLocation.locationList.size(); i++) {
            params.put("locations[" + i + "][latitude]", String.valueOf(happeningNowLocation.locationList.get(i).getLatitude()));
            params.put("locations[" + i + "][longitude]", String.valueOf(happeningNowLocation.locationList.get(i).getLongitude()));
        }
        System.out.println("se apeleaza aici ");
        System.out.println(params.toString());
        System.out.println(headers.toString());
        HTTPResponseController.getInstance().savePoints(params, headers, activity, this);
        Persistance.getInstance().setHappeningNow(null, activity);
    }

    public void getAroundMeEvents(String pageNumber, Double latitude, Double longitude) {
        v1 = (ProgressBar) v.findViewById(R.id.activityProgressBar);
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> urlParams = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

        GPSTracker gps = new GPSTracker(activity.getApplicationContext(), activity);
        if (!gps.canGetLocation()) {
            this.noGps = true;
            this.prepareError("No location services available!");
            return;
        }
        //set urlParams
        urlParams.put("userId", Persistance.getInstance().getUserInfo(activity).id);
        urlParams.put("pageNumber", pageNumber);
        urlParams.put("userLatitude", String.valueOf(latitude));
        urlParams.put("userLongitude", String.valueOf(longitude));
        urlParams.put("userRange", "" + Persistance.getInstance().getUserInfo(activity).range);
        String userSport = "";
        for (int i = 0; i < Persistance.getInstance().getUserInfo(activity).sports.size(); i++) {
            if (i < Persistance.getInstance().getUserInfo(activity).sports.size() - 1) {
                userSport = userSport + Persistance.getInstance().getUserInfo(activity).sports.get(i).code + ";";
            } else {
                userSport = userSport + Persistance.getInstance().getUserInfo(activity).sports.get(i).code;
            }
        }
        urlParams.put("userSports", userSport);
        //get Around Me Event
        HTTPResponseController.getInstance().getAroundMeEvent(params, headers, activity, urlParams, this);
    }

    public void getMyEvents(String pageNumber) {
        ActivitiesActivity.getFirstPageMyActivity = pageNumber.equals("0");

        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        HashMap<String, String> urlParams = new HashMap<>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

        //set urlParams
        urlParams.put("userId", Persistance.getInstance().getUserInfo(activity).id);
        urlParams.put("pageNumber", pageNumber);

        //get Around Me Event
        HTTPResponseController.getInstance().getMyEvent(params, headers, activity, urlParams, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (fradapter != null) fradapter.notifyDataSetChanged();
        if (myAdapter != null) myAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(getActivity());


        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(300 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        googleApiClient.connect();

        System.out.println("on create");

        mContext = inflater.getContext();
        isOnActivityPage = true;
        aroundMeEventList.clear();

        v = inflater.inflate(R.layout.activities_acitivity, container, false);
        nrElements = 4;
        while (activity == null) {
            activity = getActivity();
        }

        Calligrapher calligrapher = new Calligrapher(activity);
        calligrapher.setFont(activity, "fonts/Quicksand-Medium.ttf", true);

        if (Persistance.getInstance().getMyActivities(activity) != null && !Persistance.getInstance().getMyActivities(activity).equals("0") && Persistance.getInstance().getMyActivities(activity).size() > 1) {

            eventShouldHappen = Persistance.getInstance().getMyActivities(activity).get(0);
            pastEvent = eventShouldHappen.eventDateTimeStamp;
        }
        v1 = (ProgressBar) v.findViewById(R.id.activityProgressBar);

        int NumberOfUnseen = Persistance.getInstance().getUnseenChats(activity).size();

        BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_friends);
        nearby.setBadgeCount(NumberOfUnseen);
        //set activeToken in firebase node for notification
        final DatabaseReference userNode = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(activity).id);

        SharedPreferences sharedPreferences = activity.getSharedPreferences("regId", 0);
        String activeToken = sharedPreferences.getString("regId", "0");
        if (!activeToken.equalsIgnoreCase("0")) {
            userNode.child("activeToken").setValue(activeToken);
        }

        try {
            super.onCreate(savedInstanceState);

            // Creating ViewPager Adapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs
            adapter = new ViewPagerAdapter(activity.getSupportFragmentManager(), Titles, Numboftabs);

            // Assigning ViewPager View and setting the adapter
            pager = (ViewPager) v.findViewById(R.id.pager);
            pager.setAdapter(adapter);

            // Assiging the Sliding Tab Layout View
            tabs = (SlidingTabLayout) v.findViewById(R.id.tabs);
            tabs.setDistributeEvenly(false); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(pager);

            if (Persistance.getInstance().getLocation(activity).locationList.size() > 0 && !HPShouldBeVisible) {
                happeningNowLocation = Persistance.getInstance().getLocation(activity);
                happeningNowLocation.endDate = String.valueOf(System.currentTimeMillis() / 1000);
                savePoints();

            }
            if (HPShouldBeVisible) {
                happeningNowLayout = (RelativeLayout) v.findViewById(R.id.generalHappeningNowLayout);
                params = happeningNowLayout.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                happeningNowLayout.setLayoutParams(params);
            }
            happeningNowLayout = (RelativeLayout) v.findViewById(R.id.generalHappeningNowLayout);
            params = happeningNowLayout.getLayoutParams();
            params.height = 0;

            happeningNowLayout.requestLayout();


            myAdapter = new MyAdapter(myEventList, sponsorsList, activity.getApplicationContext(), activity, getResources(), currentFragment);
            fradapter = new AroundMeAdapter(aroundMeEventList, sponsorsList, activity.getApplicationContext(), activity, getResources(), currentFragment);


            getMyEvents("0");

            NumberOfRefreshMyEvents = 0;
            NumberOfRefreshAroundMe = 0;


            //
            // for facebook share


            final ShareButton shareButton;

            FacebookSdk.sdkInitialize(activity);
            callbackManager = CallbackManager.Factory.create();
            shareDialog = new ShareDialog(activity);
            shareButton = (ShareButton) v.findViewById(R.id.share_btn);
            String facebookId = Persistance.getInstance().getUserInfo(super.getActivity()).facebookId;
            if (facebookId.equals("")) {
                shareButton.getLayoutParams().height = 0;
            } else {
                ShareLinkContent contentLink = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://developers.facebook.com"))
                        .build();

                shareButton.setShareContent(contentLink);
                shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle("Activity on Ludicon")
                                    .setContentDescription(
                                            "I will attend an event in Ludicon ! Let's go and play ! ")
                                    .setContentUrl(Uri.parse("http://ludicon.ro/"))
                                    .build();

                            shareDialog.show(linkContent, ShareDialog.Mode.AUTOMATIC);

                        }
                    }
                });

                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(activity, "This post was shared! ", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {


                    }
                });


            }


            Runnable runnableStart = new Runnable() {
                private Event firstEvent() {
                    synchronized (myEventList) {
                        return myEventList.get(0);
                    }
                }

                @Override
                public void run() {
                    try {
                        long timeToNextEvent = Integer.MAX_VALUE;
                        Event currentEvent = Persistance.getInstance().getHappeningNow(activity);
                        if (currentEvent != null) {
                            //din shared pref, a inceput deja eventul
                            System.out.println("intra in primul if, cu shared pref");
                            timeToNextEvent = (currentEvent.eventDateTimeStamp - System.currentTimeMillis() / 1000);
                            if ((timeToNextEvent > -3600 && buttonState == 0) || (timeToNextEvent > -7200 && buttonState == 1)) {
                                googleApiClient.connect();
                                startedEventDate = currentEvent.eventDateTimeStamp;
                                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                //mai am nevoie de asta?
                                /*activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        happeningNowLayout.setLayoutParams(params);
                                    }
                                });*/
                                if (!checkinDone) {
                                    handler.sendEmptyMessage(0);
                                }
                                return;
                            }
                            else{
                                Persistance.getInstance().setHappeningNow(null, activity);
                            }
                        } else {
                            System.out.println("intra in al doilea if, nu am nimic in sharedPref " + myEventList.size()
                            );

                            if (myEventList.size() >= 1) {
                                pastEvent = myEventList.get(0).eventDateTimeStamp;
                                timeToNextEvent = (pastEvent - System.currentTimeMillis() / 1000);
                                System.out.println(timeToNextEvent);
                                while (timeToNextEvent >= 0 || timeToNextEvent < -3600) {
                                    System.out.println(timeToNextEvent);
                                    Thread.sleep(1000);

                                     /* pastEvent = Persistance.getInstance().getMyActivities(activity).get(0)
                                        .eventDateTimeStamp;
                                    eventShouldHappen = Persistance.getInstance().getMyActivities(activity).get(0);
                                    timeToNextEvent = (Persistance.getInstance().getMyActivities(activity).get(0).eventDateTimeStamp - System.currentTimeMillis() / 1000);*/

                                     if(myEventList.size() > 0) {
                                         System.out.println("ajung aici 2, nume creator: " + myEventList.get(0).creatorName);

                                         pastEvent = myEventList.get(0).eventDateTimeStamp;
                                         eventShouldHappen = myEventList.get(0);
                                         timeToNextEvent = myEventList.get(0).eventDateTimeStamp - System.currentTimeMillis()
                                                 / 1000;
                                     }
                                     else{
                                         break;
                                     }
                                }
                            }


                            synchronized (myEventList) {
                                //happening now started
                                if ((timeToNextEvent > -3600 && buttonState == 0) || (timeToNextEvent > -7200 && buttonState == 1)) {
                                    googleApiClient.connect();
                                    startedEventDate = pastEvent;
                                    if (myEventList.size()>0 && pastEvent == myEventList.get(0).eventDateTimeStamp) {
                                        System.out.println("intra vreodata aici?");
                                        //myEventList.remove(0);
                                    }

                                    Persistance.getInstance().setHappeningNow(eventShouldHappen, activity);

                                    handler.sendEmptyMessage(0);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            Runnable runnableStop = new Runnable() {
                @Override
                public void run() {
                    Log.d("runnableStop", "Started");
                    try {
                        if (myEventList.size() >= 1) {
                            long timeToNextEvent = (startedEventDate - System.currentTimeMillis() / 1000);
                            while ((buttonState == 0 && timeToNextEvent <= 3600) || (buttonState == 1 && timeToNextEvent < 7200) || !HPShouldBeVisible) {
                                if (buttonState == 2) {
                                    break;
                                }
                                Thread.sleep(1000);
                                timeToNextEvent = (startedEventDate - System.currentTimeMillis() / 1000);
                            }
                            if (timeToNextEvent > 7200 && buttonState != 2) {
                                savePoints();
                            }
                            //happening now stoped
                            handler.sendEmptyMessage(1);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            if (stopHappeningNow == null || !stopHappeningNow.isAlive()) {
                System.out.println("primul");
                stopHappeningNow = new Thread(runnableStop);
            }
            if (startHappeningNow == null || !stopHappeningNow.isAlive()) {
                System.out.println("al doilea");
                startHappeningNow = new Thread(runnableStart);
            }

            if (HPShouldBeVisible) {
                googleApiClient.connect();
                handler.sendEmptyMessage(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateListOfEventsAroundMe(final boolean eventHappeningNow) {
        try {

            System.out.println(isGetingPage + " aici");
            RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);
            ll.getLayoutParams().height = 0;
            isGetingPage = false;

            ll.setLayoutParams(ll.getLayoutParams());
            if (this.noGps) {
                this.prepareError("No location services available!");
            }

            // stop swiping on my events
            final SwipeRefreshLayout mSwipeRefreshLayout2 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh2);
            if (!isFirstTimeAroundMe) {
                layoutManagerAroundMe = new LinearLayoutManager(activity);
            }
            if(fradapter == null){
                fradapter = new AroundMeAdapter(aroundMeEventList, sponsorsList, activity.getApplicationContext(), activity, getResources(), currentFragment);
            }

            fradapter.notifyDataSetChanged();

            if (!isFirstTimeAroundMe) {
                frlistView = (RecyclerView) v.findViewById(R.id.events_listView2);
                layoutManagerAroundMe.setOrientation(LinearLayoutManager.VERTICAL);
                if(frlistView != null) {
                    frlistView.setLayoutManager(layoutManagerAroundMe);
                }
                heartImageAroundMe = (ImageView) v.findViewById(R.id.heartImageAroundMe);
                progressBarAroundMe = (ProgressBar) v.findViewById(R.id.progressBarAroundMe);
                progressBarAroundMe.setIndeterminate(true);
                progressBarAroundMe.setAlpha(0f);
            }


            noActivitiesTextFieldAroundMe = (TextView) v.findViewById(R.id.noActivitiesTextFieldAroundMe);
            pressPlusButtonTextFieldAroundMe = (TextView) v.findViewById(R.id.pressPlusButtonTextFieldAroundMe);
            final FloatingActionButton createNewActivityFloatingButtonAroundMe = (FloatingActionButton) v.findViewById(R.id.floatingButton2);
            createNewActivityFloatingButtonAroundMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, CreateNewActivity.class);
                    startActivity(intent);
                }
            });

            if (!isFirstTimeAroundMe) {
                frlistView.setAdapter(fradapter);
            }

            if (aroundMeEventList.size() == 0) {
                heartImageAroundMe.setVisibility(View.VISIBLE);
                noActivitiesTextFieldAroundMe.setVisibility(View.VISIBLE);
                pressPlusButtonTextFieldAroundMe.setVisibility(View.VISIBLE);
            } else {
                heartImageAroundMe.setVisibility(View.INVISIBLE);
                noActivitiesTextFieldAroundMe.setVisibility(View.INVISIBLE);
                pressPlusButtonTextFieldAroundMe.setVisibility(View.INVISIBLE);
            }


            if (frlistView != null) {

                frlistView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (layoutManagerAroundMe.findLastCompletelyVisibleItemPosition() == aroundMeEventList.size() - 1) {
                            progressBarAroundMe.setAlpha(1f);
                            System.out.println(layoutManagerAroundMe.findLastVisibleItemPosition() + " aici");
                            getAroundMeEvents(String.valueOf(NumberOfRefreshAroundMe), latitude, longitude);
                        }


                    }
                });
            }

            if (!addedSwipeAroundMe) {
                mSwipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getAroundMeEvents("0", latitude, longitude);
                        getFirstPageAroundMe = true;
                        mSwipeRefreshLayout2.setRefreshing(false);
                        NumberOfRefreshAroundMe = 0;
                        nrElements = 4;
                    }
                });
                addedSwipeAroundMe = true;
            }

            progressBarAroundMe.setAlpha(0f);

            isFirstTimeAroundMe = true;
            // SMOOTHING THE SCROLL STOP
            /*
            int last = layoutManagerAroundMe.findLastCompletelyVisibleItemPosition();
            int count = fradapter.getItemCount();
            if (last + 1 < count) {
                layoutManagerAroundMe.scrollToPositionWithOffset(last, 30);
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateListOfMyEvents(final boolean eventHappeningNow) {
        RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);
        ll.getLayoutParams().height = 0;
        ll.setLayoutParams(ll.getLayoutParams());
        if (this.noGps) {
            this.prepareError("No location services available!");
        }


        // stop swiping on my events
        final SwipeRefreshLayout mSwipeRefreshLayout1 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh1);
        ;
        myAdapter.notifyDataSetChanged();
        mylistView = (RecyclerView) v.findViewById(R.id.events_listView1);
        heartImageMyActivity = (ImageView) v.findViewById(R.id.heartImageMyActivity);
        noActivitiesTextFieldMyActivity = (TextView) v.findViewById(R.id.noActivitiesTextFieldMyActivity);
        pressPlusButtonTextFieldMyActivity = (TextView) v.findViewById(R.id.pressPlusButtonTextFieldMyActivity);
        progressBarMyEvents = (ProgressBar) v.findViewById(R.id.progressBarMyEvents);
        progressBarMyEvents.setIndeterminate(true);
        progressBarMyEvents.setAlpha(0f);


        if (!isFirstTimeMyEvents) {
            layoutManagerMyActivities = new LinearLayoutManager(getContext());
        }

        if (!isFirstTimeMyEvents) {

            layoutManagerMyActivities.setOrientation(LinearLayoutManager.VERTICAL);
            mylistView.setLayoutManager(layoutManagerMyActivities);
            mylistView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        }

        final FloatingActionButton createNewActivityFloatingButtonMyActivity = (FloatingActionButton) v.findViewById(R.id.floatingButton1);
        createNewActivityFloatingButtonMyActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, CreateNewActivity.class);
                startActivity(intent);
            }
        });

        if (!isFirstTimeMyEvents) {
            mylistView.setAdapter(myAdapter);
        }


        if (myEventList.size() == 0) {
            heartImageMyActivity.setVisibility(View.VISIBLE);
            noActivitiesTextFieldMyActivity.setVisibility(View.VISIBLE);
            pressPlusButtonTextFieldMyActivity.setVisibility(View.VISIBLE);
        } else {
            heartImageMyActivity.setVisibility(View.INVISIBLE);
            noActivitiesTextFieldMyActivity.setVisibility(View.INVISIBLE);
            pressPlusButtonTextFieldMyActivity.setVisibility(View.INVISIBLE);
        }


        if (mylistView != null) {

            mylistView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (layoutManagerMyActivities.findLastCompletelyVisibleItemPosition() == myEventList.size() - 1) {
                        progressBarMyEvents.setAlpha(1f);
                        getMyEvents(String.valueOf(NumberOfRefreshMyEvents));
                    }


                }
            });
        }

        if (!addedSwipeMyActivity) {
            mSwipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    System.out.println("intra aici");
                    getMyEvents("0");
                    getFirstPageMyActivity = true;
                    mSwipeRefreshLayout1.setRefreshing(false);
                    NumberOfRefreshMyEvents = 0;
                    fromSwipe = true;
                }
            });
            //addedSwipeMyActivity = true;
        }
        if (!stopHappeningNow.isAlive() && !startHappeningNow.isAlive()) {
            stopHappeningNow.start();
            startHappeningNow.start();
        }

        isFirstTimeMyEvents = true;

        int last = layoutManagerMyActivities.findLastCompletelyVisibleItemPosition();
        int count = myAdapter.getItemCount();
        if (last + 1 < count && !fromSwipe) {
            layoutManagerMyActivities.smoothScrollToPosition(mylistView, null, last + 1);
        }
        fromSwipe = false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            System.out.println(location.getLatitude() + " locatie");
            latitude = location.getLatitude();

            longitude = location.getLongitude();
            getAroundMeEvents("0", latitude, longitude);
        } catch (NullPointerException e) {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("Connection suspended");
    }

    private void requestLocationUpdates() {
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
        locationListener = this;
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("Connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println(location.getLatitude() + " new api");
        happeningNowLocation.locationList.add(location);
        Persistance.getInstance().setLocation(activity, happeningNowLocation);
    }

    private void onInternetRefresh() {
        getMyEvents("0");
        getFirstPageMyActivity = true;
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh1);
        mSwipeRefreshLayout.setRefreshing(false);
        NumberOfRefreshMyEvents = 0;
        getAroundMeEvents("0", latitude, longitude);
        getFirstPageAroundMe = true;
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh2);
        mSwipeRefreshLayout.setRefreshing(false);
        NumberOfRefreshAroundMe = 0;
    }

    private void prepareError(String message) {
        v.findViewById(R.id.internetRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setOnClickListener(null);
                onInternetRefresh();
            }
        });
        RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);

        TextView noConnection = (TextView) ll.findViewById(R.id.noConnectionText);
        noConnection.setText(message);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        ll.getLayoutParams().height = pixels;
        ll.setLayoutParams(ll.getLayoutParams());
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        try {
            if (error.getMessage().contains("error")) {
                String json = trimMessage(error.getMessage(), "error");
                System.out.println("sunt aici");
                if (json != null) {
                    System.out.println(error.getMessage());
                    Toast.makeText(super.getContext(), json, Toast.LENGTH_LONG).show();
                }
            } else {
                System.out.println("sunt aici 222");
                System.out.println(error.getMessage());
                Toast.makeText(super.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
            Log.d("Response", error.toString());
            if (error instanceof NetworkError) {
                this.prepareError("No internet connection!");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public String trimMessage(String json, String key) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
            if (trimmedString.equalsIgnoreCase("Invalid Auth Key provided.")) {
                deleteCachedInfo();
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    public static void deleteCachedInfo() {
        DatabaseReference userNode = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(activity).id);
        userNode.child("activeToken").setValue(false);
        Persistance.getInstance().deleteUserProfileInfo(activity);
        Log.v("logout", "am dat logout");
        SharedPreferences preferences = activity.getSharedPreferences("ProfileImage", 0);
        preferences.edit().remove("ProfileImage").apply();
        Persistance.getInstance().setHappeningNow(null, activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
