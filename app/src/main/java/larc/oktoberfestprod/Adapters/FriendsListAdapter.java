/*
package larc.ludiconprod.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import larc.ludiconprod.Activities.EventDetails;
import larc.ludiconprod.Activities.GroupChatTemplate;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.UserInfo;

*/
/**
 * Created by LaurUser on 5/14/2016.
 *//*

public class FriendsListAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<UserInfo> list = new ArrayList<>();
    private Context context;
    private EventDetails eventInstance;
    //final ListView listView = (ListView) findViewById(R.id.listViewUsers);

    public FriendsListAdapter(ArrayList<UserInfo> list, Context context, EventDetails evDetailsInstance) {
        this.list = list;
        this.context = context;
        eventInstance = evDetailsInstance;
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
        profilePicture.setBackgroundResource(R.drawable.defaultpicture);
        Picasso.with(context).load(list.get(position).photo).into(profilePicture);
        points.setText(list.get(position).points);

        if(EventDetails.creatorIsCurrentUser) {
            final ImageButton removeUser = (ImageButton) view.findViewById(R.id.removeUserButton);
            removeUser.setVisibility(View.VISIBLE);
            removeUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    eventInstance.removeUser(name.getText().toString(),list.get(position).uid);
                }
            });
        }

        return view;
    }
}
*/
