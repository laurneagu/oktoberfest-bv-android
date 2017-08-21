package larc.ludiconprod.Adapters.CouponsActivity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import larc.ludiconprod.Activities.CouponsActivity;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Coupon;

public class CouponsAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<Coupon> list = new ArrayList<>();
    private Context context;
    private Activity activity;
    private Resources resources;
    private CouponsActivity fragment;
    private final ListView listView;

    public static class ViewHolder {
        ImageView locationImage;
        TextView title;
        TextView location;
        TextView description;
        TextView validDate;
        TextView ludicoinsCode;
    }

    public CouponsAdapter(ArrayList<Coupon> list, Context context, Activity activity, Resources resources, CouponsActivity fragment) {
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
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (list.size() > 0) {
            final CouponsAdapter.ViewHolder holder;

            final Coupon currentCoupon = list.get(position);

            // Initialize the view
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.coupon_card, null);

                holder = new CouponsAdapter.ViewHolder();
                holder.locationImage = (ImageView) view.findViewById(R.id.locationImage);
                holder.title = (TextView) view.findViewById(R.id.title);
                holder.location = (TextView) view.findViewById(R.id.location);
                holder.description = (TextView) view.findViewById(R.id.description);
                holder.validDate = (TextView) view.findViewById(R.id.validDate);
                holder.ludicoinsCode = (TextView) view.findViewById(R.id.ludicoins);
                view.setTag(holder);
            } else {
                holder = (CouponsAdapter.ViewHolder) view.getTag();
            }

            /*holder.friends0.setVisibility(View.INVISIBLE);
            holder.friends1.setVisibility(View.INVISIBLE);
            holder.friends2.setVisibility(View.INVISIBLE);
            holder.friendsNumber.setVisibility(View.INVISIBLE);
            holder.profileImage.setImageResource(R.drawable.ph_user);
            holder.friends0.setImageResource(R.drawable.ph_user);
            holder.friends1.setImageResource(R.drawable.ph_user);
            holder.friends2.setImageResource(R.drawable.ph_user);*/

            view.setBackgroundColor(Color.parseColor("#FFFFFF"));

            final View currView = view;

            view.findViewById(R.id.couponGetIt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currView.setBackgroundColor(Color.parseColor("#f5f5f5"));

                    HashMap<String, String> params = new HashMap<String, String>();
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("apiKey", HTTPResponseController.API_KEY);

                    params.put("userId", Persistance.getInstance().getUserInfo(activity).id);
                    params.put("couponBlockId", currentCoupon.couponBlockId);
                    HTTPResponseController.getInstance().redeemCoupon(params, headers, fragment);
                }
            });

            holder.title.setText(currentCoupon.title);
            if (!currentCoupon.companyPicture.equals("")) {
                Bitmap bitmap = MyAdapter.decodeBase64(currentCoupon.companyPicture);
                holder.locationImage.setImageBitmap(bitmap);
            }

            holder.location.setText(currentCoupon.companyName);
            holder.title.setText(currentCoupon.title);
            holder.ludicoinsCode.setText(currentCoupon.ludicoins + " ");
            holder.description.setText(currentCoupon.description);

            Date date = new Date(currentCoupon.expiryDate * 1000);
            SimpleDateFormat fmt = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
            holder.validDate.setText("Valid till " + fmt.format(date));
        }

        return view;
    }
}
