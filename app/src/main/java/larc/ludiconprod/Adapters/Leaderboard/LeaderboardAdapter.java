package larc.ludiconprod.Adapters.Leaderboard;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import larc.ludiconprod.Activities.CouponsActivity;
import larc.ludiconprod.Activities.LeaderboardActivity;
import larc.ludiconprod.Adapters.CouponsActivity.CouponsAdapter;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Coupon;
import larc.ludiconprod.Utils.UserPosition;

/**
 * Created by alex_ on 23.08.2017.
 */

public class LeaderboardAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<UserPosition> list = new ArrayList<>();
    private LeaderboardActivity fragment;
    private final ListView listView;

    public LeaderboardAdapter(ArrayList<UserPosition> list, View v, LeaderboardActivity fragment) {
        this.list = list;
        this.fragment = fragment;

        this.listView = (ListView) v.findViewById(R.id.leaderList);
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
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.fragment.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.coupon_card, null);
        } else {
        }

        return view;
    }
}
