package larc.ludiconprod.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;
import larc.ludiconprod.Adapters.MainActivity.AroundMeAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.BottomBarHelper.BottomBar;
import larc.ludiconprod.BottomBarHelper.BottomBarTab;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.Layer.DataPersistence.PointsPersistence;
import larc.ludiconprod.Manifest;
import larc.ludiconprod.PasswordEncryptor;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.HappeningNowLocation;
import larc.ludiconprod.Utils.Location.GPSTracker;
import larc.ludiconprod.Utils.MainPageUtils.ViewPagerAdapter;
import larc.ludiconprod.Utils.Message;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;
import larc.ludiconprod.Utils.util.Sport;

import static larc.ludiconprod.Activities.Main.bottomBar;

/**
 * Created by ancuta on 7/26/2017.
 */

public class ActivitiesActivity extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener ,LocationListener, Response.ErrorListener{

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
    public static ArrayList<Event> myEventList = new ArrayList<Event>();
    ImageView heartImageAroundMe;
    TextView noActivitiesTextFieldAroundMe;
    TextView pressPlusButtonTextFieldAroundMe;
    ImageView heartImageMyActivity;
    TextView noActivitiesTextFieldMyActivity;
    TextView pressPlusButtonTextFieldMyActivity;
    ProgressBar progressBarMyEvents;
    ProgressBar progressBarAroundMe;
    public static int NumberOfRefreshMyEvents = 0;
    public static int NumberOfRefreshAroundMe = 0;
    public static ListView frlistView;
    public static ListView mylistView;
    Boolean isFirstTimeAroundMe = false;
    Boolean isFirstTimeMyEvents = false;
    Boolean dataComeArundeMe=false;
    Boolean dataComeMy=false;
    public static ProgressBar v1;
    public double longitude = 0;
    public double latitude = 0;
    public static Thread startHN;
    public static Thread stopHN;
    static Boolean isOnActivityPage=false;
    static int buttonState=0;//0:is never pressed 1:Check-in Performed 2:Check-Out Performed

    //LocationManager locationManager;
    LocationListener locationListener;
    Object startGettingLocation=new Object();
    private LocationRequest locationRequest;
    private FusedLocationProviderApi fusedLocationProviderApi= LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    RelativeLayout happeningNowLayout;
    public static CountDownTimer buttonSetter;
    static Button checkinButton;
    private boolean noGps = false;
    static Thread stopHappeningNow;
    static Thread startHappeningNow;
    static Boolean HPShouldBeVisible=false;
    static public HappeningNowLocation happeningNowLocation=new HappeningNowLocation();


    public ActivitiesActivity() {
        currentFragment = this;
    }

