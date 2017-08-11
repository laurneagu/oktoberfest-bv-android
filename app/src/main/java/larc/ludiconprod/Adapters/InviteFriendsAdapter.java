package larc.ludiconprod.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Activities.ActivitiesActivity;
import larc.ludiconprod.Activities.InviteFriendsActivity;
import larc.ludiconprod.Adapters.MainActivity.AroundMeAdapter;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.Friend;

/**
 * Created by ancuta on 8/4/2017.
 */

public class InviteFriendsAdapter  extends BaseAdapter implements ListAdapter {

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    class ViewHolder {

        CircleImageView friendProfileImage;
        TextView friendName;
        TextView friendLevel;
        Button inviteButton;


    }

    private ArrayList<Friend> list = new ArrayList<>();
    private Context context;
    private Activity activity;
    final ListView listView;
    private Resources resources;
    private InviteFriendsActivity fragment;

    public InviteFriendsAdapter(ArrayList<Friend> list, Context context, Activity activity, Resources resources, InviteFriendsActivity fragment) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.resources = resources;
        this.fragment = fragment;

        this.listView = (ListView) activity.findViewById(R.id.events_listView2); // era v.
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (list.size() > 0) {

            final InviteFriendsAdapter.ViewHolder holder;

            final Friend currentFriend = list.get(position);

            // Initialize the view
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.invite_friends_card, null);


                holder = new InviteFriendsAdapter.ViewHolder();
                holder.friendProfileImage=(CircleImageView)view.findViewById(R.id.profileImageInvitedFriend);
                holder.friendName=(TextView)view.findViewById(R.id.friendName);
                holder.friendLevel=(TextView)view.findViewById(R.id.friendLevel);
                holder.inviteButton=(Button)view.findViewById(R.id.inviteFriendButton);
                view.setTag(holder);
            } else {
                holder = (InviteFriendsAdapter.ViewHolder) view.getTag();
            }

            view.setBackgroundColor(Color.parseColor("#f7f9fc"));

            final View currView = view;
            if(position == 0){
                ViewGroup.LayoutParams params = holder.friendLevel.getLayoutParams();
                params.height =0;
                holder.friendLevel.setLayoutParams(params);
                holder.friendName.setText(currentFriend.userName);
                holder.friendProfileImage.setImageResource(R.drawable.ic_invite);


                holder.inviteButton.setVisibility(View.INVISIBLE);

                holder.friendProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Friend friend=new Friend();
                        friend.userName= Persistance.getInstance().getUserInfo(activity).lastName+"'s Friend";
                        friend.offlineFriend=true;
                        friend.profileImage="";
                        InviteFriendsActivity.friendsList.add(1,friend);
                        InviteFriendsActivity.numberOfOfflineFriends++;
                        InviteFriendsActivity.inviteFriendsAdapter.notifyDataSetChanged();
                    }
                });

            }else if(currentFriend.offlineFriend){
                ViewGroup.LayoutParams params = holder.friendLevel.getLayoutParams();
                params.height =0;
                holder.friendLevel.setLayoutParams(params);

                if(currentFriend.userName.length() < 16) {
                    holder.friendName.setText(currentFriend.userName);
                }
                else{
                    String names[] =currentFriend.userName.split(" ");
                    String displayName=names[0]+"\n";
                    for(int i=1;i < names.length;i++){
                        displayName=displayName+names[i];

                    }
                    holder.friendName.setText(displayName);
                }
                holder.friendProfileImage.setImageResource(R.drawable.ic_invite);

                holder.inviteButton.setText("REMOVE");
                holder.inviteButton.setTextColor(Color.parseColor("#ffffff"));
                holder.inviteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InviteFriendsActivity.numberOfOfflineFriends--;
                        InviteFriendsActivity.friendsList.remove(position);
                        InviteFriendsActivity.inviteFriendsAdapter.notifyDataSetChanged();
                    }
                });

            }



            else if(!currentFriend.offlineFriend){
                if(!currentFriend.profileImage.equals("")) {
                    Bitmap bitmap = decodeBase64(currentFriend.profileImage);
                    holder.friendProfileImage.setImageBitmap(bitmap);
                }

                ViewGroup.LayoutParams params = holder.friendLevel.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.friendLevel.setLayoutParams(params);

                if(!currentFriend.isInvited){
                    holder.inviteButton.setBackgroundResource(R.drawable.green_button_selector);
                    holder.inviteButton.setText("INVITE");
                    holder.inviteButton.setTextColor(Color.parseColor("#ffffff"));

                }else{
                holder.inviteButton.setBackgroundResource(R.drawable.transparent_button);
                holder.inviteButton.setText("INVITED");
                holder.inviteButton.setTextColor(Color.parseColor("#660c3855"));
                 }
                holder.inviteButton.setVisibility(View.VISIBLE);
                if(currentFriend.userName.length() < 16) {
                    holder.friendName.setText(currentFriend.userName);
                }
                else{
                    String names[] =currentFriend.userName.split(" ");
                    String displayName=names[0]+"\n";
                    for(int i=1;i < names.length;i++){
                        displayName=displayName+names[i];

                    }
                    holder.friendName.setText(displayName);
                }
                holder.friendLevel.setText("Level "+String.valueOf(currentFriend.level));
                holder.inviteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(currentFriend.isInvited){
                            holder.inviteButton.setBackgroundResource(R.drawable.green_button_selector);
                            holder.inviteButton.setText("INVITE");
                            holder.inviteButton.setTextColor(Color.parseColor("#ffffff"));
                            InviteFriendsActivity.friendsList.get(position).isInvited=false;
                        }else{
                            holder.inviteButton.setBackgroundResource(R.drawable.transparent_button);
                            holder.inviteButton.setText("INVITED");
                            holder.inviteButton.setTextColor(Color.parseColor("#660c3855"));
                            InviteFriendsActivity.friendsList.get(position).isInvited=true;
                        }
                    }
                });
            }


        }
        return view;

    }
}