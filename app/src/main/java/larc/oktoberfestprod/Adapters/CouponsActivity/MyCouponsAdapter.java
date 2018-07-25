package larc.oktoberfestprod.Adapters.CouponsActivity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import larc.oktoberfestprod.Activities.CouponsActivity;
import larc.oktoberfestprod.Adapters.MainActivity.MyAdapter;
import larc.oktoberfestprod.Controller.HTTPResponseController;
import larc.oktoberfestprod.Controller.Persistance;
import larc.oktoberfestprod.R;
import larc.oktoberfestprod.Utils.Coupon;

/**
 * Created by alex_ on 18.08.2017.
 */

public class MyCouponsAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<Coupon> list = new ArrayList<>();
    private Context context;
    private Activity activity;
    private Resources resources;
    private CouponsActivity fragment;
    private final ListView listView;

    public MyCouponsAdapter(ArrayList<Coupon> list, Context context, Activity activity, Resources resources, CouponsActivity fragment) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.resources = resources;
        this.fragment = fragment;

        this.listView = (ListView) activity.findViewById(R.id.couponsList);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (list.size() > 0) {
            final CouponsAdapter.ViewHolder holder;

            final Coupon currentCoupon = list.get(i);

            // Initialize the view
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.my_coupon_card, null);

                Typeface typeFace= Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Medium.ttf");
                Typeface typeFaceBold= Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Bold.ttf");

                holder = new CouponsAdapter.ViewHolder();
                holder.locationImage = (ImageView) view.findViewById(R.id.locationImage);
                holder.title = (TextView) view.findViewById(R.id.title);
                holder.title.setTypeface(typeFace);
                holder.location = (TextView) view.findViewById(R.id.location);
                holder.location.setTypeface(typeFace);
                holder.description = (TextView) view.findViewById(R.id.description);
                holder.description.setTypeface(typeFace);
                holder.validDate = (TextView) view.findViewById(R.id.validDate);
                holder.validDate.setTypeface(typeFace);
                holder.ludicoinsCode = (TextView) view.findViewById(R.id.discountCode);
                holder.ludicoinsCode.setTypeface(typeFaceBold);

                view.setTag(holder);
            } else {
                holder = (CouponsAdapter.ViewHolder) view.getTag();
            }

            view.setBackgroundColor(Color.parseColor("#FFFFFF"));

            holder.title.setText(currentCoupon.title);
            if (!currentCoupon.companyPicture.equals("")) {
                Bitmap bitmap = MyAdapter.decodeBase64(currentCoupon.companyPicture);
                holder.locationImage.setImageBitmap(bitmap);
            }

            holder.location.setText(currentCoupon.companyName);
            holder.title.setText(currentCoupon.title);
            holder.ludicoinsCode.setText("Discount code: " + currentCoupon.discountCode);
            holder.description.setText(currentCoupon.description);

            Date date = new Date(currentCoupon.expiryDate * 1000);
            SimpleDateFormat fmt = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
            holder.validDate.setText("Valid till " + fmt.format(date));
        }

        return view;
    }
}
