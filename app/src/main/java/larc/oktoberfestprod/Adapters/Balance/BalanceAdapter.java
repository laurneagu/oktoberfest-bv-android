package larc.oktoberfestprod.Adapters.Balance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import larc.oktoberfestprod.Activities.BalanceActivity;
import larc.oktoberfestprod.Activities.LeaderboardActivity;
import larc.oktoberfestprod.Activities.UserProfileActivity;
import larc.oktoberfestprod.Adapters.Leaderboard.LeaderboardAdapter;
import larc.oktoberfestprod.Adapters.MainActivity.MyAdapter;
import larc.oktoberfestprod.Controller.Persistance;
import larc.oktoberfestprod.R;
import larc.oktoberfestprod.Utils.UserPosition;

/**
 * Created by alex_ on 30.08.2017.
 */

public class BalanceAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<BalanceActivity.BalanceEntry> list = new ArrayList<>();
    private Activity activity;
    private final ListView listView;

    public BalanceAdapter(ArrayList<BalanceActivity.BalanceEntry> list, Activity activity) {
        this.list = list;
        this.activity = activity;

        this.listView = (ListView) activity.findViewById(R.id.balamceList);
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
        final BalanceActivity.BalanceEntry current = list.get(position);

        BalanceAdapter.ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.balance_card, null);

            holder = new BalanceAdapter.ViewHolder();
            holder.activity = (TextView) view.findViewById(R.id.activity);
            holder.date = (TextView) view.findViewById(R.id.date);
            holder.ludicoins = (TextView) view.findViewById(R.id.ludicoins);

            AssetManager assets = activity.getAssets();
            Typeface typeFace = Typeface.createFromAsset(assets, "fonts/Quicksand-Medium.ttf");

            holder.activity.setTypeface(typeFace);
            holder.date.setTypeface(typeFace);
            holder.ludicoins.setTypeface(typeFace);

            view.setTag(holder);
        } else {
            holder = (BalanceAdapter.ViewHolder) view.getTag();
        }

        holder.activity.setText("" + current.activity);
        holder.ludicoins.setText("" + current.ludicoins + " ");

        if (current.ludicoins < 0) {
            holder.activity.setTextColor(0xffff0000);
        }
        else{
            holder.activity.setTextColor(0xff009150);
        }

        String date = new SimpleDateFormat("dd MMM yyyy").format(current.date);
        holder.date.setText(date);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }

    public class ViewHolder {
        TextView activity;
        TextView date;
        TextView ludicoins;
    }
}
