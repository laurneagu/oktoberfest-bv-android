package larc.oktoberfestprod.Utils.MainPageUtils;

/**
 * Created by Andrei on 5/21/2016.
 */

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import larc.oktoberfestprod.R;

public class Tab2 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab2, container, false);

        Typeface typeFace= Typeface.createFromAsset(super.getActivity().getAssets(), "fonts/Quicksand-Medium.ttf");

        TextView tv = (TextView) v.findViewById(R.id.noActivitiesTextFieldMyActivity);
        tv.setTypeface(typeFace);
        tv = (TextView) v.findViewById(R.id.pressPlusButtonTextFieldMyActivity);
        tv.setTypeface(typeFace);

        return v;
    }
}
