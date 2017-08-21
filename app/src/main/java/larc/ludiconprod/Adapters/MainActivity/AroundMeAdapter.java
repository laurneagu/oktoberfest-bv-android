package larc.ludiconprod.Adapters.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Activities.ActivitiesActivity;
import larc.ludiconprod.Activities.ActivityDetailsActivity;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.General;
import larc.ludiconprod.Utils.util.Sport;

import static larc.ludiconprod.Activities.ActivitiesActivity.frlistView;


public class AroundMeAdapter extends BaseAdapter implements ListAdapter {

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    public static String getMonth(int month) {
        String date=new DateFormatSymbols().getMonths()[month-1];
        return date.substring(0,1).toUpperCase().concat(date.substring(1,3));
    }

        class ViewHolder {

            CircleImageView profileImage;
            TextView creatorName;
            TextView sportName;
            TextView ludicoinsNumber;
            TextView pointsNumber;
            TextView eventDate;
            TextView locationEvent;
            TextView playersNumber;
            CircleImageView friends0;
            CircleImageView friends1;
            CircleImageView friends2;
            TextView friendsNumber;
            Button joinButton;
            ImageView imageViewBackground;
            TextView creatorLevelAroundMe;

        }

        private ArrayList<Event> list = new ArrayList<>();
        private Context context;
        private Activity activity;
        private Resources resources;
        private ActivitiesActivity fragment;
        final ListView listView;

        public AroundMeAdapter(ArrayList<Event> list, Context context, Activity activity, Resources resources, ActivitiesActivity fragment) {
            this.list = list;
            this.context = context;
            this.activity = activity;
            this.resources = resources;
            this.fragment = fragment;

            this.listView = (ListView) activity.findViewById(R.id.events_listView2); // era v.
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
            if(list.size() > 0) {

                final ViewHolder holder;

                final Event currentEvent = list.get(position);

                // Initialize the view
                if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.around_me_card, null);

                    holder = new ViewHolder();
                    holder.profileImage = (CircleImageView) view.findViewById(R.id.profileImage);
                    holder.creatorName = (TextView) view.findViewById(R.id.creatorName);
                    holder.sportName = (TextView) view.findViewById(R.id.sportName);
                    holder.ludicoinsNumber = (TextView) view.findViewById(R.id.ludicoinsNumber);
                    holder.pointsNumber = (TextView) view.findViewById(R.id.pointsNumber);
                    holder.eventDate = (TextView) view.findViewById(R.id.eventDate);
                    holder.locationEvent = (TextView) view.findViewById(R.id.locationEvent);
                    holder.playersNumber = (TextView) view.findViewById(R.id.playersNumber);
                    holder.friends0 = (CircleImageView) view.findViewById(R.id.friends0);
                    holder.friends1 = (CircleImageView) view.findViewById(R.id.friends1);
                    holder.friends2 = (CircleImageView) view.findViewById(R.id.friends2);
                    holder.friendsNumber = (TextView) view.findViewById(R.id.friendsNumber);
                    holder.joinButton = (Button) view.findViewById(R.id.joinButton);
                    holder.imageViewBackground = (ImageView) view.findViewById(R.id.imageViewBackground);
                    holder.creatorLevelAroundMe=(TextView)view.findViewById(R.id.creatorLevelAroundMe);

                    view.setTag(holder);
                } else {
                    holder = (ViewHolder) view.getTag();
                }

                // Clean up layout
                holder.friends0.setVisibility(View.INVISIBLE);
                holder.friends1.setVisibility(View.INVISIBLE);
                holder.friends2.setVisibility(View.INVISIBLE);
                holder.friendsNumber.setVisibility(View.INVISIBLE);
                holder.profileImage.setImageResource(R.drawable.ph_user);
                holder.friends0.setImageResource(R.drawable.ph_user);
                holder.friends1.setImageResource(R.drawable.ph_user);
                holder.friends2.setImageResource(R.drawable.ph_user);


                        // Set name and picture for the first user of the event
                view.setBackgroundColor(Color.parseColor("#FFFFFF"));

                final View currView = view;

                // Event details redirect
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currView.setBackgroundColor(Color.parseColor("#f5f5f5"));
                        //getEventDetails

                        HashMap<String, String> params = new HashMap<String, String>();
                        HashMap<String, String> headers = new HashMap<String, String>();
                        HashMap<String, String> urlParams = new HashMap<String, String>();
                        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

                        //set urlParams

