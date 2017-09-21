package larc.ludiconprod.Activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
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
import java.util.Date;
import java.util.HashMap;

import larc.ludiconprod.Adapters.Balance.BalanceAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;

import static larc.ludiconprod.Activities.ActivitiesActivity.deleteCachedInfo;

public class BalanceActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    private final ArrayList<BalanceEntry> entryes = new ArrayList<>();
    private int pageNumber;
    private BalanceAdapter balanceAdapter;
    private boolean firstTimeBalance;
    private boolean addedSwipe;

    private void getBalance(int pageNumber) {
        String id = Persistance.getInstance().getUserInfo(this).id;

        HashMap<String, String> head = new HashMap<>();
        head.put("authKey", Persistance.getInstance().getUserInfo(this).authKey);
        String params = "userId=" + id;
        params += "&pageNumber=" + pageNumber;

        HTTPResponseController.getInstance().getBalance(head, params, this, this, this);
    }

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();

            super.setContentView(R.layout.balance_activity);

            View backButton = findViewById(R.id.backButton);
            TextView titleText = (TextView) findViewById(R.id.titleText);
            titleText.setText(Persistance.getInstance().getUserInfo(this).ludicoins + " Ludicoins");
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            this.balanceAdapter = new BalanceAdapter(this.entryes, this);

            this.entryes.addAll(Persistance.getInstance().getBalanceCache(this));
            this.updateBalanceList();
            getBalance(0);

            AssetManager assets = super.getAssets();
            Typeface typeFace = Typeface.createFromAsset(assets, "fonts/Quicksand-Medium.ttf");

            RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.tool_bar);
            TextView title = (TextView)toolbar.findViewById(R.id.titleText);
            title.setTypeface(typeFace);

            ((TextView) findViewById(R.id.activityLabel)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.dateLabel)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.ludicoinsLabel)).setTypeface(typeFace);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBalanceList() {
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.noInternetLayout);
        ll.getLayoutParams().height = 0;
        ll.setLayoutParams(ll.getLayoutParams());

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.balamceSwapRefresh);

        this.balanceAdapter.notifyDataSetChanged();
        final ListView listView = (ListView) findViewById(R.id.balamceList);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBalance);
        progressBar.setIndeterminate(true);
        progressBar.setAlpha(0f);

        if (!this.firstTimeBalance) {
            listView.setAdapter(this.balanceAdapter);
        }
        if(listView != null) {
            listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    if (v != null && listView.getChildCount() > 0) {
                        if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                            if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1 && listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {
                                progressBar.setAlpha(1f);
                                ++pageNumber;
                                getBalance(pageNumber);
                            }
                        }
                    }
                    return false;
                }
            });
        }
        if (!this.addedSwipe) {
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    entryes.clear();
                    getBalance(0);
                    mSwipeRefreshLayout.setRefreshing(false);
                    pageNumber = 0;
                }
            });
            this.addedSwipe = true;
        }
        progressBar.setAlpha(0f);
        this.firstTimeBalance = true;

        int last = listView.getLastVisiblePosition();
        int count = balanceAdapter.getCount();
        if (last + 1 < count) {
            listView.smoothScrollToPosition(last + 1);
        }
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        try {
            JSONArray balance = jsonObject.getJSONArray("balance");
            int size = balance.length();
            BalanceEntry be;
            for (int i = 0; i < size; ++i) {
                be = new BalanceEntry();
                JSONObject obj = balance.getJSONObject(i);
                be.id = Integer.parseInt(obj.getString("balanceId"));
                be.activity = obj.getString("activity");
                long timestamp = Long.parseLong(obj.getString("date"));
                timestamp *= 1000;
                be.date = new Date(timestamp);
                be.ludicoins = Integer.parseInt(obj.getString("ludicoins"));

                this.entryes.add(be);
            }
            Persistance.getInstance().setBalanceCache(this.entryes, this);
            this.updateBalanceList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onInternetRefresh() {
        entryes.clear();
        getBalance(0);
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.balamceSwapRefresh);
        mSwipeRefreshLayout.setRefreshing(false);
        pageNumber = 0;
    }

    public String trimMessage(String json, String key) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
            if(trimmedString.equalsIgnoreCase("Invalid Auth Key provided.")){
                deleteCachedInfo();
                Intent intent =new Intent(this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                this.startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(error.getMessage().contains("error")) {
            String json = trimMessage(error.getMessage(), "error");
            if (json != null){
                Toast.makeText(this, json, Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
        if (error instanceof NetworkError) {
            findViewById(R.id.internetRefresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setOnClickListener(null);
                    onInternetRefresh();
                }
            });
            RelativeLayout ll = (RelativeLayout) findViewById(R.id.noInternetLayout);
            final float scale = this.getResources().getDisplayMetrics().density;
            int pixels = (int) (56 * scale + 0.5f);
            ll.getLayoutParams().height = pixels;
            ll.setLayoutParams(ll.getLayoutParams());
        }
    }

    public static class BalanceEntry {
        public int id;
        public String activity;
        public Date date;
        public int ludicoins;
    }
}
