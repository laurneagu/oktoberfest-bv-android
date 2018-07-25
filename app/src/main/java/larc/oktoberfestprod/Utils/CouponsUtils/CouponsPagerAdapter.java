package larc.oktoberfestprod.Utils.CouponsUtils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import larc.oktoberfestprod.Activities.CouponsActivity;
import larc.oktoberfestprod.Utils.MyProfileUtils.EditProfileTab1;
import larc.oktoberfestprod.Utils.MyProfileUtils.EditProfileTab2;

public class CouponsPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when EditViewPagerAdapter is created
    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the EditViewPagerAdapter is created
    private CouponsTab1 editProfileTab1;
    private CouponsTab2 editProfileTab2;
    private boolean t1 = false;
    private boolean t2 = false;
    private CouponsActivity fragment;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public CouponsPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb, CouponsActivity fragment) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.fragment = fragment;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) { // if the position is 0 we are returning the First tab
            if (!this.t1) {
                editProfileTab1 = new CouponsTab1();
                editProfileTab1.fragment = this.fragment;
                t1 = true;
            }
            return editProfileTab1;
        } else {
            if(!this.t2) {
                editProfileTab2 = new CouponsTab2();
                //editProfileTab2.fragment = this.fragment;
                t2 = true;
            }
            return editProfileTab2;
        }
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
