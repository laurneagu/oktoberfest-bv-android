package larc.ludiconprod.Utils.LeaderboardUtils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import larc.ludiconprod.Activities.LeaderboardActivity;
import larc.ludiconprod.Adapters.Leaderboard.LeaderboardAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.UserPosition;

/**
 * Created by alex_ on 23.08.2017.
 */

public class LeaderboardTab extends Fragment implements Response.Listener<JSONObject> {

    private ArrayList<UserPosition> users = new ArrayList<>();
    private LeaderboardAdapter leaderboardAdapter;
    private int pageNumber;
    private View v;
    private String userId;

    public static final String TIMEFRAME_KEY = "TIMEFRAME";
    public static final int THIS_MONTH = 0;
    public static final int MONTHS_3 = 1;
    public static final int ALL_TIME = 2;
    private int timeFrame;
    private boolean addedSwipeleaderboards;
    private LeaderboardActivity activity;

    @Override
    public void setArguments(Bundle args) {
        this.timeFrame = args.getInt(LeaderboardTab.TIMEFRAME_KEY);
        super.setArguments(args);
    }

    public void setLeaderboardActivity(LeaderboardActivity activity) {
        this.activity = activity;
    }

    public void getLeaderboards(String page) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(getActivity()).authKey);
        String urlParams = "";

        urlParams += "userId=" + Persistance.getInstance().getUserInfo(getActivity()).id;
        urlParams += "&pageNumber=" + page;

        String sportCode = this.activity.getSelectedSportCode();
        if (sportCode == null) {
            sportCode = "GEN";
        }
        urlParams += "&sportName=" + sportCode;
        urlParams += "&timeframe=" + this.timeFrame;
        urlParams += "&area=" + (this.activity.isFriends() ? "0" : "1");

        HTTPResponseController.getInstance().getLeaderboard(urlParams, headers, this);
        Log.d("urlParams", urlParams);
    }

    public void updateUserList() {
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.leaderSwapRefresh);

        this.leaderboardAdapter.notifyDataSetChanged();

        final ListView listView = (ListView) v.findViewById(R.id.leaderList);
        final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progressCoupons) ;
        progressBar.setIndeterminate(true);
        progressBar.setAlpha(0f);

        if (listView.getAdapter() == null) {
            listView.setAdapter(this.leaderboardAdapter);
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
                                getLeaderboards(String.valueOf(pageNumber));
                            }
                        }
                    }
                    return false;
                }
            });
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                    checkYourScroll(listView);
                }
            });
        }
        if (!this.addedSwipeleaderboards) {
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getLeaderboards("0");
                    mSwipeRefreshLayout.setRefreshing(false);
                    pageNumber = 0;
                }
            });
            this.addedSwipeleaderboards = true;
        }
        progressBar.setAlpha(0f);

        this.checkYourScroll(listView);
    }

    private void updateYourCard(View card, UserPosition up) {
        TextView position = (TextView) card.findViewById(R.id.position);
        ImageView image = (ImageView) card.findViewById(R.id.image);
        TextView level = (TextView) card.findViewById(R.id.level);
        TextView name = (TextView) card.findViewById(R.id.name);
        TextView points = (TextView) card.findViewById(R.id.points);

        position.setText("" + up.rank);
        if (!up.profileImage.equals("")) {
            Bitmap bitmap = MyAdapter.decodeBase64(up.profileImage);
            image.setImageBitmap(bitmap);
        } else {
            image.setImageResource(R.drawable.ph_user);
        }
        level.setText("" + up.level);
        name.setText(up.name);
        points.setText("" + up.points);

        card.findViewById(R.id.leaderCard).setBackgroundColor(Color.WHITE);
    }

    private void checkYourScroll(ListView listView) {
        int first = listView.getFirstVisiblePosition();
        int last = listView.getLastVisiblePosition();
        LeaderboardAdapter la = (LeaderboardAdapter) listView.getAdapter();
        int count = la.getCount();
        View you = v.findViewById(R.id.youRank);
        if (count == 0) {
            you.setVisibility(View.INVISIBLE);
            return;
        }

        String id = this.userId;
        int th = count;

        for (int i = 0; i < count; ++i) {
            UserPosition up = (UserPosition) la.getItem(i);
            if (up.userId.equals(id)) {
                th = i;
                break;
            }
        }

        Log.d("You", first + " < " + th + " > " + last);

        if (th < first) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) you.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            you.setLayoutParams(params);
            you.setVisibility(View.VISIBLE);
        } else if (th > last) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) you.getLayoutParams();
            params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);;
            you.setLayoutParams(params);
            you.setVisibility(View.VISIBLE);
        } else {
            you.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.d("Leaderboard", "" + response);
        try {
            if (pageNumber == 0) {
                this.users.clear();
            }

            JSONArray users = response.getJSONArray("users");

            UserPosition user;
            int length = users.length();
            for (int i = 0; i < length; ++i) {
                JSONObject obj = users.getJSONObject(i);
                user = new UserPosition();
                user.userId = obj.getString("userId");
                user.name = obj.getString("name");
                user.points = Integer.parseInt(obj.getString("points"));
                user.level = Integer.parseInt(obj.getString("level"));
                user.profileImage = obj.getString("profileImage");
                user.rank = Integer.parseInt(obj.getString("rank"));
                this.users.add(user);
            }

            try {
                JSONObject you = response.getJSONObject("you");
                user = new UserPosition();
                user.userId = you.getString("userId");
                user.name = you.getString("name");
                user.points = Integer.parseInt(you.getString("points"));
                user.level = Integer.parseInt(you.getString("level"));
                user.profileImage = you.getString("profileImage");
                user.rank = Integer.parseInt(you.getString("rank"));

                this.updateYourCard(v.findViewById(R.id.youRank), user);
            } catch (JSONException e) {
            }


            this.updateUserList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.leaderboard_tab, container, false);

        try {
            this.userId = Persistance.getInstance().getUserInfo(super.getActivity()).id;
            this.users.clear();
            this.v = v;
            this.leaderboardAdapter = new LeaderboardAdapter(this.users, v, this.activity);

            this.addedSwipeleaderboards = false;
            pageNumber = 0;

            getLeaderboards("0");

            AssetManager assets = inflater.getContext().getAssets();
            Typeface typeFace= Typeface.createFromAsset(assets, "fonts/Quicksand-Medium.ttf");

            View card = v.findViewById(R.id.youRank);
            TextView position = (TextView) card.findViewById(R.id.position);
            position.setTypeface(typeFace);
            TextView level = (TextView) card.findViewById(R.id.level);
            level.setTypeface(typeFace);
            TextView name = (TextView) card.findViewById(R.id.name);
            name.setTypeface(typeFace);
            TextView points = (TextView) card.findViewById(R.id.points);
            points.setTypeface(typeFace);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    public void reload() {
        users.clear();
        getLeaderboards("0");
        SwipeRefreshLayout sl = (SwipeRefreshLayout) v.findViewById(R.id.leaderSwapRefresh);
        sl.setRefreshing(false);
        pageNumber = 0;
    }
}
