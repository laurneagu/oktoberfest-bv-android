package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

import larc.ludiconprod.Adapters.EditProfile.EditActivitiesAdapter;
import larc.ludiconprod.Adapters.EditProfile.EditInfoAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.MyProfileUtils.EditViewPagerAdapter;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;

/**
 * Created by alex_ on 10.08.2017.
 */

public class EditProfileActivity extends AppCompatActivity {
    private static final CharSequence TITLES[] = {"MY ACTIVITIES", "INFO DETAILS"};
    private int tabsNumber = 2;
    private Context mContext;
    private View v;
    private EditViewPagerAdapter adapter;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    private ArrayList<Event> activities = new  ArrayList<>();
    private EditInfoAdapter infoAdapter;
    private EditActivitiesAdapter myAdapter;
    private ArrayList<Event> myEventList;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.edit_profile_activity);

        try {


            this.adapter = new EditViewPagerAdapter(getSupportFragmentManager(), EditProfileActivity.TITLES, tabsNumber);

            pager = (ViewPager) findViewById(R.id.editPager);
            pager.setAdapter(adapter);

            // Assiging the Sliding Tab Layout View
            tabs = (SlidingTabLayout) findViewById(R.id.editTabs);
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

            myAdapter = new EditActivitiesAdapter(myEventList, this.getApplicationContext(), this, getResources(), this);


            //infoAdapter = new EditInfoAdapter(getActivity().getApplicationContext(), getActivity(), getResources(), this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
