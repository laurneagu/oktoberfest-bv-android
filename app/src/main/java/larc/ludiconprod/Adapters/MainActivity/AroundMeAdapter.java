/*
package larc.ludiconprod.Adapters.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import larc.ludiconprod.Activities.EventDetails;
import larc.ludiconprod.Activities.MainActivityVechi;
import larc.ludiconprod.Activities.ProfileActivity;
import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.General;

*/
/**
 * Created by LaurUser on 7/4/2017.
 *//*


public class AroundMeAdapter extends BaseAdapter implements ListAdapter {

        class ViewHolder {
            TextView name;
            ImageView profilePicture;
            TextView firstPart;
            TextView secondPart;
            TextView time;
            TextView place;
            ImageView icon;
            Button join;
            TextView players;
            TextView description;
        }

        private ArrayList<Event> list = new ArrayList<>();
        private Context context;
        private Activity activity;
        private Resources resources;
        private MainActivityVechi fragment;
        final ListView listView;

        public AroundMeAdapter(ArrayList<Event> list, Context context, Activity activity, Resources resources, MainActivityVechi fragment) {
            this.list = list;
            this.context = context;
            this.activity = activity;
            this.resources = resources;
            this.fragment = fragment;

            this.listView = (ListView) activity.findViewById(R.id.events_listView1); // era v.
        }

    public void setListOfEvents(ArrayList<Event> newList){
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
            ViewHolder holder;

            final Event currentEvent = list.get(position);

            // Initialize the view
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.timeline_list_layout, null);

                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.nameLabel);
                holder.profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
                holder.firstPart = (TextView) view.findViewById(R.id.firstPartofText);
                holder.secondPart = (TextView) view.findViewById(R.id.secondPartofText);
                holder.time = (TextView) view.findViewById(R.id.timeText);
                holder.place = (TextView) view.findViewById(R.id.placeText);
                holder.icon = (ImageView) view.findViewById(R.id.sportIcon);
                holder.join = (Button) view.findViewById(R.id.join_btn);
                holder.description = (TextView) view.findViewById(R.id.descriptionID);
                holder.players = (TextView) view.findViewById(R.id.playersID);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            // Set name and picture for the first user of the event
            view.setBackgroundColor(Color.parseColor("#FFFFFF"));

            final View currView = view;

            // Event details redirect
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currView.setBackgroundColor(Color.parseColor("#D3D3D3"));

                    Intent intent = new Intent(currView.getContext(), EventDetails.class);
                    intent.putExtra("eventUid", currentEvent.id);
                    activity.startActivity(intent);
                }
            });

            // Set user event creator name and picture
            holder.name.setText(currentEvent.creatorName.split(" ")[0]);
            holder.profilePicture.setBackgroundResource(R.drawable.defaultpicture);
            Picasso.with(context).load(currentEvent.profileImageURL).into(holder.profilePicture);

            // Redirect to user profile on picture tap
            holder.profilePicture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(currView.getContext(), ProfileActivity.class);
                    intent.putExtra("uid", currentEvent.creator);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                }
            });

            // Event details set sport picture
            String uri = "@drawable/" + currentEvent.sport.toLowerCase().replace(" ", "");
            int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
            Drawable res = resources.getDrawable(imageResource);

            // Event details set message for sport played
            holder.icon.setImageDrawable(res);
            if (currentEvent.sport.equalsIgnoreCase("jogging") ||
                    currentEvent.sport.equalsIgnoreCase("gym"))
                holder.firstPart.setText("Will go to " + currentEvent.sport);
            else
                holder.firstPart.setText("Will play " + currentEvent.sport);

            // Event details set message for number of users to play
            if ((currentEvent.noUsers - 1) > 1) {
                holder.secondPart.setText(" with " + (currentEvent.noUsers - 1) + " others");
            } else if ((currentEvent.noUsers - 1) == 1) {
                holder.secondPart.setText(" with 1 other");
            } else {
                holder.secondPart.setText(" with no others");
            }

            // Event details set message for description
            if (currentEvent.description.equalsIgnoreCase(""))
                holder.description.setText("\"" + "I don't have a description for my event :(" + "\"");
            else
                holder.description.setText("\"" +currentEvent.description + "\"");

            // Event details set message number of players in capacity
            holder.players.setText(currentEvent.noUsers + "/" + currentEvent.roomCapacity);

            // Event details set message for place
            if (currentEvent != null)
                holder.place.setText(currentEvent.place);
            else
                holder.place.setText("Unknown");

            // Event details set message for date and time
            Calendar c = Calendar.getInstance();
            Date today = c.getTime();
            int todayDay = General.getDayOfMonth(today);
            int todayMonth = today.getMonth();
            int todayYear = today.getYear();

            String day;
            if (todayDay == General.getDayOfMonth(currentEvent.date) && todayMonth == currentEvent.date.getMonth() && todayYear == currentEvent.date.getYear())
                day = "Today";
            else if (todayDay == (General.getDayOfMonth(currentEvent.date) - 1) && todayMonth == currentEvent.date.getMonth() && todayYear == currentEvent.date.getYear())
                day = "Tomorrow";
            else
                day = General.getDayOfMonth(currentEvent.date) + "/" + (currentEvent.date.getMonth() + 1) + "/" + (currentEvent.date.getYear() + 1900);

            String dateHour = currentEvent.date.getHours() + "";
            String dateMin = currentEvent.date.getMinutes() + "";

            if (dateHour.equalsIgnoreCase("0")) dateHour += "0";
            if (dateMin.equalsIgnoreCase("0")) dateMin += "0";

            String hour = dateHour + ":" + dateMin;
            holder.time.setText(day + " at " + hour);

            // Event details set action on join button
            holder.join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference usersRef = User.firebaseRef.child("events").child(currentEvent.id).child("users");
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Map<String, Object> map = new HashMap<>();
                            for (DataSnapshot data : snapshot.getChildren()) {

                                map.put(data.getKey(), data.getValue());
                            }

                            // NOTE: I need userRef to set the map value
                            DatabaseReference userRef = User.firebaseRef.child("events").child(currentEvent.id).child("users").child(User.uid);
                            userRef.child("accepted").setValue(false);
                            userRef.child("profilePictureURL").setValue(User.profilePictureURL);

                            Map<String, Object> inEv = new HashMap<>();
                            inEv.put("participation", true);
                            inEv.put("points", 0);

                            Map<String, Object> ev = new HashMap<String, Object>();
                            ev.put(currentEvent.id, inEv);
                            list.remove(position);
                            User.firebaseRef.child("users").child(User.uid).child("events").updateChildren(ev);

                            fragment.updateListOfEvents(false);
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                        }
                    });
                }
            });
            return view;
        }
}

*/
