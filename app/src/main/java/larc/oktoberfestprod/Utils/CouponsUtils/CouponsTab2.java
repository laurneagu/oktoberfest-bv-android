package larc.oktoberfestprod.Utils.CouponsUtils;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import larc.oktoberfestprod.R;

/**
 * Created by alex_ on 18.08.2017.
 */

public class CouponsTab2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.couponstab2, container, false);

        Typeface typeFace= Typeface.createFromAsset(super.getActivity().getAssets(), "fonts/Quicksand-Medium.ttf");

        TextView tv = (TextView) v.findViewById(R.id.noMyCoupons);
        tv.setTypeface(typeFace);
        tv = (TextView) v.findViewById(R.id.noMyCouponsText);
        tv.setTypeface(typeFace);
        ((Button) v.findViewById(R.id.noMyCouponsButton)).setTypeface(typeFace);

        return v;
    }
}
