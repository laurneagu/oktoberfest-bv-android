package larc.ludiconprod.Utils.MyProfileUtils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by alex_ on 10.08.2017.
 */

public class EditViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when EditViewPagerAdapter is created
    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the EditViewPagerAdapter is created
    private EditProfileTab1 editProfileTab1;
    private EditProfileTab2 editProfileTab2;
    private boolean t1=false;
    private boolean t2 = false;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public EditViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            if (!t1){
                editProfileTab1 = new EditProfileTab1();
                t1 = true;
            }
            return editProfileTab1;
        }
        else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            if(!t2) {
                editProfileTab2 = new EditProfileTab2();
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
