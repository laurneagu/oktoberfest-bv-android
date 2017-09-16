
package larc.ludiconprod.Adapters.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Activities.ActivitiesActivity;
import larc.ludiconprod.Activities.ActivityDetailsActivity;
import larc.ludiconprod.Activities.UserProfileActivity;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.General;
import larc.ludiconprod.Utils.util.Sponsors;
import larc.ludiconprod.Utils.util.Sport;


public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String getMonth(int month) {
        String date = new DateFormatSymbols().getMonths()[month - 1];
        return date.substring(0, 1).toUpperCase().concat(date.substring(1, 3));
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView profileImage;
        public TextView creatorName;
        public TextView sportName;
        public TextView sportLabel;
        public TextView ludicoinsNumber;
        public TextView pointsNumber;
        public TextView eventDate;
        public TextView locationEvent;
        public TextView playersNumber;
        public CircleImageView friends0;
        public CircleImageView friends1;
        public CircleImageView friends2;
        public TextView friendsNumber;
        public ImageView imageViewBackground;
        public TextView creatorLevelMyActivity;
        public ProgressBar progressBar;
        View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.profileImage = (CircleImageView) view.findViewById(R.id.profileImage);
            this.creatorName = (TextView) view.findViewById(R.id.creatorName);
            this.sportName = (TextView) view.findViewById(R.id.sportName);
            this.sportLabel = (TextView) view.findViewById(R.id.sportNameLabel);
            this.ludicoinsNumber = (TextView) view.findViewById(R.id.ludicoinsNumber);
            this.pointsNumber = (TextView) view.findViewById(R.id.pointsNumber);
            this.eventDate = (TextView) view.findViewById(R.id.eventDate);
            this.locationEvent = (TextView) view.findViewById(R.id.locationEvent);
            this.playersNumber = (TextView) view.findViewById(R.id.playersNumber);
            this.friends0 = (CircleImageView) view.findViewById(R.id.friends0);
            this.friends1 = (CircleImageView) view.findViewById(R.id.friends1);
            this.friends2 = (CircleImageView) view.findViewById(R.id.friends2);
            this.creatorLevelMyActivity = (TextView) view.findViewById(R.id.creatorLevelMyActivity);
            this.friendsNumber = (TextView) view.findViewById(R.id.friendsNumber);
            this.imageViewBackground = (ImageView) view.findViewById(R.id.imageViewBackground);
            this.creatorLevelMyActivity = (TextView) view.findViewById(R.id.creatorLevelMyActivity);
            this.progressBar = (ProgressBar) view.findViewById(R.id.cardProgressBar);
            this.progressBar.setAlpha(0);

            Typeface typeFace = Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Medium.ttf");
            Typeface typeFaceBold = Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Bold.ttf");

            this.creatorName.setTypeface(typeFace);
            this.sportName.setTypeface(typeFace);
            this.sportLabel.setTypeface(typeFace);
            this.ludicoinsNumber.setTypeface(typeFace);
            this.pointsNumber.setTypeface(typeFace);
            this.eventDate.setTypeface(typeFace);
            this.locationEvent.setTypeface(typeFace);
            this.playersNumber.setTypeface(typeFace);
            this.friendsNumber.setTypeface(typeFace);
            this.creatorLevelMyActivity.setTypeface(typeFace);


            // Clean up layout
            this.friends0.setVisibility(View.INVISIBLE);
            this.friends1.setVisibility(View.INVISIBLE);
            this.friends2.setVisibility(View.INVISIBLE);
            this.friendsNumber.setVisibility(View.INVISIBLE);
            this.profileImage.setImageResource(R.drawable.ph_user);
            this.friends0.setImageResource(R.drawable.ph_user);
            this.friends1.setImageResource(R.drawable.ph_user);
            this.friends2.setImageResource(R.drawable.ph_user);


            // Set name and picture for the first user of the event
            //view.setBackgroundColor(Color.parseColor("#FFFFFF"));

            final View currView = view;


        }

    }

   /* public class ViewHolderSponsors extends RecyclerView.ViewHolder{

        public LinearLayout sponsors;
        View view;

        public ViewHolderSponsors(View view) {
            super(view);
            this.view = view;
            this.sponsors =(LinearLayout) view.findViewById(R.id.sponsors);



            // Set name and picture for the first user of the event
            //view.setBackgroundColor(Color.parseColor("#FFFFFF"));

            final View currView = view;


        }

    }*/

    private ArrayList<Event> list = new ArrayList<>();
    private ArrayList<Sponsors> sponsorsList = new ArrayList<>();
    private Context context;
    private Activity activity;
    private Resources resources;
    private ActivitiesActivity fragment;
    final ListView listView;
    public static ProgressBar progressBarCard;

    public MyAdapter(ArrayList<Event> list, ArrayList<Sponsors> sponsorList, Context context, Activity activity, Resources resources, ActivitiesActivity fragment) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.resources = resources;
        this.fragment = fragment;
        this.sponsorsList=sponsorList;

        this.listView = (ListView) activity.findViewById(R.id.events_listView1); // era v.
    }

    public void setListOfEvents(ArrayList<Event> newList) {
        this.list = newList;
        this.notifyDataSetChanged();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (viewType) {

            /*case 0:
                view = inflater.inflate(R.layout.activities_sponsors_card, null);
                viewHolder=new ViewHolderSponsors(view);
                break;*/
            case 1:

                view = inflater.inflate(R.layout.my_activity_card, null);
                viewHolder=new ViewHolder(view);
                break;

        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,final int position) {

        // Clean up layout
        switch (holder.getItemViewType()) {
           /* case 0:
                ((ViewHolderSponsors)holder).sponsors.removeAllViews();
                for(int i=0;i < sponsorsList.size();i++){
                    int margins = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, activity.getResources().getDisplayMetrics());
                    ImageView imageView=new ImageView(activity);
                    Bitmap bitmap=decodeBase64(sponsorsList.get(i).logo);
                    imageView.setImageBitmap(bitmap);

                    LinearLayout.LayoutParams layoutMargins = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (i == sponsorsList.size() - 1) {
                        layoutMargins.setMargins(0, 0, 0, 0);
                    } else {
                        layoutMargins.setMargins(0, 0, margins, 0);
                    }
                    ((ViewHolderSponsors)holder).sponsors.addView(imageView,layoutMargins);
                }
                break;*/
            case 1:
                ((ViewHolder)holder).friends0.setVisibility(View.INVISIBLE);
                ((ViewHolder)holder).friends1.setVisibility(View.INVISIBLE);
                ((ViewHolder)holder).friends2.setVisibility(View.INVISIBLE);
                ((ViewHolder)holder).friendsNumber.setVisibility(View.INVISIBLE);
                ((ViewHolder)holder).profileImage.setImageResource(R.drawable.ph_user);
                ((ViewHolder)holder).friends0.setImageResource(R.drawable.ph_user);
                ((ViewHolder)holder).friends1.setImageResource(R.drawable.ph_user);
                ((ViewHolder)holder).friends2.setImageResource(R.drawable.ph_user);

            final Event currentEvent = list.get(position);

            // Event details redirect
                ((ViewHolder)holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //holder.view.setBackgroundColor(Color.parseColor("#f5f5f5"));
                    //getEventDetails & show progress bar when loading data
                    ((ViewHolder)holder).progressBar.setAlpha(1);
                    progressBarCard = ((ViewHolder)holder).progressBar;
                    HashMap<String, String> params = new HashMap<String, String>();
                    HashMap<String, String> headers = new HashMap<String, String>();
                    HashMap<String, String> urlParams = new HashMap<String, String>();
                    headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

                    //set urlParams


                    urlParams.put("eventId", currentEvent.id);
                    System.out.println(currentEvent.id + "eventId");
                    urlParams.put("userId", Persistance.getInstance().getUserInfo(activity).id);
                    HTTPResponseController.getInstance().getEventDetails(params, headers, activity, urlParams);


                }
            });

            // Set user event creator name and picture
                ((ViewHolder)holder).creatorName.setText(currentEvent.creatorName);
            if (!currentEvent.creatorProfilePicture.equals("")) {
                Bitmap bitmap = decodeBase64(currentEvent.creatorProfilePicture);
                ((ViewHolder)holder).profileImage.setImageBitmap(bitmap);

            }

            // Redirect to user profile on picture tap
                ((ViewHolder)holder).profileImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String id = currentEvent.creatorId;
                    if (Persistance.getInstance().getUserInfo(activity).id.equals(id)) {
                        Toast.makeText(activity, "It's you :)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(activity, UserProfileActivity.class);
                    intent.putExtra("UserId", id);
                    activity.startActivity(intent);
                }
            });


            // Event details set message for sport played
            Sport sport = new Sport(currentEvent.sportCode);

            String weWillPlayString = "";
            String sportName = "";

            if (currentEvent.sportCode.equalsIgnoreCase("JOG") || currentEvent.sportCode.equalsIgnoreCase("GYM") || currentEvent.sportCode.equalsIgnoreCase("CYC")) {
                weWillPlayString = "Will go to ";
                sportName = sport.sportName;
            } else {
                if (currentEvent.sportCode.equalsIgnoreCase("OTH")) {
                    weWillPlayString = "Will play ";
                    sportName = currentEvent.otherSportName;
                } else {
                    weWillPlayString = "Will play ";
                    sportName = sport.sportName;
                }
            }

            sportName = sportName.substring(0, 1).toUpperCase() + sportName.substring(1);

                ((ViewHolder)holder).sportLabel.setText(weWillPlayString);
                ((ViewHolder)holder).sportName.setText(sportName);

                ((ViewHolder)holder).ludicoinsNumber.setText("  +" + String.valueOf(currentEvent.ludicoins));
                ((ViewHolder)holder).pointsNumber.setText("  +" + String.valueOf(currentEvent.points));


                ((ViewHolder)holder).locationEvent.setText(currentEvent.placeName);
                ((ViewHolder)holder).playersNumber.setText(currentEvent.numberOfParticipants + "/" + currentEvent.capacity);
            int counter = 0;
            if (currentEvent.numberOfParticipants - 1 >= 1) {
                ((ViewHolder)holder).friends0.setVisibility(View.VISIBLE);
            }
            if (currentEvent.numberOfParticipants - 1 >= 2) {
                ((ViewHolder)holder).friends1.setVisibility(View.VISIBLE);
            }
            if (currentEvent.numberOfParticipants - 1 >= 3) {
                ((ViewHolder)holder).friends2.setVisibility(View.VISIBLE);
            }
            if (currentEvent.numberOfParticipants - 1 >= 4) {
                ((ViewHolder)holder).friendsNumber.setVisibility(View.VISIBLE);
                ((ViewHolder)holder).friendsNumber.setText("+" + String.valueOf(currentEvent.numberOfParticipants - 4));

            }
            for (int i = 0; i < currentEvent.participansProfilePicture.size(); i++) {
                if (!currentEvent.participansProfilePicture.get(i).equals("") && i == 0) {
                    Bitmap bitmap = decodeBase64(currentEvent.participansProfilePicture.get(i));
                    ((ViewHolder)holder).friends0.setImageBitmap(bitmap);
                } else if (!currentEvent.participansProfilePicture.get(i).equals("") && i == 1) {
                    Bitmap bitmap = decodeBase64(currentEvent.participansProfilePicture.get(i));
                    ((ViewHolder)holder).friends1.setImageBitmap(bitmap);
                } else if (!currentEvent.participansProfilePicture.get(i).equals("") && i == 2) {
                    Bitmap bitmap = decodeBase64(currentEvent.participansProfilePicture.get(i));
                    ((ViewHolder)holder).friends2.setImageBitmap(bitmap);
                }
            }

            switch (currentEvent.sportCode) {
                case "FOT":
                    ((ViewHolder)holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_football);
                    break;
                case "BAS":
                    ((ViewHolder)holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_basketball);
                    break;
                case "VOL":
                    ((ViewHolder)holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_volleyball);
                    break;
                case "JOG":
                    ((ViewHolder)holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_jogging);
                    break;
                case "GYM":
                    ((ViewHolder)holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_gym);
                    break;
                case "CYC":
                    ((ViewHolder)holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_cycling);
                    break;
                case "TEN":
                    ((ViewHolder)holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_tennis);
                    break;
                case "PIN":
                    ((ViewHolder)holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_pingpong);
                    break;
                case "SQU":
                    ((ViewHolder)holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_squash);
                    break;
                case "OTH":
                    ((ViewHolder)holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_others);
                    break;
            }

                ((ViewHolder)holder).creatorLevelMyActivity.setText(String.valueOf(currentEvent.creatorLevel));

            // Event details set message for date and time
            Calendar c = Calendar.getInstance();
            Date today = c.getTime();
            int todayDay = General.getDayOfMonth(today);
            int todayMonth = today.getMonth();
            int todayYear = c.get(Calendar.YEAR);
            String displayDate = "";
            String[] stringDateAndTime = currentEvent.eventDate.split(" ");
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                displayDate = formatter.format(formatter.parse(stringDateAndTime[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] stringDate = displayDate.split("-");
            int year = Integer.parseInt(stringDate[0]);
            String date = "";
            if (year <= todayYear && Integer.parseInt(stringDate[1]) - 1 == todayMonth && Integer.parseInt(stringDate[2]) == todayDay) {
                date = "Today, " + stringDateAndTime[1].substring(0, 5);
            } else if (year <= todayYear && Integer.parseInt(stringDate[1]) - 1 == todayMonth && Integer.parseInt(stringDate[2]) - 1 == todayDay) {
                date = "Tomorrow, " + stringDateAndTime[1].substring(0, 5);
            } else if (year <= todayYear) {
                date = getMonth(Integer.parseInt(stringDate[1])) + " " + stringDate[2] + ", " + stringDateAndTime[1].substring(0, 5);
            } else {
                date = stringDate[2] + " " + getMonth(Integer.parseInt(stringDate[1])) + " " + year + ", " + stringDateAndTime[1].substring(0, 5);
            }
                ((ViewHolder)holder).eventDate.setText(date);
                break;
        }
    }

    @Override
    public long getItemId(int pos) {
        return Long.valueOf(list.get(pos).id);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        /*if(position == 0 && sponsorsList.size() >= 1){
            return 0;
        }
        else{*/
            return 1;
       // }
    }

}

