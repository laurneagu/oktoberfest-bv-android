package larc.oktoberfestprod.Utils.LeaderboardUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import larc.oktoberfestprod.Activities.LeaderboardActivity;
import larc.oktoberfestprod.Utils.MyProfileUtils.EditProfileTab1;

/**
 * Created by alex_ on 23.08.2017.
 */

public class LeaderboardPagerAdapter extends FragmentStatePagerAdapter {
    private CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when EditViewPagerAdapter is created
    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the EditViewPagerAdapter is created
    private LeaderboardTab tab1;
    private LeaderboardTab tab2;
    private LeaderboardTab tab3;
    private LeaderboardActivity activity;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public LeaderboardPagerAdapter(FragmentManager fm, LeaderboardActivity activity, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.activity = activity;
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                if (tab1 == null){
                    tab1 = new LeaderboardTab();
                    tab1.setLeaderboardActivity(this.activity);
                    args.putInt(LeaderboardTab.TIMEFRAME_KEY, LeaderboardTab.THIS_MONTH);
                    tab1.setArguments(args);
                }
                return tab1;
            case 1:
                if (tab2 == null){
                    tab2 = new LeaderboardTab();
                    tab2.setLeaderboardActivity(this.activity);
                    args.putInt(LeaderboardTab.TIMEFRAME_KEY, LeaderboardTab.MONTHS_3);
                    tab2.setArguments(args);
                }
                return tab2;
            case 2:
                if (tab3 == null){
                    tab3 = new LeaderboardTab();
                    tab3.setLeaderboardActivity(this.activity);
                    args.putInt(LeaderboardTab.TIMEFRAME_KEY, LeaderboardTab.ALL_TIME);
                    tab3.setArguments(args);
                }
                return tab3;
        }

        return null;
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }

    public void reload() {
        if (tab1 != null && tab1.getActivity() != null) {
            this.tab1.reload();
        }
        if (tab2 != null && tab2.getActivity() != null) {
            this.tab2.reload();
        }
        if (tab3 != null && tab3.getActivity() != null) {
            this.tab3.reload();
        }
    }
}
