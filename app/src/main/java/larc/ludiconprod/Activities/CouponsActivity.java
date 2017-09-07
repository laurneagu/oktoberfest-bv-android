package larc.ludiconprod.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class CouponsActivity extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

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
    private boolean noGps = false;

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
            this.noGps = false;
        } else {
            this.noGps = true;
            this.prepareError("No location services available!");
            return;
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

        HTTPResponseController.getInstance().getCoupons(urlParams, headers, this, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onCouponsResponse(response);
            }
        }, this);
    }

    public void getMyCoupons(String pageNumber) {
        HashMap<String, String> headers = new HashMap<>();
        //headers.put("Content-Type", "application/json");
        headers.put("authKey", Persistance.getInstance().getUserInfo(getActivity()).authKey);
        String urlParams = "";

        urlParams += "userId=" + Persistance.getInstance().getUserInfo(getActivity()).id;
        urlParams += "&page=" + pageNumber;

        HTTPResponseController.getInstance().getMyCoupons(urlParams, headers, this, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onMyCouponsResponse(response);
            }
        }, this);
    }

    public void updateCouponsList() {
        RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);
        ll.getLayoutParams().height = 0;
        ll.setLayoutParams(ll.getLayoutParams());
        if (this.noGps) {
            this.prepareError("No location services available!");
        }

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.couponsSwapRefresh);

        this.couponsAdapter.notifyDataSetChanged();
        final ListView listView = (ListView) v.findViewById(R.id.couponsList);
        ImageView heartImage = (ImageView) v.findViewById(R.id.heartImageCoupons);
        final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progressCoupons) ;
        progressBar.setIndeterminate(true);
        progressBar.setAlpha(0f);

        TextView noCoupons = (TextView)v.findViewById(R.id.noCoupons);
        TextView discoverActivities = (TextView) v.findViewById(R.id.noCouponsText);
        Button noMyCouponsButton = (Button) v.findViewById(R.id.noCouponsButton);

        if (!this.firstTimeCoupons) {
            listView.setAdapter(this.couponsAdapter);
        }
        if (this.coupons.size() == 0) {
            heartImage.setVisibility(View.VISIBLE);
            noCoupons.setVisibility(View.VISIBLE);
            discoverActivities.setVisibility(View.VISIBLE);
            noMyCouponsButton.setVisibility(View.VISIBLE);
            noMyCouponsButton.setEnabled(true);
            noMyCouponsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*Intent intent = new Intent(getActivity(), Main.class);
                    intent.putExtra("Tab", R.id.tab_activities);
                    startActivity(intent);*/
                    Main mainFr = (Main) getActivity();
                    mainFr.bottomBar.setDefaultTab(R.id.tab_activities);
                }
            });
        } else {
            heartImage.setVisibility(View.INVISIBLE);
            noCoupons.setVisibility(View.INVISIBLE);
            discoverActivities.setVisibility(View.INVISIBLE);
            noMyCouponsButton.setEnabled(false);
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
        RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);
        ll.getLayoutParams().height = 0;
        ll.setLayoutParams(ll.getLayoutParams());
        if (this.noGps) {
            this.prepareError("No location services available!");
        }

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.myCouponsSwapRefresh);

        this.myCouponsAdapter.notifyDataSetChanged();
        final ListView listView = (ListView) v.findViewById(R.id.myCouponsList);
        ImageView heartImage = (ImageView) v.findViewById(R.id.heartImageMyCoupons);
        final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progressMyCoupons) ;
        progressBar.setIndeterminate(true);
        progressBar.setAlpha(0f);

        TextView noCoupons = (TextView)v.findViewById(R.id.noMyCoupons);
        TextView discoverActivities = (TextView) v.findViewById(R.id.noMyCouponsText);
        Button noMyCouponsButton = (Button) v.findViewById(R.id.noMyCouponsButton);

        if (!this.firstTimeMyCoupons) {
            listView.setAdapter(this.myCouponsAdapter);
        }
        if (this.myCoupons.size() == 0) {
            heartImage.setVisibility(View.VISIBLE);
            noCoupons.setVisibility(View.VISIBLE);
            discoverActivities.setVisibility(View.VISIBLE);
            noMyCouponsButton.setVisibility(View.VISIBLE);
            noMyCouponsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pager.setCurrentItem(0);
                }
            });
            noMyCouponsButton.setEnabled(true);
        } else {
            heartImage.setVisibility(View.INVISIBLE);
            noCoupons.setVisibility(View.INVISIBLE);
            discoverActivities.setVisibility(View.INVISIBLE);
            noMyCouponsButton.setVisibility(View.INVISIBLE);
            noMyCouponsButton.setEnabled(false);
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

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(super.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        if (error instanceof NetworkError) {
            this.prepareError("No internet connection!");
        } else {
            RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);
            ll.getLayoutParams().height = 0;
            ll.setLayoutParams(ll.getLayoutParams());
        }
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

    private void onInternetRefresh() {
        coupons.clear();
        getCoupons("0");
        firstPageCoupons = true;
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.couponsSwapRefresh);
        mSwipeRefreshLayout.setRefreshing(false);
        numberOfRefreshCoupons = 0;

        myCoupons.clear();
        getMyCoupons("0");
        firstPageMyCoupons = true;
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.myCouponsSwapRefresh);
        mSwipeRefreshLayout.setRefreshing(false);
        numberOfRefreshMyCoupons = 0;
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

    public void onMyCouponsResponse(JSONObject response) {
        try {
            JSONArray coupons = response.getJSONArray("coupons");

            CouponsActivity ca = this;

            Coupon c;
            for (int i = 0; i < coupons.length(); ++i) {
                JSONObject o = coupons.getJSONObject(i);
                c = new Coupon();
                c.couponBlockId = o.getString("couponBlockId");
                c.title = o.getString("title");
                c.description = o.getString("description");
                c.expiryDate = Long.parseLong(o.getString("expiryDate"));
                c.numberOfCoupons = Integer.parseInt(o.getString("numberOfCoupons"));
                c.ludicoins = Integer.parseInt(o.getString("ludicoins"));
                c.companyPicture = o.getString("companyPicture");
                c.companyName = o.getString("companyName");
                c.discountCode = o.getString("discountCode");

                ca.myCoupons.add(c);
            }

            ca.updateMyCouponsList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onCouponsResponse(JSONObject response) {
        try {
            JSONArray coupons = response.getJSONArray("coupons");

            CouponsActivity ca = this;

            Coupon c;
            for (int i = 0; i < coupons.length(); ++i) {
                JSONObject o = coupons.getJSONObject(i);
                c = new Coupon();
                c.couponBlockId = o.getString("couponBlockId");
                c.title = o.getString("title");
                c.description = o.getString("description");
                c.expiryDate = Long.parseLong(o.getString("expiryDate"));
                c.numberOfCoupons = Integer.parseInt(o.getString("numberOfCoupons"));
                c.ludicoins = Integer.parseInt(o.getString("ludicoins"));
                c.companyPicture = o.getString("companyPicture");
                c.companyName = o.getString("companyName");

                ca.coupons.add(c);
            }

            ca.updateCouponsList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            Toast.makeText(super.getActivity(), "" + response, Toast.LENGTH_SHORT).show();

            this.coupons.clear();
            this.getCoupons("0");
            this.firstPageCoupons = true;
            this.numberOfRefreshCoupons = 0;

            this.myCoupons.clear();
            this.getMyCoupons("0");
            this.firstPageMyCoupons = true;
            this.numberOfRefreshMyCoupons = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}