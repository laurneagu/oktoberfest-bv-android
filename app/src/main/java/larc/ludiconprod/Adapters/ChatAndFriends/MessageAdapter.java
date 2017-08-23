package larc.ludiconprod.Adapters.ChatAndFriends;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Activities.ChatActivity;
import larc.ludiconprod.Activities.ChatAndFriendsActivity;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Chat;
import larc.ludiconprod.Utils.Message;

/**
 * Created by ancuta on 8/22/2017.
 */

public class MessageAdapter extends BaseAdapter implements ListAdapter {

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    class ViewHolder {

        TextView message;



    }

    private ArrayList<Message> list = new ArrayList<>();
    private Context context;
    private Activity activity;
    private Resources resources;
    private ChatActivity fragment;
    final ListView listView;

    public MessageAdapter(ArrayList<Message> list, Context context, Activity activity, Resources resources, ChatActivity fragment) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.resources = resources;
        this.fragment = fragment;

        this.listView = (ListView) activity.findViewById(R.id.events_listView2); // era v.
    }

    public void setListOfEvents(ArrayList<Message> newList){
        this.list = newList;
        this.notifyDataSetChanged();
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


        if (list.size() > 0) {

            final MessageAdapter.ViewHolder holder;

            final Message currentMessage = list.get(position);
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.message_card, null);
                holder = new MessageAdapter.ViewHolder();
                holder.message=(TextView) view.findViewById(R.id.message);
                view.setTag(holder);
            } else {
                holder = (MessageAdapter.ViewHolder) view.getTag();
            }

            //clear layout

            holder.message.setText(currentMessage.message);

        }

        return view;
    }
}
