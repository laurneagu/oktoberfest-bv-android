package larc.ludiconprod.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import larc.ludiconprod.Adapters.CouponsActivity.CouponsAdapter;
import larc.ludiconprod.Adapters.CouponsActivity.MyCouponsAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Coupon;
import larc.ludiconprod.Utils.CouponsUtils.CouponsPagerAdapter;
import larc.ludiconprod.Utils.Location.GPSTracker;
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

    boolean firstTimeCoupons = false;
    public int numberOfRefreshCoupons = 0;
    public boolean firstPageCoupons = true;
    boolean addedSwipeCoupons = false;

    boolean firstTimeMyCoupons = false;
    public int numberOfRefreshMyCoupons = 0;
    public boolean firstPageMyCoupons = true;
    boolean addedSwipeMyCoupons = false;

    public void getCoupons() {

    }

    public void getCoupons(String pageNumber) {
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

        HTTPResponseController.getInstance().getCoupons(urlParams, headers, this);
    }

    public void getMyCoupons(String pageNumber) {
        HashMap<String, String> headers = new HashMap<>();
        //headers.put("Content-Type", "application/json");
        headers.put("authKey", Persistance.getInstance().getUserInfo(getActivity()).authKey);
        String urlParams = "";

        urlParams += "userId=" + Persistance.getInstance().getUserInfo(getActivity()).id;
        urlParams += "&page=" + pageNumber;

        HTTPResponseController.getInstance().getMyCoupons(urlParams, headers, this);
    }

    public void updateCouponsList() {
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.couponsSwapRefresh);

        this.couponsAdapter.notifyDataSetChanged();
        final ListView listView = (ListView) v.findViewById(R.id.couponsList);
        ImageView heartImage = (ImageView) v.findViewById(R.id.heartImageCoupons);
        final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progressCoupons) ;
        progressBar.setIndeterminate(true);
        progressBar.setAlpha(0f);

        TextView noCoupons = (TextView)v.findViewById(R.id.noCoupons);
        TextView discoverActivities = (TextView) v.findViewById(R.id.noCouponsText);

        if (!this.firstTimeCoupons) {
            listView.setAdapter(this.couponsAdapter);
        }
        if (this.coupons.size() == 0) {
            heartImage.setVisibility(View.VISIBLE);
            noCoupons.setVisibility(View.VISIBLE);
            discoverActivities.setVisibility(View.VISIBLE);
        } else {
            heartImage.setVisibility(View.INVISIBLE);
            noCoupons.setVisibility(View.INVISIBLE);
            discoverActivities.setVisibility(View.INVISIBLE);
        }

        if(listView != null) {
            listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    if (v != null && listView.getChildCount() > 0) {
                        if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                            if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1 && listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {
                                progressBar.setAlpha(1f);
                                ++numberOfRefreshCoupons;
                                getCoupons(String.valueOf(numberOfRefreshCoupons));
                            }
                        }
                    }
                    return false;
                }
            });
        }
        if (!this.addedSwipeCoupons) {
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    coupons.clear();
                    getCoupons("0");
                    firstPageCoupons = true;
                    mSwipeRefreshLayout.setRefreshing(false);
                    numberOfRefreshCoupons = 0;
                }
            });
            this.addedSwipeCoupons = true;
        }
        progressBar.setAlpha(0f);
        this.firstTimeCoupons = true;
    }

    public void updateMyCouponsList() {
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.myCouponsSwapRefresh);

        this.myCouponsAdapter.notifyDataSetChanged();
        final ListView listView = (ListView) v.findViewById(R.id.myCouponsList);
        ImageView heartImage = (ImageView) v.findViewById(R.id.heartImageMyCoupons);
        final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progressMyCoupons) ;
        progressBar.setIndeterminate(true);
        progressBar.setAlpha(0f);

        TextView noCoupons = (TextView)v.findViewById(R.id.noMyCoupons);
        TextView discoverActivities = (TextView) v.findViewById(R.id.noMyCouponsText);

        if (!this.firstTimeMyCoupons) {
            listView.setAdapter(this.myCouponsAdapter);
        }
        if (this.myCoupons.size() == 0) {
            heartImage.setVisibility(View.VISIBLE);
            noCoupons.setVisibility(View.VISIBLE);
            discoverActivities.setVisibility(View.VISIBLE);
        } else {
            heartImage.setVisibility(View.INVISIBLE);
            noCoupons.setVisibility(View.INVISIBLE);
            discoverActivities.setVisibility(View.INVISIBLE);
        }

        if(listView != null) {
            listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    if (v != null && listView.getChildCount() > 0) {
                        if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                            if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1 && listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {
                                progressBar.setAlpha(1f);
                                ++numberOfRefreshMyCoupons;
                                getMyCoupons(String.valueOf(numberOfRefreshMyCoupons));
                            }
                        }
                    }
                    return false;
                }
            });
        }
        if (!this.addedSwipeMyCoupons) {
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    myCoupons.clear();
                    getMyCoupons("0");
                    firstPageMyCoupons = true;
                    mSwipeRefreshLayout.setRefreshing(false);
                    numberOfRefreshMyCoupons = 0;
                }
            });
            this.addedSwipeMyCoupons = true;
        }
        progressBar.setAlpha(0f);
        this.firstTimeMyCoupons = true;
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

            this.tabs = (SlidingTabLayout) v.findViewById(R.id.couponsTabs);
            this.tabs.setDistributeEvenly(false);

            this.tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            this.tabs.setViewPager(pager);

            this.couponsAdapter = new CouponsAdapter(this.coupons, getActivity().getApplicationContext(), getActivity(), getResources(), this);
            this.myCouponsAdapter = new MyCouponsAdapter(this.myCoupons, getActivity().getApplicationContext(), getActivity(), getResources(), this);

            getCoupons("0");
            getMyCoupons("0");

            this.numberOfRefreshCoupons = 0;
            this.numberOfRefreshMyCoupons = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }


}