    public  Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 0) {


                HPShouldBeVisible=true;
                if(isFirstTimeMyEvents){
                    myEventList.remove(0);
                    myAdapter.notifyDataSetChanged();
                }
                System.out.println("eventStarted");


                ViewGroup.LayoutParams params = happeningNowLayout.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                happeningNowLayout.setLayoutParams(params);

                //set happening now field
                TextView weWillplay=(TextView)v.findViewById(R.id.HNPlay) ;
                TextView sportField=(TextView)v.findViewById(R.id.HNPlayWhat) ;
                TextView ludicoins=(TextView)v.findViewById(R.id.ludicoinsHN);
                TextView points=(TextView)v.findViewById(R.id.pointsHN);
                TextView location=(TextView)v.findViewById(R.id.locationHN);
                ImageView imageViewBackground=(ImageView)v.findViewById(R.id.imageViewBackground);
                CircleImageView friends0=(CircleImageView)v.findViewById(R.id.friends0HN);
                CircleImageView friends1=(CircleImageView)v.findViewById(R.id.friends1HN);
                CircleImageView friends2=(CircleImageView)v.findViewById(R.id.friends2HN);
                TextView allFriends=(TextView)v.findViewById(R.id.friendsNumberHN);
                checkinButton = (Button) v.findViewById(R.id.checkinHN);


                    checkinButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (buttonState == 0) {
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
                                        System.out.println(isOnActivityPage + " booleana " + checkinButton);
                                        if (isOnActivityPage) {
                                            checkinButton.setText("CHECK-OUT " + minutesValue + ":" + secondsValue);
                                        }


                                    }

                                    @Override
                                    public void onFinish() {

                                    }
                                }.start();

                                    requestLocationUpdates();
                                happeningNowLocation.startDate=String.valueOf(System.currentTimeMillis()/1000);




                            }else if(buttonState == 1){

                                //Call sendLocation
                                happeningNowLocation.endDate=String.valueOf(System.currentTimeMillis()/1000);
                                savePoints();


                                buttonState=2;
                                ViewGroup.LayoutParams params = happeningNowLayout.getLayoutParams();
                                params.height = 0;
                                happeningNowLayout.setLayoutParams(params);
                            }
                        }
                    });





                Event currentEvent;

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("HappeningNowEvent", 0);
                String json = sharedPreferences.getString("HappeningNowEvent", "0");
                Gson gson = new Gson();
                currentEvent = gson.fromJson(json, Event.class);



                Sport sport = new Sport(currentEvent.sportCode);
                String weWillPlayString = "";
                String sportName = "";

                if (currentEvent.sportCode.equalsIgnoreCase("JOG") || currentEvent.sportCode.equalsIgnoreCase("GYM") || currentEvent.sportCode.equalsIgnoreCase("CYC")) {
                    weWillPlayString = "Will go to ";
                    sportName =  sport.sportName;
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













            }else if(msg.what ==1 ){
                System.out.println("eventStopped");


                ViewGroup.LayoutParams params = happeningNowLayout.getLayoutParams();
                params.height = 0;
                happeningNowLayout.setLayoutParams(params);
                if(buttonSetter != null) {
                    buttonSetter.cancel();
                }

                HPShouldBeVisible=false;

                if (googleApiClient.isConnected()) {
                    googleApiClient.disconnect();
                }
            }


        }
    };

    public void savePoints(){
        System.out.println(" save points called");
        Event currentEvent;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("HappeningNowEvent", 0);
        String json = sharedPreferences.getString("HappeningNowEvent", "0");
        Gson gson = new Gson();
        currentEvent = gson.fromJson(json, Event.class);

        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(getActivity()).authKey);
        params.put("userId", Persistance.getInstance().getUserInfo(getActivity()).id);
        params.put("eventId",currentEvent.id);
        params.put("startedAt",happeningNowLocation.startDate);
        params.put("endedAt",happeningNowLocation.endDate);
        for(int i=0;i < happeningNowLocation.locationList.size();i++){
            params.put("locations["+i+"][latitude]",String.valueOf(happeningNowLocation.locationList.get(i).getLatitude()));
            params.put("locations["+i+"][longitude]",String.valueOf(happeningNowLocation.locationList.get(i).getLongitude()));
        }

        HTTPResponseController.getInstance().savePoints(params, headers, getActivity(), this);
    }

    public void getAroundMeEvents(String pageNumber, Double latitude, Double longitude) {
        v1 = (ProgressBar) v.findViewById(R.id.activityProgressBar);
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> urlParams = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(getActivity()).authKey);

        GPSTracker gps = new GPSTracker(getActivity().getApplicationContext(),  getActivity());
        if (gps.canGetLocation()) {
            this.latitude = gps.getLatitude();
            this.longitude = gps.getLongitude();

            latitude = this.latitude;
            longitude = this.longitude;

            gps.stopUsingGPS();
            this.noGps = false;
        } else {
            this.noGps = true;
            this.prepareError("No location services available!");
            return;
        }

        //set urlParams
        urlParams.put("userId", Persistance.getInstance().getUserInfo(getActivity()).id);
        urlParams.put("pageNumber", pageNumber);
        urlParams.put("userLatitude", String.valueOf(latitude));
        urlParams.put("userLongitude", String.valueOf(longitude));
        urlParams.put("userRange", "" + Persistance.getInstance().getUserInfo(getActivity()).range);
        String userSport = "";
        for (int i = 0; i < Persistance.getInstance().getUserInfo(getActivity()).sports.size(); i++) {
            if (i < Persistance.getInstance().getUserInfo(getActivity()).sports.size() - 1) {
                userSport = userSport + Persistance.getInstance().getUserInfo(getActivity()).sports.get(i).code + ";";
            } else {
                userSport = userSport + Persistance.getInstance().getUserInfo(getActivity()).sports.get(i).code;
            }
        }
        urlParams.put("userSports", userSport);
        //get Around Me Event
        HTTPResponseController.getInstance().getAroundMeEvent(params, headers, getActivity(), urlParams, this);
    }

    public void getMyEvents(String pageNumber) {

        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> urlParams = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(getActivity()).authKey);

        //set urlParams

        urlParams.put("userId", Persistance.getInstance().getUserInfo(getActivity()).id);
        urlParams.put("pageNumber", pageNumber);


        //get Around Me Event
        HTTPResponseController.getInstance().getMyEvent(params, headers, getActivity(), urlParams, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (fradapter != null) fradapter.notifyDataSetChanged();
        if (myAdapter != null) myAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = inflater.getContext();
        isOnActivityPage=true;

        v = inflater.inflate(R.layout.activities_acitivity, container, false);
        v1 = (ProgressBar) v.findViewById(R.id.activityProgressBar);

        int NumberOfUnseen=Persistance.getInstance().getUnseenChats(getActivity()).size();

        BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_friends);
        nearby.setBadgeCount(NumberOfUnseen);

        //set activeToken in firebase node for notification
        final DatabaseReference userNode = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(getActivity()).id);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("regId", 0);
        String activeToken = sharedPreferences.getString("regId", "0");
        if(!activeToken.equalsIgnoreCase("0")) {
            userNode.child("activeToken").setValue(activeToken);
        }
        myEventList.clear();
        aroundMeEventList.clear();

        try {
            super.onCreate(savedInstanceState);

            // Creating ViewPager Adapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs
            adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), Titles, Numboftabs);

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

            // Initialize Crashlytics (Fabric)
            //Fabric.with(getActivity(), new Crashlytics());
            // logUser();

            //dialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait", true);

            // Check if there are any unsaved points and save them
            /*
            pointsPersistence = PointsPersistence.getInstance();
            Map<String,Integer> unsavedPointsMap = pointsPersistence.getUnsavedPoints(getActivity());
            for (final Map.Entry<String, Integer> entry : unsavedPointsMap.entrySet()) {
                if (entry.getValue() == 0) continue;



            }*/

            if(Persistance.getInstance().getLocation(getActivity()).locationList.size() > 0 && !HPShouldBeVisible) {
                 happeningNowLocation=Persistance.getInstance().getLocation(getActivity());
                happeningNowLocation.endDate=String.valueOf(System.currentTimeMillis()/1000);
                 savePoints();

            }



            myAdapter = new MyAdapter(myEventList, getActivity().getApplicationContext(), getActivity(), getResources(), currentFragment);
            fradapter = new AroundMeAdapter(aroundMeEventList, getActivity().getApplicationContext(), getActivity(), getResources(), currentFragment);

            getAroundMeEvents("0", latitude, longitude);
            getMyEvents("0");

            NumberOfRefreshMyEvents = 0;
            NumberOfRefreshAroundMe = 0;

            happeningNowLayout=(RelativeLayout) v.findViewById(R.id.generalHappeningNowLayout);

            Runnable runnableStart=new Runnable() {
                @Override
                public void run() {
                    try {
                        ArrayList<Event> myFirstPageEventList = Persistance.getInstance().getMyActivities(getActivity());
                        if (myFirstPageEventList.size() >= 1) {
                            //long timeToNextEvent = (myFirstPageEventList.get(0).eventDateTimeStamp - System.currentTimeMillis() / 1000) / 60;
                            long timeToNextEvent = (1504869293 - System.currentTimeMillis() / 1000) ;
                            while (timeToNextEvent >= 0) {
                                Thread.sleep(1000);
                                timeToNextEvent -= 1;
                            }
                            //happening now started
                        if((timeToNextEvent > -3600 && buttonState == 0)|| (timeToNextEvent > -7200 && buttonState == 1)) {

                                googleApiClient.connect();
                                myEventList.remove(0);
                                myAdapter.notifyDataSetChanged();

                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("HappeningNowEvent", 0).edit();
                            Gson gson = new Gson();
                            editor.putString("HappeningNowEvent", gson.toJson(Persistance.getInstance().getMyActivities(getActivity()).get(0)));
                            editor.commit();

                                handler.sendEmptyMessage(0);

                        }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };

            Runnable runnableStop=new Runnable() {
                @Override
                public void run() {
                    try {
                        ArrayList<Event> myFirstPageEventList = Persistance.getInstance().getMyActivities(getActivity());
                        if (myFirstPageEventList.size() >= 1) {
                            //long timeToNextEvent = (myFirstPageEventList.get(0).eventDateTimeStamp - System.currentTimeMillis() / 1000) / 60;
                            long timeToNextEvent = (System.currentTimeMillis() / 1000 - 1504869293) ;
                            while ((buttonState == 0 && timeToNextEvent <= 3600 ) || (buttonState == 1 && timeToNextEvent < 7200)) {
                                if(buttonState == 2)
                                    break;
                                Thread.sleep(1000);
                                timeToNextEvent += 1;
                            }
                            if(timeToNextEvent > 7200){
                                savePoints();
                            }
                            //happening now stoped
                            handler.sendEmptyMessage(1);

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };

            if(stopHappeningNow == null || !stopHappeningNow.isAlive()) {
                stopHappeningNow = new Thread(runnableStop);
            }
            if(startHappeningNow == null || !stopHappeningNow.isAlive()) {
                startHappeningNow = new Thread(runnableStart);
            }




            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            locationRequest=new LocationRequest();
            locationRequest.setInterval(10*1000);
            locationRequest.setFastestInterval(5*1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            if(HPShouldBeVisible){
                googleApiClient.connect();
                handler.sendEmptyMessage(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    public void updateListOfEventsAroundMe(final boolean eventHappeningNow) {
        RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);
        ll.getLayoutParams().height = 0;
        ll.setLayoutParams(ll.getLayoutParams());
        if (this.noGps) {
            this.prepareError("No location services available!");
        }

        // stop swiping on my events
        final SwipeRefreshLayout mSwipeRefreshLayout2 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh2);
        // mSwipeRefreshLayout2.setEnabled(false);
        // mSwipeRefreshLayout2.setFocusable(false);
        fradapter.notifyDataSetChanged();
        frlistView = (ListView) v.findViewById(R.id.events_listView2);
        heartImageAroundMe = (ImageView) v.findViewById(R.id.heartImageAroundMe);
        progressBarAroundMe = (ProgressBar) v.findViewById(R.id.progressBarAroundMe);
        progressBarAroundMe.setIndeterminate(true);
        progressBarAroundMe.setAlpha(0f);


        noActivitiesTextFieldAroundMe = (TextView) v.findViewById(R.id.noActivitiesTextFieldAroundMe);
        pressPlusButtonTextFieldAroundMe = (TextView) v.findViewById(R.id.pressPlusButtonTextFieldAroundMe);
        final FloatingActionButton createNewActivityFloatingButtonAroundMe = (FloatingActionButton) v.findViewById(R.id.floatingButton2);
        createNewActivityFloatingButtonAroundMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateNewActivity.class);
                startActivity(intent);
            }
        });

        if (!isFirstTimeAroundMe) {
            //  if (
            //      friendsEventsList.size() == 0 &&
            //          !eventHappeningNow) {
            // place holder no events created
            //  }
            //else frlistView.setBackgroundColor(Color.parseColor("#FFFFFF"));

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
            frlistView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    if (v != null && frlistView.getChildCount() > 0) {
                        if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                            if (frlistView.getLastVisiblePosition() == frlistView.getAdapter().getCount() - 1 &&
                                    frlistView.getChildAt(frlistView.getChildCount() - 1).getBottom() <= frlistView.getHeight()) {

                                    // mSwipeRefreshLayout1.setRefreshing(true);
                                progressBarAroundMe.setAlpha(1f);


//                                Log.v("numar de elemete",String.valueOf(frlistView.getAdapter().getCount()));
                                    //(new Handler()).postDelayed(new Runnable() {
                                ////@Override
                                // public void run() {
                                getAroundMeEvents(String.valueOf(NumberOfRefreshAroundMe),latitude,longitude);

                                // }
                                // }, 1000);
                                    //
                                }

                      }

                    }

                    return false;
                }

            });
        }

        if (!addedSwipeAroundMe) {
            mSwipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getAroundMeEvents("0",latitude,longitude);
                    getFirstPageAroundMe = true;
                    mSwipeRefreshLayout2.setRefreshing(false);
                    NumberOfRefreshAroundMe = 0;

                }
            });
            addedSwipeAroundMe = true;
        }


        progressBarAroundMe.setAlpha(0f);

        isFirstTimeAroundMe = true;

    }

    public void updateListOfMyEvents(final boolean eventHappeningNow) {
        RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);
        ll.getLayoutParams().height = 0;
        ll.setLayoutParams(ll.getLayoutParams());
        if (this.noGps) {
            this.prepareError("No location services available!");
        }
        //if(HPShouldBeVisible){
           // myEventList.remove(0);
       // }

        // stop swiping on my events
        final SwipeRefreshLayout mSwipeRefreshLayout1 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh1);
        // mSwipeRefreshLayout2.setEnabled(false);
        // mSwipeRefreshLayout2.setFocusable(false);
        myAdapter.notifyDataSetChanged();
        mylistView = (ListView) v.findViewById(R.id.events_listView1);
        heartImageMyActivity = (ImageView) v.findViewById(R.id.heartImageMyActivity);
        noActivitiesTextFieldMyActivity = (TextView) v.findViewById(R.id.noActivitiesTextFieldMyActivity);
        pressPlusButtonTextFieldMyActivity = (TextView) v.findViewById(R.id.pressPlusButtonTextFieldMyActivity);
        progressBarMyEvents = (ProgressBar) v.findViewById(R.id.progressBarMyEvents);

        progressBarMyEvents.setIndeterminate(true);
        progressBarMyEvents.setAlpha(0f);

        final FloatingActionButton createNewActivityFloatingButtonMyActivity = (FloatingActionButton) v.findViewById(R.id.floatingButton1);
        createNewActivityFloatingButtonMyActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateNewActivity.class);
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
            mylistView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    if (v != null && mylistView.getChildCount() > 0) {
                        if (event.getAction() == android.view.MotionEvent.ACTION_UP) {


                            if (mylistView.getLastVisiblePosition() == mylistView.getAdapter().getCount() - 1 &&
                                    mylistView.getChildAt(mylistView.getChildCount() - 1).getBottom() <= mylistView.getHeight()) {

                                // mSwipeRefreshLayout1.setRefreshing(true);
                                progressBarMyEvents.setAlpha(1f);
                                getMyEvents(String.valueOf(NumberOfRefreshMyEvents));

                            }
                        }
                    }
                    return false;
                }
            });
        }

        if (!addedSwipeMyActivity) {
            mSwipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getMyEvents("0");
                    getFirstPageMyActivity = true;
                    mSwipeRefreshLayout1.setRefreshing(false);
                    NumberOfRefreshMyEvents = 0;
                }
            });
            addedSwipeMyActivity = true;
        }
        if(!stopHappeningNow.isAlive() && !startHappeningNow.isAlive()){
            stopHappeningNow.start();
            startHappeningNow.start();
        }

        isFirstTimeMyEvents = true;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
       // requestLocationUpdates();
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
        locationListener=this;
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("Connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println(location.getLatitude()+" new api");
        happeningNowLocation.locationList.add(location);
        Persistance.getInstance().setLocation(getActivity(),happeningNowLocation);


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
        Toast.makeText(super.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        Log.d("Response", error.toString());
        if (error instanceof NetworkError) {
            this.prepareError("No internet connection!");
        }
    }
}
