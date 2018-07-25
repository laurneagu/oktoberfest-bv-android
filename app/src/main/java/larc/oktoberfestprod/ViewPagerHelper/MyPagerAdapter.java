package larc.oktoberfestprod.ViewPagerHelper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import larc.oktoberfestprod.Activities.GMapsActivity;
import larc.oktoberfestprod.R;
import larc.oktoberfestprod.Utils.util.AuthorizedLocation;

public class MyPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.PageTransformer {
    public final static float BIG_SCALE = 1f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    private MyLinearLayout cur = null;
    private MyLinearLayout next = null;
    private GMapsActivity context;
    private FragmentManager fm;
    private float scale;
    private ArrayList<AuthorizedLocation> authorizedLocation=new ArrayList<AuthorizedLocation>();

    public MyPagerAdapter(GMapsActivity context, FragmentManager fm, ArrayList<AuthorizedLocation> authorizedLocations) {
        super(fm);
        this.fm = fm;
        this.context = context;
        for(int i=0;i<authorizedLocations.size();i++){
            authorizedLocation.add(authorizedLocations.get(i));
        }
    }

    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        if (position == GMapsActivity.FIRST_PAGE) {
            scale = BIG_SCALE;

        }
        else
            scale = SMALL_SCALE;

        position = position % GMapsActivity.PAGES;
        return MyFragment.newInstance(context, position, scale, authorizedLocation.get(position));


    }
    @Override
    public int getItemPosition(Object object) {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return GMapsActivity.PAGES * GMapsActivity.LOOPS;
    }

    @Override
    public void transformPage(View page, float position) {
        MyLinearLayout myLinearLayout = (MyLinearLayout) page.findViewById(R.id.root);
        float scale = BIG_SCALE;
        if (position > 0) {
            scale = scale - position * DIFF_SCALE;
        } else {
            scale = scale + position * DIFF_SCALE;
        }
        if (scale < 0) scale = 0;
        myLinearLayout.setScaleBoth(scale);
    }
}