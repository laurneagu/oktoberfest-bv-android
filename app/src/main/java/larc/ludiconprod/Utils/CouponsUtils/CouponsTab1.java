package larc.ludiconprod.Utils.CouponsUtils;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import larc.ludiconprod.Activities.CouponsActivity;
import larc.ludiconprod.Activities.Main;
import larc.ludiconprod.Adapters.CouponsActivity.CouponsAdapter;
import larc.ludiconprod.Adapters.MainActivity.AroundMeAdapter;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Coupon;
import larc.ludiconprod.Utils.Event;

/**
 * Created by alex_ on 18.08.2017.
 */

public class CouponsTab1 extends Fragment {
    public ArrayList<Coupon> coupons = new ArrayList<>();
    public CouponsAdapter fradapter;
    public CouponsActivity fragment;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.couponstab1, container, false);

        try {
            coupons = Persistance.getInstance().getCouponsCache(super.getActivity());
            fradapter = new CouponsAdapter(coupons, getActivity().getApplicationContext(), getActivity(), getResources(), fragment);
            updateCoupons();

            Typeface typeFace= Typeface.createFromAsset(super.getActivity().getAssets(), "fonts/Quicksand-Medium.ttf");

            TextView tv = (TextView) v.findViewById(R.id.noCoupons);
            tv.setTypeface(typeFace);
            tv = (TextView) v.findViewById(R.id.noCouponsText);
            tv.setTypeface(typeFace);
            ((Button) v.findViewById(R.id.noCouponsButton)).setTypeface(typeFace);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    private void updateCoupons() {
        fradapter.notifyDataSetChanged();

        final ListView listView = (ListView) v.findViewById(R.id.couponsList);
        ImageView heartImage = (ImageView) v.findViewById(R.id.heartImageCoupons);
        TextView noCoupons = (TextView) v.findViewById(R.id.noCoupons);
        TextView discoverActivities = (TextView) v.findViewById(R.id.noCouponsText);
        Button noMyCouponsButton = (Button) v.findViewById(R.id.noCouponsButton);

        listView.setAdapter(fradapter);

        heartImage.setVisibility(View.INVISIBLE);
        noCoupons.setVisibility(View.INVISIBLE);
        discoverActivities.setVisibility(View.INVISIBLE);
        noMyCouponsButton.setEnabled(false);
    }
}
