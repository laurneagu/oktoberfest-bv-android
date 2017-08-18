package larc.ludiconprod.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import larc.ludiconprod.Adapters.CouponsActivity.CouponsAdapter;
import larc.ludiconprod.Adapters.CouponsActivity.MyCouponsAdapter;
import larc.ludiconprod.Adapters.EditProfile.EditActivitiesAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Coupon;
import larc.ludiconprod.Utils.CouponsUtils.CouponsPagerAdapter;
import larc.ludiconprod.Utils.Location.GPSTracker;
import larc.ludiconprod.Utils.MyProfileUtils.EditViewPagerAdapter;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;
import larc.ludiconprod.Utils.util.Sport;

public class CouponsActivity extends Fragment {

    private static final CharSequence TITLES[] = {"COUPONS", "MY COUPONS"};

    private Context mContext;
    private int tabsNumber = 2;
    private View v;
    private CouponsPagerAdapter adapter;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    private CouponsAdapter couponsAdapter;
    private MyCouponsAdapter myCouponsAdapter;

    public ArrayList<Coupon> coupons = new ArrayList<>();
    public ArrayList<Coupon> myCoupons = new ArrayList<>();

    public void getCoupons(String pageNumber){
        HashMap<String, String> headers = new HashMap<>();
        //headers.put("Content-Type", "application/json");
        headers.put("authKey", Persistance.getInstance().getUserInfo(getActivity()).authKey);
        String urlParams = "";

        //set urlParams
        double longitude = 0;
        double latitude = 0;
        GPSTracker gps = new GPSTracker(getActivity().getApplicationContext(),  getActivity());
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            gps.stopUsingGPS();
        }

        urlParams += "userId=" + Persistance.getInstance().getUserInfo(getActivity()).id;
        urlParams += "&page=" + pageNumber;
        urlParams += "&latitude=" + String.valueOf(latitude);
        urlParams += "&longitude=" + String.valueOf(longitude);
        urlParams += "&range=" + 10000;
        String userSport = "";
        ArrayList<Sport> sports = Persistance.getInstance().getUserInfo(getActivity()).sports;
        for(int i = 0; i < sports.size(); ++i) {
            if(i < sports.size() - 1) {
                userSport = userSport + sports.get(i).code + ";";
            } else {
                userSport = userSport + sports.get(i).code;
            }
        }
        urlParams += "&sportList=" + userSport;
        //get Around Me Event
        HTTPResponseController.getInstance().getCoupons(urlParams, headers, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = inflater.getContext();
        v = inflater.inflate(R.layout.coupons_activity, container, false);

        try {
            super.onCreate(savedInstanceState);

            this.adapter = new CouponsPagerAdapter(this.getFragmentManager(), CouponsActivity.TITLES, this.tabsNumber);

            pager = (ViewPager) v.findViewById(R.id.couponsPager);
            pager.setAdapter(adapter);

            // Assiging the Sliding Tab Layout View
            this.tabs = (SlidingTabLayout) v.findViewById(R.id.couponsTabs);
            this.tabs.setDistributeEvenly(false); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            // Setting Custom Color for the Scroll bar indicator of the Tab View
            this.tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            this.couponsAdapter = new CouponsAdapter(this.coupons, getActivity().getApplicationContext(), getActivity(), getResources(), this);
            this.myCouponsAdapter = new MyCouponsAdapter(this.myCoupons, getActivity().getApplicationContext(), getActivity(), getResources(), this);

            getCoupons("0");

            // Setting the ViewPager For the SlidingTabsLayout
            this.tabs.setViewPager(pager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    public void updateCouponsList() {

    }
}