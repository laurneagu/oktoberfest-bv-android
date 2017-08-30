package larc.ludiconprod.Adapters.ChatAndFriends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Activities.ActivitiesActivity;
import larc.ludiconprod.Activities.ChatActivity;
import larc.ludiconprod.Activities.ChatAndFriendsActivity;
import larc.ludiconprod.Adapters.MainActivity.AroundMeAdapter;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Chat;
import larc.ludiconprod.Utils.Event;

import static larc.ludiconprod.Activities.ChatAndFriendsActivity.threadsList;

/**
 * Created by ancuta on 8/18/2017.
 */

public class ConversationsAdapter extends BaseAdapter implements ListAdapter {
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    class ViewHolder {

        CircleImageView chatParticipantImage;
        TextView participantName;
        TextView timeElapsed;
        TextView lastMessage;


    }
    private ArrayList<Chat> list = new ArrayList<>();
    private Context context;
    private Activity activity;
    private Resources resources;
    private ChatAndFriendsActivity fragment;
    final ListView listView;
    public ConversationsAdapter(ArrayList<Chat> list, Context context, Activity activity, Resources resources, ChatAndFriendsActivity fragment) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.resources = resources;
        this.fragment = fragment;

        this.listView = (ListView) activity.findViewById(R.id.events_listView2); // era v.
    }

    public void setListOfEvents(ArrayList<Chat> newList){
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


        if(list.size() > 0) {

            final ConversationsAdapter.ViewHolder holder;

            final Chat currentChat = list.get(position);

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.chat_list_card, null);
                holder = new ConversationsAdapter.ViewHolder();
                holder.chatParticipantImage=(CircleImageView)view.findViewById(R.id.chatParticipantImage);
                holder.participantName=(TextView) view.findViewById(R.id.participantName);
                holder.timeElapsed=(TextView)view.findViewById(R.id.timeElapsed);
                holder.lastMessage=(TextView)view.findViewById(R.id.lastMessage);
                view.setTag(holder);
            } else {
                holder = (ConversationsAdapter.ViewHolder) view.getTag();
            }
            //clear layout
            holder.chatParticipantImage.setImageResource(R.drawable.ic_user);
            if(threadsList.size() > 0 && threadsList.size() > position && threadsList.get(position) != null ) {
                threadsList.get(position).cancel();
                threadsList.remove(position);
            }

            if(currentChat.image != null){
                Bitmap bitmap=decodeBase64(currentChat.image);
                holder.chatParticipantImage.setImageBitmap(bitmap);
            }

            final int timeElapsed=(int)((System.currentTimeMillis()/1000 - currentChat.lastMessageTime)/60);
            setTime(timeElapsed,holder,currentChat);
            updateTime(timeElapsed,holder,currentChat,position);
            if(currentChat.participantName.length() > 1) {
                holder.participantName.setText(currentChat.participantName.substring(0, currentChat.participantName.length() - 1));
            }
            Typeface typeFace= Typeface.createFromAsset(activity.getAssets(),"fonts/Quicksand-Medium.ttf");
            Typeface typeFaceBold= Typeface.createFromAsset(activity.getAssets(),"fonts/Quicksand-Bold.ttf");
            holder.lastMessage.setText(currentChat.lastMessage);
            if(!currentChat.lastMessageSeen.equalsIgnoreCase(currentChat.lastMessageId)) {
                holder.lastMessage.setTypeface(typeFaceBold);
            }else{
                holder.lastMessage.setTypeface(typeFace);
            }
            final View currView = view;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(activity, ChatActivity.class);
                    intent.putExtra("chatId",currentChat.chatId);
                    intent.putExtra("otherParticipantName",currentChat.participantName);
                    intent.putExtra("otherParticipantImage",currentChat.image);

                    activity.startActivity(intent);
                    ChatAndFriendsActivity.isOnChatPage=false;
                    //activity.finish();
                }
            });







        }

        return view;
    }

    public void updateTime(final int timeElapsed,final ViewHolder holder,final Chat currentChat,final int position){
        threadsList.add(position,new CountDownTimer(3600000, 60000) {
            int newtimeElapsed=timeElapsed;
            @Override
            public void onTick(long l) {
                newtimeElapsed=newtimeElapsed+1;
                setTime(newtimeElapsed,holder,currentChat);

            }

            @Override
            public void onFinish() {
                updateTime(newtimeElapsed,holder,currentChat,position);
            }
            }.start());
    }

    public void setTime(int timeElapsed,ViewHolder holder,Chat currentChat){
        if(timeElapsed == 0){
            holder.timeElapsed.setText("less than a min ago");
        }else if(timeElapsed < 60){
            holder.timeElapsed.setText(Integer.valueOf(timeElapsed)+" min ago");
        }else if(timeElapsed < 1440){
            holder.timeElapsed.setText(Integer.valueOf(timeElapsed/60)+" hour ago");
        }else if(timeElapsed < 10080){
            holder.timeElapsed.setText(Integer.valueOf(timeElapsed/1440)+" days ago");
        }else{
            String displayDate="";
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");
                java.util.Date date=new java.util.Date((long)currentChat.lastMessageTime*1000);
                displayDate = formatter.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.timeElapsed.setText(displayDate);
        }
    }
}
