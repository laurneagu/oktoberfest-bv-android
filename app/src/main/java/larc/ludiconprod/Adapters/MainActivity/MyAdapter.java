package larc.ludiconprod.Adapters.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import larc.ludiconprod.Activities.EventDetails;
import larc.ludiconprod.Activities.ProfileActivity;
import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.General;

/**
 * Created by LaurUser on 7/4/2017.
 */

public class MyAdapter extends BaseAdapter implements ListAdapter {
        class ViewHolder {
            TextView name;
            ImageView profilePicture;
            TextView firstPart;
            TextView secondPart;
            TextView time;
            TextView place;
            ImageView icon;
            TextView description;
            TextView players;
        }

        private ArrayList<Event> list = new ArrayList<>();
        private Context context;
        private Activity activity;
        private Resources resources;

        public MyAdapter(ArrayList<Event> list, Context context, Activity activity, Resources resources) {
            this.list = list;
            this.context = context;
            this.activity = activity;
            this.resources = resources;
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

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.timeline_list_myactivities_layout, null);

                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.nameLabel);
                holder.profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
                holder.firstPart = (TextView) view.findViewById(R.id.firstPartofText);
                holder.secondPart = (TextView) view.findViewById(R.id.secondPartofText);
                holder.time = (TextView) view.findViewById(R.id.timeText);
                holder.place = (TextView) view.findViewById(R.id.placeText);
                holder.icon = (ImageView) view.findViewById(R.id.sportIcon);
                holder.description = (TextView) view.findViewById(R.id.descriptionID);
                holder.players = (TextView) view.findViewById(R.id.playersID);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            // Set name and picture for the first user of the event
            view.setBackgroundColor(Color.parseColor("#FFFFFF"));

            final View currView = view;
            view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currView.setBackgroundColor(Color.parseColor("#D3D3D3"));

                        Intent intent = new Intent(currView.getContext(), EventDetails.class);
                        intent.putExtra("eventUid", list.get(position).id);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                }
              }
            );

            String firstName = list.get(position).creatorName.split(" ")[0];
            holder.name.setText(firstName);
            holder.profilePicture.setBackgroundResource(R.drawable.defaultpicture);
            Picasso.with(context).load(list.get(position).profileImageURL).into(holder.profilePicture);

            // Redirect to user profile on picture click
            holder.profilePicture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (User.uid.equals(list.get(position).creator)) {
                        Toast.makeText(context, "This is you ! We can't compare with yourself..", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(currView.getContext(), ProfileActivity.class);
                        intent.putExtra("uid", list.get(position).creator);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                }
            });

            String uri = "@drawable/" + list.get(position).sport.toLowerCase().replace(" ", "");
            Log.v("drawable", uri);
            int imageResource = this.resources.getIdentifier(uri, null, context.getPackageName());
            Drawable res = this.resources.getDrawable(imageResource);

            holder.icon.setImageDrawable(res);
            if (list.get(position).sport.equalsIgnoreCase("jogging"))
                holder.firstPart.setText("Will go " + list.get(position).sport);
            else
                holder.firstPart.setText("Will play " + list.get(position).sport);
            if ((list.get(position).noUsers - 1) > 1) {
                holder.secondPart.setText(" with " + (list.get(position).noUsers - 1) + " others");
            } else if ((list.get(position).noUsers - 1) == 1) {
                holder.secondPart.setText(" with 1 other");
            } else {
                holder.secondPart.setText(" with no others");
            }

            if (list.get(position).description.equalsIgnoreCase(""))
                holder.description.setText("\"" + "I don't have a description for my event :(" + "\"");
            else
                holder.description.setText("\"" + list.get(position).description + "\"");

            holder.players.setText(list.get(position).noUsers + "/" + list.get(position).roomCapacity);

            if (list.get(position) != null) {
                if (list.get(position).isOfficial == 0) {
                    holder.place.setTextColor(Color.DKGRAY);
                }
                holder.place.setText(list.get(position).place);
            } else
                holder.place.setText("Unknown");

            Calendar c = Calendar.getInstance();
            Date today = c.getTime();
            int todayDay = General.getDayOfMonth(today);
            int todayMonth = today.getMonth();
            int todayYear = today.getYear();

            String day;
            if (todayDay == General.getDayOfMonth(list.get(position).date) && todayMonth == list.get(position).date.getMonth() && todayYear == list.get(position).date.getYear())
                day = "Today";
            else if (todayDay == (General.getDayOfMonth(list.get(position).date) - 1) && todayMonth == list.get(position).date.getMonth() && todayYear == list.get(position).date.getYear())
                day = "Tomorrow";
            else
                day = General.getDayOfMonth(list.get(position).date) + "/" + (list.get(position).date.getMonth() + 1) + "/" + (list.get(position).date.getYear() + 1900);
            String dateHour = (list.get(position).date.getHours() < 10) ? "0" + list.get(position).date.getHours() :
                    list.get(position).date.getHours() +  "";

            String dateMin = (list.get(position).date.getMinutes()<10)? "0" + list.get(position).date .getMinutes() :
                    list.get(position).date .getMinutes() +  "";

            if (dateHour.equalsIgnoreCase("0")) dateHour += "0";
            if (dateMin.equalsIgnoreCase("0")) dateMin += "0";
            String hour = dateHour + ":" + dateMin;
            holder.time.setText(day + " at " + hour);

            return view;
        }
    }
