package larc.ludicon.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import larc.ludicon.UserInfo.ActivityInfo;
import larc.ludicon.UserInfo.User;

import java.util.ArrayList;

import larc.ludicon.R;
import larc.ludicon.Utils.UserInfo;

/**
 * Created by LaurUser on 5/14/2016.
 */
public class FriendsListAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<UserInfo> list = new ArrayList<>();
    private Context context;
    //final ListView listView = (ListView) findViewById(R.id.listViewUsers);

    public FriendsListAdapter(ArrayList<UserInfo> list, Context context) {
        this.list = list;
        this.context = context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.event_details_list_layout, null);
        }

        final TextView name = (TextView) view.findViewById(R.id.nameUser);
        final ImageView profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
        final TextView points = (TextView) view.findViewById(R.id.pointsUser);


        name.setText(list.get(position).name);
        Picasso.with(context).load(list.get(position).photo).into(profilePicture);
        points.setText(list.get(position).points);

        return view;
    }
}