                        urlParams.put("eventId",currentEvent.id);
                        urlParams.put("userId",Persistance.getInstance().getUserInfo(activity).id);
                        HTTPResponseController.getInstance().getEventDetails(params, headers, activity,urlParams);
                    }
                });

                // Set user event creator name and picture
                holder.creatorName.setText(currentEvent.creatorName);
                if (!currentEvent.creatorProfilePicture.equals("")) {
                    Bitmap bitmap = decodeBase64(currentEvent.creatorProfilePicture);
                    holder.profileImage.setImageBitmap(bitmap);

                }

                // Redirect to user profile on picture tap
                holder.profileImage.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Toast.makeText(context, "Go to ProfileDetails", Toast.LENGTH_LONG).show();
                    }
                });


                // Event details set message for sport played
                Sport sport = new Sport(currentEvent.sportCode);

                String weWillPlayString = "";

                if (currentEvent.sportCode.equalsIgnoreCase("JOG") ||
                        currentEvent.sportCode.equalsIgnoreCase("GYM") || currentEvent.sportCode.equalsIgnoreCase("CYC"))
                    weWillPlayString = "Will go to " + sport.sportName;
                else
                    weWillPlayString = "Will play " + sport.sportName;

                holder.sportName.setText(weWillPlayString);

                holder.ludicoinsNumber.setText("  +" + String.valueOf(currentEvent.ludicoins));
                holder.pointsNumber.setText("  +" + String.valueOf(currentEvent.points));


                holder.locationEvent.setText(currentEvent.placeName);
                holder.playersNumber.setText(currentEvent.numberOfParticipants + "/" + currentEvent.capacity);
                if (currentEvent.numberOfParticipants - 1 >= 1) {
                    holder.friends0.setVisibility(View.VISIBLE);
                }
                if (currentEvent.numberOfParticipants - 1 >= 2) {
                    holder.friends1.setVisibility(View.VISIBLE);
                }
                if (currentEvent.numberOfParticipants - 1 >= 3) {
                    holder.friends2.setVisibility(View.VISIBLE);
                }
                if (currentEvent.numberOfParticipants - 1 >= 4) {
                    holder.friendsNumber.setVisibility(View.VISIBLE);
                    holder.friendsNumber.setText("+" + String.valueOf(currentEvent.numberOfParticipants - 4));

                }
                for (int i = 0; i < currentEvent.participansProfilePicture.size(); i++) {
                    if (!currentEvent.participansProfilePicture.get(i).equals("") && i==0) {
                        Bitmap bitmap = decodeBase64(currentEvent.participansProfilePicture.get(i));
                        holder.friends0.setImageBitmap(bitmap);
                    } else if (!currentEvent.participansProfilePicture.get(i).equals("") && i == 1) {
                        Bitmap bitmap = decodeBase64(currentEvent.participansProfilePicture.get(i));
                        holder.friends1.setImageBitmap(bitmap);
                    } else if (!currentEvent.participansProfilePicture.get(i).equals("") && i == 2) {
                        Bitmap bitmap = decodeBase64(currentEvent.participansProfilePicture.get(i));
                        holder.friends2.setImageBitmap(bitmap);
                    }
                }

                switch (currentEvent.sportCode) {
                    case "FOT":
                        holder.imageViewBackground.setBackgroundResource(R.drawable.bg_sport_football);
                        break;
                    case "BAS":
                        holder.imageViewBackground.setBackgroundResource(R.drawable.bg_sport_basketball);
                        break;
                    case "VOL":
                        holder.imageViewBackground.setBackgroundResource(R.drawable.bg_sport_volleyball);
                        break;
                    case "JOG":
                        holder.imageViewBackground.setBackgroundResource(R.drawable.bg_sport_jogging);
                        break;
                    case "GYM":
                        holder.imageViewBackground.setBackgroundResource(R.drawable.bg_sport_gym);
                        break;
                    case "CYC":
                        holder.imageViewBackground.setBackgroundResource(R.drawable.bg_sport_cycling);
                        break;
                    case "TEN":
                        holder.imageViewBackground.setBackgroundResource(R.drawable.bg_sport_tennis);
                        break;
                    case "PIN":
                        holder.imageViewBackground.setBackgroundResource(R.drawable.bg_sport_pingpong);
                        break;
                    case "SQU":
                        holder.imageViewBackground.setBackgroundResource(R.drawable.bg_sport_squash);
                        break;
                    case "OTH":
                        holder.imageViewBackground.setBackgroundResource(R.drawable.bg_sport_others);
                        break;
                }

                holder.creatorLevelAroundMe.setText(String.valueOf(currentEvent.creatorLevel));

                // Event details set message for date and time
                Calendar c = Calendar.getInstance();
                Date today = c.getTime();
                int todayDay = General.getDayOfMonth(today);
                int todayMonth = today.getMonth();
                int todayYear = today.getYear();
                String displayDate = "";
                String[] stringDateAndTime = currentEvent.eventDate.split(" ");
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    displayDate = formatter.format(formatter.parse(stringDateAndTime[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String[] stringDate = displayDate.split("-");
                String date = "";
                if (Integer.parseInt(stringDate[1]) - 1 == todayMonth && Integer.parseInt(stringDate[2]) == todayDay) {
                    date = "Today, " + stringDateAndTime[1].substring(0, 5);
                } else if (Integer.parseInt(stringDate[1]) - 1 == todayMonth && Integer.parseInt(stringDate[2]) - 1 == todayDay) {
                    date = "Tomorrow, " + stringDateAndTime[1].substring(0, 5);
                } else {
                    date = getMonth(Integer.parseInt(stringDate[1])) + " " + stringDate[2] + ", " + stringDateAndTime[1].substring(0, 5);
                }
                holder.eventDate.setText(date);

                // Event details set action on join button
                holder.joinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> params = new HashMap<String, String>();
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);
                        params.put("eventId", currentEvent.id);
                        params.put("userId", Persistance.getInstance().getUserInfo(activity).id);
                        HTTPResponseController.getInstance().joinEvent(activity,params, headers, currentEvent.id);
                        holder.joinButton.setEnabled(false);

                    }
                });
                System.out.println(currentEvent.id+" eventid:"+position+"  "+  currentEvent.numberOfParticipants + " profilepicture"+ currentEvent.participansProfilePicture.size());
            }

            return view;
        }
}

