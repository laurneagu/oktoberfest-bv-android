package larc.ludiconprod.Adapters.ChatAndFriends;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Activities.ChatActivity;
import larc.ludiconprod.Activities.ChatAndFriendsActivity;
import larc.ludiconprod.Controller.Persistance;
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

        RelativeLayout myLayout;
        RelativeLayout otherParticipantLayout;
        RelativeLayout profileImageLayout;
        CircleImageView otherParticipantImage;
        TextView otherParticipantMessage;
        TextView otherParticipantMessageTime;
        TextView myMessage;
        TextView myMessageTime;
        CircleImageView topOtherParticipantImage;
        TextView topOtherParticipantName;



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
                holder.myLayout=(RelativeLayout) view.findViewById(R.id.myLayout);
                holder.otherParticipantLayout=(RelativeLayout) view.findViewById(R.id.otherParticipantLayout);
                holder.profileImageLayout=(RelativeLayout) view.findViewById(R.id.profileImageLayout);
                holder.otherParticipantImage=(CircleImageView)view.findViewById(R.id.otherParticipantImage);
                holder.otherParticipantMessage=(TextView)view.findViewById(R.id.otherParticipantMessage);
                holder.otherParticipantMessageTime=(TextView)view.findViewById(R.id.otherParticipantMessageTime);
                holder.myMessage=(TextView)view.findViewById(R.id.myMessage);
                holder.myMessageTime=(TextView)view.findViewById(R.id.myMessageTime);
                holder.topOtherParticipantImage=(CircleImageView)view.findViewById(R.id.topOtherParticipantImage);
                holder.topOtherParticipantName=(TextView)view.findViewById(R.id.topOtherParticipantName);

                Typeface typeFace = Typeface.createFromAsset(fragment.getAssets(),"fonts/Quicksand-Medium.ttf");
                holder.otherParticipantMessage.setTypeface(typeFace);
                holder.otherParticipantMessageTime.setTypeface(typeFace);
                holder.myMessage.setTypeface(typeFace);
                holder.myMessageTime.setTypeface(typeFace);
                holder.topOtherParticipantName.setTypeface(typeFace);

                view.setTag(holder);
            } else {
                holder = (MessageAdapter.ViewHolder) view.getTag();
            }

            //clear layout
            holder.myLayout.setVisibility(View.INVISIBLE);
            holder.otherParticipantLayout.setVisibility(View.INVISIBLE);
            holder.profileImageLayout.setVisibility(View.INVISIBLE);
            holder.otherParticipantImage.setVisibility(View.VISIBLE);

            ViewGroup.LayoutParams params1 = holder.myLayout.getLayoutParams();
            params1.height =0;
            holder.myLayout.setLayoutParams(params1);

            ViewGroup.LayoutParams params2 = holder.otherParticipantLayout.getLayoutParams();
            params2.height =0;
            holder.otherParticipantLayout.setLayoutParams(params2);

            ViewGroup.LayoutParams params3 = holder.profileImageLayout.getLayoutParams();
            params3.height =0;
            holder.profileImageLayout.setLayoutParams(params3);


            if(currentMessage.authorId != null && currentMessage.authorId.equalsIgnoreCase(Persistance.getInstance().getUserInfo(activity).id)&& !currentMessage.setTopImage){
                holder.myLayout.setVisibility(View.VISIBLE);
                holder.myMessage.setText(currentMessage.message);

                ViewGroup.LayoutParams paramsMy = holder.myLayout.getLayoutParams();
                paramsMy.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.myLayout.setLayoutParams(paramsMy);
            }
            else if(currentMessage.authorId != null && !currentMessage.authorId.equalsIgnoreCase(Persistance.getInstance().getUserInfo(activity).id) && !currentMessage.setTopImage){
                holder.otherParticipantLayout.setVisibility(View.VISIBLE);
                holder.otherParticipantMessage.setText(currentMessage.message);
                if(currentMessage.otherUserImage != null){
                    Bitmap bitmap=decodeBase64(currentMessage.otherUserImage);
                    holder.otherParticipantImage.setImageBitmap(bitmap);
                }

                ViewGroup.LayoutParams paramsOther = holder.otherParticipantLayout.getLayoutParams();
                paramsOther.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.otherParticipantLayout.setLayoutParams(paramsOther);
                if(position < list.size()-1 && list.get(position+1).authorId.equalsIgnoreCase(list.get(position).authorId)){
                    holder.otherParticipantImage.setVisibility(View.INVISIBLE);
                }
            }else if(currentMessage.setTopImage){
                holder.profileImageLayout.setVisibility(View.VISIBLE);
                holder.topOtherParticipantName.setText(currentMessage.otherUserName.substring(0,currentMessage.otherUserName.length()-1));
                if(currentMessage.otherUserImage != null){
                    Bitmap bitmap=decodeBase64(currentMessage.otherUserImage);
                    holder.topOtherParticipantImage.setImageBitmap(bitmap);
                }

                ViewGroup.LayoutParams paramsProfile = holder.profileImageLayout.getLayoutParams();
                paramsProfile.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.profileImageLayout.setLayoutParams(paramsProfile);
            }

        }

        return view;
    }
}
