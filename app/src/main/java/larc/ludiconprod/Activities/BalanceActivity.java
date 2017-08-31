package larc.ludiconprod.Activities;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;

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

public class BalanceActivity extends AppCompatActivity implements Response.Listener<JSONObject> {

    private ArrayList<BalanceEntry> entryes = new ArrayList<>();
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

        HTTPResponseController.getInstance().getBalance(head, params, this);
    }

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();

            super.setContentView(R.layout.balance_activity);

            ImageButton backButton=(ImageButton) findViewById(R.id.backButton);
            backButton.setBackgroundResource(R.drawable.ic_nav_up);
            TextView titleText = (TextView) findViewById(R.id.titleText);
            titleText.setText(Persistance.getInstance().getUserInfo(this).ludicoins + " Ludicoins");
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            this.balanceAdapter = new BalanceAdapter(this.entryes, this);

            getBalance(0);

            AssetManager assets = super.getAssets();
            Typeface typeFace = Typeface.createFromAsset(assets, "fonts/Quicksand-Medium.ttf");

            ((TextView) findViewById(R.id.activityLabel)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.dateLabel)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.ludicoinsLabel)).setTypeface(typeFace);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBalanceList() {
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
            this.updateBalanceList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class BalanceEntry {
        public int id;
        public String activity;
        public Date date;
        public int ludicoins;
    }
}
