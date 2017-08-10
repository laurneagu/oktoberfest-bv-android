package larc.ludiconprod.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import io.fabric.sdk.android.Fabric;
import larc.ludiconprod.Adapters.MainActivity.AroundMeAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.Layer.DataPersistence.PointsPersistence;
import larc.ludiconprod.PasswordEncryptor;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.Location.GPSTracker;
import larc.ludiconprod.Utils.MainPageUtils.ViewPagerAdapter;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;

/**
 * Created by ancuta on 7/26/2017.
 */

public class ActivitiesActivity extends Fragment {

    ViewPager pager;
    private Context mContext;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"AROUND ME", "MY ACTIVITIES"};
    int Numboftabs = 2;
    private View v;
    boolean addedSwipeAroundMe = false;
    boolean addedSwipeMyActivity = false;
    static public AroundMeAdapter fradapter;
    static public MyAdapter myAdapter;
    static public ActivitiesActivity currentFragment;
    public static ArrayList<Event> aroundMeEventList=new  ArrayList<Event>();
    public static ArrayList<Event> myEventList=new  ArrayList<Event>();
    ImageView heartImageAroundMe;
    TextView noActivitiesTextFieldAroundMe;
    TextView pressPlusButtonTextFieldAroundMe;
    ImageView heartImageMyActivity;
    TextView noActivitiesTextFieldMyActivity;
    TextView pressPlusButtonTextFieldMyActivity;
    ProgressBar progressBarMyEvents;
    ProgressBar progressBarAroundMe;
    public static int NumberOfRefreshMyEvents=0;
    public static int NumberOfRefreshAroundMe=0;
    ListView frlistView;
    Boolean isFirstTimeAroundMe=false;
    Boolean isFirstTimeMyEvents=false;

    final ArrayList<String> favoriteSports = new ArrayList<>();
    private final ArrayList<Event> myEventsList = new ArrayList<>();

    public ActivitiesActivity(){
        currentFragment=this;
    }

    public void getAroundMeEvents(String pageNumber){
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> urlParams = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(getActivity()).authKey);

        //set urlParams
        double longitude=0;
        double latitude=0;
        GPSTracker gps = new GPSTracker(getActivity().getApplicationContext(),  getActivity());
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            gps.stopUsingGPS();
        }

        urlParams.put("userId",Persistance.getInstance().getUserInfo(getActivity()).id);
        urlParams.put("pageNumber",pageNumber);
        urlParams.put("userLatitude",String.valueOf(latitude));
        urlParams.put("userLongitude",String.valueOf(longitude));
        urlParams.put("userRange","10000");
        String userSport="";
        for(int i = 0;i < Persistance.getInstance().getUserInfo(getActivity()).sports.size();i++) {
            if(i < Persistance.getInstance().getUserInfo(getActivity()).sports.size()-1) {
                userSport = userSport + Persistance.getInstance().getUserInfo(getActivity()).sports.get(i).code + ";";
            }
            else{
                userSport = userSport + Persistance.getInstance().getUserInfo(getActivity()).sports.get(i).code;
            }
        }
        urlParams.put("userSports",userSport);
        //get Around Me Event
        HTTPResponseController.getInstance().getAroundMeEvent(params, headers, getActivity(),urlParams);
    }
    public void getMyEvents(String pageNumber){
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> urlParams = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(getActivity()).authKey);

        //set urlParams

        urlParams.put("userId",Persistance.getInstance().getUserInfo(getActivity()).id);
        urlParams.put("pageNumber",pageNumber);


        //get Around Me Event
        HTTPResponseController.getInstance().getMyEvent(params, headers, getActivity(),urlParams);
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
        v = inflater.inflate(R.layout.activities_acitivity, container, false);
        myEventList.clear();
        aroundMeEventList.clear();
/*
        if(locationChecker == null) locationChecker = LocationChecker.getInstance();
        locationChecker.setContext(mContext);
        */

        try {
            super.onCreate(savedInstanceState);


            //Go to chat
            /*
            String chatUID = getActivity().getIntent().getStringExtra("chatUID");
            if (chatUID != null) {
                Intent goToChatList = new Intent(getActivity(), ChatListActivity.class);
                goToChatList.putExtra("chatUID", chatUID);
                startActivity(goToChatList);


            }
            */

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
            getAroundMeEvents("0");
            getMyEvents("0");

            myAdapter = new MyAdapter(myEventList, getActivity().getApplicationContext(), getActivity(), getResources(), currentFragment);


            fradapter = new AroundMeAdapter(aroundMeEventList, getActivity().getApplicationContext(), getActivity(), getResources(), currentFragment);




            NumberOfRefreshMyEvents=0;
            NumberOfRefreshAroundMe=0;



        } catch (Exception e) {
            e.printStackTrace();

        }
