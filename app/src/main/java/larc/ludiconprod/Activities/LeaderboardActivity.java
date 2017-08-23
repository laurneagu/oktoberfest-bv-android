package larc.ludiconprod.Activities;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import larc.ludiconprod.R;

import static android.R.color.transparent;

public class LeaderboardActivity extends Fragment {

    View v;
    Context mContext;
    DrawerLayout mDrawer;
    View dLeft, dRight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = inflater.getContext();
        v = inflater.inflate(R.layout.leaderboard_activity, container, false);

        try {
            super.onCreate(savedInstanceState);

            mDrawer = (DrawerLayout) v.findViewById(R.id.drawer);
            dRight = v.findViewById(R.id.right);

            /*v.findViewById(R.id.filterButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawer.openDrawer(dRight);
                }
            });*/

            RadioGroup filterSwich = (RadioGroup) v.findViewById(R.id.filterSwich);
            final RadioButton general =  (RadioButton) v.findViewById(R.id.general);
            final RadioButton friends =  (RadioButton) v.findViewById(R.id.friends);

            general.setBackgroundResource(R.drawable.toggle_male);
            general.setTextColor(Color.parseColor("#ffffff"));
            filterSwich.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    if(general.isChecked()){
                        general.setBackgroundResource(R.drawable.toggle_male);
                        general.setTextColor(Color.parseColor("#ffffff"));
                        friends.setBackgroundResource(transparent);
                        friends.setTextColor(Color.parseColor("#1A0c3855"));
                    }
                    else{
                        friends.setBackgroundResource(R.drawable.toggle_female);
                        friends.setTextColor(Color.parseColor("#ffffff"));
                        general.setBackgroundResource(transparent);
                        general.setTextColor(Color.parseColor("#1A0c3855"));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }
}