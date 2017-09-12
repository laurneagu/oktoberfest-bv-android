package larc.ludiconprod.Activities;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Set;
import java.util.TreeMap;

import larc.ludiconprod.R;
import larc.ludiconprod.Utils.LeaderboardUtils.LeaderboardPagerAdapter;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;

import static android.R.color.transparent;

public class LeaderboardActivity extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private TreeMap<Integer, String> codes = new TreeMap<>();
    private TreeMap<Integer, Integer> compoundDrawables = new TreeMap<>();
    private int tabsNumber = 3;
    private static final String[] TITLES = {"THIS MONTH", "3 MONTHS", "ALL TIME"};

    View v;
    Context mContext;
    DrawerLayout mDrawer;
    View dRight;
    LeaderboardPagerAdapter adapter;
    ViewPager pager;
    String selectedSportCode;
    boolean friends;

    public String getSelectedSportCode() {
        return selectedSportCode;
    }

    public boolean isFriends() {
        return friends;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = inflater.getContext();
        v = inflater.inflate(R.layout.leaderboard_activity, container, false);

        try {
            super.onCreate(savedInstanceState);

            this.adapter = new LeaderboardPagerAdapter(this.getFragmentManager(), this, LeaderboardActivity.TITLES, this.tabsNumber);

            pager = (ViewPager) v.findViewById(R.id.couponsPager);
            pager.setAdapter(adapter);
            pager.setOffscreenPageLimit(3);

            SlidingTabLayout tabs = (SlidingTabLayout) v.findViewById(R.id.couponsTabs);
            tabs.setDistributeEvenly(false);

            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            tabs.setViewPager(pager);

            mDrawer = (DrawerLayout) v.findViewById(R.id.drawer);
            dRight = v.findViewById(R.id.rightFilter);

            v.findViewById(R.id.filterShow).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawer.openDrawer(dRight);
                }
            });

            RadioGroup filterSwich = (RadioGroup) v.findViewById(R.id.filterSwich);
            final RadioButton general =  (RadioButton) v.findViewById(R.id.general);
            final RadioButton friends =  (RadioButton) v.findViewById(R.id.friends);

            general.setBackgroundResource(R.drawable.toggle_option_first);
            general.setTextColor(Color.parseColor("#ffffff"));
            filterSwich.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    if(general.isChecked()){
                        general.setBackgroundResource(R.drawable.toggle_option_first);
                        general.setTextColor(Color.parseColor("#ffffff"));
                        friends.setBackgroundResource(transparent);
                        friends.setTextColor(Color.parseColor("#1A0c3855"));
                    }
                    else{
                        friends.setBackgroundResource(R.drawable.toggle_option_second);
                        friends.setTextColor(Color.parseColor("#ffffff"));
                        general.setBackgroundResource(transparent);
                        general.setTextColor(Color.parseColor("#1A0c3855"));
                    }
                }
            });

            codes.put(R.id.filterFootball, "FOT");
            codes.put(R.id.filterBasketball, "BAS");
            codes.put(R.id.filterVolleyball, "VOL");
            codes.put(R.id.filterJogging, "JOG");
            codes.put(R.id.filterGym, "GYM");
            codes.put(R.id.filterCycling, "CYC");
            codes.put(R.id.filterTennis, "TEN");
            codes.put(R.id.filterPingPong, "PIN");
            codes.put(R.id.filterSquash, "SQU");
            codes.put(R.id.filterOthers, "OTH");

            TreeMap<Integer, Integer> cd = this.compoundDrawables;
            cd.put(R.id.filterFootball, R.drawable.ic_sport_football);
            cd.put(R.id.filterBasketball, R.drawable.ic_sport_basketball);
            cd.put(R.id.filterVolleyball, R.drawable.ic_sport_voleyball);
            cd.put(R.id.filterJogging, R.drawable.ic_sport_jogging);
            cd.put(R.id.filterGym, R.drawable.ic_sport_gym);
            cd.put(R.id.filterCycling, R.drawable.ic_sport_cycling);
            cd.put(R.id.filterTennis, R.drawable.ic_sport_tennis);
            cd.put(R.id.filterPingPong, R.drawable.ic_sport_pingpong);
            cd.put(R.id.filterSquash, R.drawable.ic_sport_squash);
            cd.put(R.id.filterOthers, R.drawable.ic_sport_others);

            final RadioGroup sports = (RadioGroup) v.findViewById(R.id.sports);
            sports.setOnCheckedChangeListener(this);

            AssetManager assets = inflater.getContext().getAssets();
            Typeface typeFace = Typeface.createFromAsset(assets,"fonts/Quicksand-Medium.ttf");

            general.setTypeface(typeFace);
            friends.setTypeface(typeFace);

            Set<Integer> codesKeys = this.codes.keySet();
            for (Integer id : codesKeys) {
                RadioButton rb = (RadioButton) sports.findViewById(id);
                rb.setTypeface(typeFace);
                rb.setOnClickListener(this);
            }

            this.deselectAll(sports);

            v.findViewById(R.id.clearFilters).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedSportCode = null;
                    deselectAll(sports);
                }
            });
            v.findViewById(R.id.filterApply).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LeaderboardActivity.this.friends = friends.isChecked();
                    adapter.reload();
                    mDrawer.closeDrawer(dRight);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    private void deselectAll(RadioGroup group) {
        Set<Integer> codesKeys = this.codes.keySet();

        for (Integer id : codesKeys) {
            RadioButton rb = (RadioButton) group.findViewById(id);
            Integer compoundDrawable = this.compoundDrawables.get(id);
            rb.setAlpha(0.4f);
            rb.setSelected(false);
            rb.setCompoundDrawablesWithIntrinsicBounds(compoundDrawable, 0, 0, 0);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        this.deselectAll(group);
    }

    @Override
    public void onClick(View view) {
        RadioButton rb = (RadioButton) view;
        String code = this.codes.get(view.getId());
        Integer compoundDrawable = this.compoundDrawables.get(view.getId());
        if(rb.isSelected()) {
            if (code.equals(this.selectedSportCode)) {
                this.selectedSportCode = null;
            }
            rb.setSelected(false);
            rb.setAlpha(0.4f);
            rb.setCompoundDrawablesWithIntrinsicBounds(compoundDrawable,0,0,0);
        } else {
            this.selectedSportCode = code;
            rb.setAlpha(1f);
            rb.setSelected(true);
            rb.setCompoundDrawablesWithIntrinsicBounds(compoundDrawable,0,R.drawable.ic_check,0);
        }
    }

    public void resetInternetRefresh() {
        ImageView ir = (ImageView) v.findViewById(R.id.internetRefresh);
        ir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setOnClickListener(null);
                adapter.reload();
            }
        });
    }
}