return v;
    }

    public void updateListOfEventsAroundMe(final boolean eventHappeningNow) {
        // stop swiping on my events
        final SwipeRefreshLayout mSwipeRefreshLayout2 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh2);
       // mSwipeRefreshLayout2.setEnabled(false);
       // mSwipeRefreshLayout2.setFocusable(false);
        fradapter.notifyDataSetChanged();
         frlistView = (ListView) v.findViewById(R.id.events_listView2);
        heartImageAroundMe=(ImageView) v.findViewById(R.id.heartImageAroundMe);
        progressBarAroundMe=(ProgressBar)v.findViewById(R.id.progressBarAroundMe) ;
        progressBarAroundMe.setIndeterminate(true);
        progressBarAroundMe.setAlpha(0f);
        noActivitiesTextFieldAroundMe=(TextView)v.findViewById(R.id.noActivitiesTextFieldAroundMe);
        pressPlusButtonTextFieldAroundMe=(TextView)v.findViewById(R.id.pressPlusButtonTextFieldAroundMe);
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
        if(aroundMeEventList.size() == 0){
            heartImageAroundMe.setVisibility(View.VISIBLE);
            noActivitiesTextFieldAroundMe.setVisibility(View.VISIBLE);
            pressPlusButtonTextFieldAroundMe.setVisibility(View.VISIBLE);
        }
        else{
            heartImageAroundMe.setVisibility(View.INVISIBLE);
            noActivitiesTextFieldAroundMe.setVisibility(View.INVISIBLE);
            pressPlusButtonTextFieldAroundMe.setVisibility(View.INVISIBLE);
        }
        frlistView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    if (v != null)  {

                        if (frlistView.getLastVisiblePosition() == frlistView.getAdapter().getCount() - 1 &&
                                frlistView.getChildAt(frlistView.getChildCount() - 1).getBottom() <= frlistView.getHeight()) {

                            // mSwipeRefreshLayout1.setRefreshing(true);
                            progressBarAroundMe.setAlpha(1f);

                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getAroundMeEvents(String.valueOf(NumberOfRefreshAroundMe));
                                }
                            }, 1000);

                        }
                    }
                }
                return false;
            }
        });

                    if (!addedSwipeAroundMe) {
                        mSwipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                getAroundMeEvents("0");
                                mSwipeRefreshLayout2.setRefreshing(false);
                                NumberOfRefreshAroundMe = 0;
                                aroundMeEventList.clear();
                            }
                        });
                        addedSwipeAroundMe = true;
                    }
        progressBarAroundMe.setAlpha(0f);
        isFirstTimeAroundMe=true;

                }

    public void updateListOfMyEvents(final boolean eventHappeningNow) {
        // stop swiping on my events
        final SwipeRefreshLayout mSwipeRefreshLayout1 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh1);
        // mSwipeRefreshLayout2.setEnabled(false);
        // mSwipeRefreshLayout2.setFocusable(false);
        myAdapter.notifyDataSetChanged();
        final ListView mylistView = (ListView) v.findViewById(R.id.events_listView1);
        heartImageMyActivity=(ImageView) v.findViewById(R.id.heartImageMyActivity);
        noActivitiesTextFieldMyActivity=(TextView)v.findViewById(R.id.noActivitiesTextFieldMyActivity);
        pressPlusButtonTextFieldMyActivity=(TextView)v.findViewById(R.id.pressPlusButtonTextFieldMyActivity);
        progressBarMyEvents=(ProgressBar)v.findViewById(R.id.progressBarMyEvents);
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
            //  if (
            //      friendsEventsList.size() == 0 &&
            //          !eventHappeningNow) {
            // place holder no events created
            //  }
            //else frlistView.setBackgroundColor(Color.parseColor("#FFFFFF"));

            mylistView.setAdapter(myAdapter);
        }
        if(myEventList.size() == 0){
            heartImageMyActivity.setVisibility(View.VISIBLE);
            noActivitiesTextFieldMyActivity.setVisibility(View.VISIBLE);
            pressPlusButtonTextFieldMyActivity.setVisibility(View.VISIBLE);
        }
        else{
            heartImageMyActivity.setVisibility(View.INVISIBLE);
            noActivitiesTextFieldMyActivity.setVisibility(View.INVISIBLE);
            pressPlusButtonTextFieldMyActivity.setVisibility(View.INVISIBLE);
        }
        mylistView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    if (v != null) {

                        if (mylistView.getLastVisiblePosition() == mylistView.getAdapter().getCount() - 1 &&
                                mylistView.getChildAt(mylistView.getChildCount() - 1).getBottom() <= mylistView.getHeight()) {

                            // mSwipeRefreshLayout1.setRefreshing(true);
                            progressBarMyEvents.setAlpha(1f);

                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getMyEvents(String.valueOf(NumberOfRefreshMyEvents));
                                    progressBarMyEvents.setAlpha(0f);
                                }
                            }, 1000);

                        }
                    }
                }
                return false;
            }
        });

        if (!addedSwipeMyActivity) {
            mSwipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getMyEvents("0");
                    mSwipeRefreshLayout1.setRefreshing(false);
                    NumberOfRefreshMyEvents=0;
                    myEventList.clear();
                }
            });
            addedSwipeMyActivity = true;
        }
        isFirstTimeMyEvents=true;
    }


}
