package larc.ludiconprod.Utils.LeaderboardUtils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import larc.ludiconprod.Utils.MyProfileUtils.EditProfileTab1;

/**
 * Created by alex_ on 23.08.2017.
 */

public class LeaderboardPagerAdapter extends FragmentStatePagerAdapter {
    private CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when EditViewPagerAdapter is created
    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the EditViewPagerAdapter is created
    private LeaderboardTab tab1;
    private LeaderboardTab tab2;
    private LeaderboardTab tab3;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public LeaderboardPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (tab1 == null){
                    tab1 = new LeaderboardTab();
                }
                return tab1;
            case 1:
                if (tab2 == null){
                    tab2 = new LeaderboardTab();
                }
                return tab2;
            case 2:
                if (tab3 == null){
                    tab3 = new LeaderboardTab();
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
}
