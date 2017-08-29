package larc.ludiconprod.Adapters.Leaderboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import larc.ludiconprod.Activities.ActivityDetailsActivity;
import larc.ludiconprod.Activities.CouponsActivity;
import larc.ludiconprod.Activities.LeaderboardActivity;
import larc.ludiconprod.Activities.UserProfileActivity;
import larc.ludiconprod.Adapters.CouponsActivity.CouponsAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.Controller.Persistance;
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

    public LeaderboardActivity getFragment() {
        return fragment;
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

        final UserPosition currentPosition = list.get(position);

        LeaderboardAdapter.ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.fragment.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.leaderboard_card, null);

            holder = new LeaderboardAdapter.ViewHolder();
            holder.position = (TextView) view.findViewById(R.id.position);
            holder.image = (ImageView) view.findViewById(R.id.image);
            holder.level = (TextView) view.findViewById(R.id.level);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.points = (TextView) view.findViewById(R.id.points);
            view.setTag(holder);
        } else {
            holder = (LeaderboardAdapter.ViewHolder) view.getTag();
        }

        holder.position.setText("" + currentPosition.rank);
        if (!currentPosition.profileImage.equals("")) {
            Bitmap bitmap = MyAdapter.decodeBase64(currentPosition.profileImage);
            holder.image.setImageBitmap(bitmap);
        } else {
            holder.image.setImageResource(R.drawable.ph_user);
        }
        holder.level.setText("" + currentPosition.level);
        holder.name.setText(currentPosition.name);
        holder.points.setText("" + currentPosition.points);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = currentPosition.userId;
                Activity ac = fragment.getActivity();
                if (Persistance.getInstance().getUserInfo(ac).id.equals(id)) {
                    Toast.makeText(ac, "It's you :)", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(fragment.getActivity(), UserProfileActivity.class);
                intent.putExtra("UserId", currentPosition.userId);
                fragment.getActivity().startActivity(intent);
            }
        });

        return view;
    }

    public class ViewHolder {
        TextView position;
        ImageView image;
        TextView level;
        TextView name;
        TextView points;
    }
}
