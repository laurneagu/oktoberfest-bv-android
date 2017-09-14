package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Adapters.MainActivity.AroundMeAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.Dialogs.ConfirmationDialog;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.EventDetails;
import larc.ludiconprod.Utils.Friend;
import larc.ludiconprod.Utils.General;
import larc.ludiconprod.Utils.util.Sport;

/**
 * Created by ancuta on 8/9/2017.
 */

public class ActivityDetailsActivity extends Activity implements OnMapReadyCallback, Response.ErrorListener {
    RelativeLayout backButton;
    CircleImageView creatorImageProfile;
    TextView creatorName;
    TextView sportPlayed;
    TextView ludicoinsNumber;
    TextView pointsNumber;
    TextView playerNumber;
    LinearLayout imageProfileParticipantsLayout;
    CircleImageView participant0;
    CircleImageView participant1;
    CircleImageView participant2;
    CircleImageView participant3;
    TextView participantLevel0;
    TextView participantLevel1;
    TextView participantLevel2;
    TextView participantLevel3;
    ImageView deleteImageView0;
    ImageView deleteImageView1;
    ImageView deleteImageView2;
    ImageView deleteImageView3;
    TextView allParticipants;
    Button groupChatButton;
    Button inviteFriendsButton;
    TextView playTimeAndDate;
    ImageView companyImage;
    TextView locationName;
    TextView adress;
    MapFragment mapFragment;
    TextView description;
    Button editEventButton;
    Button deleteOrCancelEventButton;
    Button joinOrUnjoinButton;
    ImageView backgroundImage;
    GoogleMap m_gmap;
    public static boolean ifFirstTimeGetParticipants = false;
    double latitude;
    double longitude;
    int isAuthorizedPlace;
    int authorizedLevel;
    LinearLayout chatAndInviteLayout;
    static public String eventID = null;
    static ActivityDetailsActivity activity;

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public String getMonth(int month) {
        String date = new DateFormatSymbols().getMonths()[month - 1];
        return date.substring(0, 1).toUpperCase().concat(date.substring(1, 3));
    }

    public void getParticipants(String pageNumber, Bundle b, EventDetails eventDetails) {
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> urlParams = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(ActivityDetailsActivity.this).authKey);
        final String eventid = b.getString("eventId");
        eventID = eventid;
        urlParams.put("eventId", eventid);
        urlParams.put("userId", Persistance.getInstance().getUserInfo(ActivityDetailsActivity.this).id);
        urlParams.put("pageNumber", pageNumber);
        HTTPResponseController.getInstance().getParticipants(params, headers, ActivityDetailsActivity.this, urlParams);
    }


    @Override
    public void onCreate(Bundle savedInstance) {
        ifFirstTimeGetParticipants = false;
        InviteFriendsActivity.isFirstTimeInviteFriends = false;
        eventID = null;
        InviteFriendsActivity.participantList.clear();
        InviteFriendsActivity.friendsList.clear();
        super.onCreate(savedInstance);
        activity = this;
        setContentView(R.layout.activity_details_activity);


        final Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
        final Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.ttf");

        RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.tool_bar);
        TextView title = (TextView)toolbar.findViewById(R.id.titleText);
        title.setTypeface(typeFace);

        findViewById(R.id.internetRefresh).setAlpha(0);
        backButton = (RelativeLayout) findViewById(R.id.backButton);
        TextView titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("Activity Details");
        creatorImageProfile = (CircleImageView) findViewById(R.id.creatorImageProfile);
        creatorName = (TextView) findViewById(R.id.creatorName);
        sportPlayed = (TextView) findViewById(R.id.sportPlayed);
        ludicoinsNumber = (TextView) findViewById(R.id.ludicoinsNumber);
        pointsNumber = (TextView) findViewById(R.id.pointsNumber);
        playerNumber = (TextView) findViewById(R.id.playerNumber);
        chatAndInviteLayout = (LinearLayout) findViewById(R.id.chatAndInviteLayout);
        imageProfileParticipantsLayout = (LinearLayout) findViewById(R.id.imageProfileParticipantsLayout);
        participant0 = (CircleImageView) findViewById(R.id.participant0);
        participant1 = (CircleImageView) findViewById(R.id.participant1);
        participant2 = (CircleImageView) findViewById(R.id.participant2);
        participant3 = (CircleImageView) findViewById(R.id.participant3);
        participantLevel0 = (TextView) findViewById(R.id.participantLevel0);
        participantLevel1 = (TextView) findViewById(R.id.participantLevel1);
        participantLevel2 = (TextView) findViewById(R.id.participantLevel2);
        participantLevel3 = (TextView) findViewById(R.id.participantLevel3);
        deleteImageView0 = (ImageView) findViewById(R.id.deleteImageView0);
        deleteImageView1 = (ImageView) findViewById(R.id.deleteImageView1);
        deleteImageView2 = (ImageView) findViewById(R.id.deleteImageView2);
        deleteImageView3 = (ImageView) findViewById(R.id.deleteImageView3);
        allParticipants = (TextView) findViewById(R.id.allParticipants);
        groupChatButton = (Button) findViewById(R.id.groupChatButton);
        inviteFriendsButton = (Button) findViewById(R.id.inviteFriendsButton);
        playTimeAndDate = (TextView) findViewById(R.id.playTimeAndDate);
        companyImage = (ImageView) findViewById(R.id.companyImage);
        locationName = (TextView) findViewById(R.id.locationName);
        adress = (TextView) findViewById(R.id.adress);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        description = (TextView) findViewById(R.id.description);
        editEventButton = (Button) findViewById(R.id.editEventButton);
        deleteOrCancelEventButton = (Button) findViewById(R.id.deleteOrCancelEventButton);
        joinOrUnjoinButton = (Button) findViewById(R.id.joinOrUnjoinButton);
        backgroundImage = (ImageView) findViewById(R.id.backgroundImage);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


        //getDataToDisplayFromIntent
        final Bundle b;
        b = getIntent().getExtras();
        final EventDetails eventDetails = new EventDetails();
        eventDetails.eventDate = b.getInt("eventDate");
        eventDetails.description = b.getString("description");
        eventDetails.placeName = b.getString("placeName");
        eventDetails.latitude = b.getDouble("latitude");
        eventDetails.longitude = b.getDouble("longitude");
        eventDetails.placeId = b.getString("placeId");
        eventDetails.isAuthorized = b.getInt("isAuthorized");
        eventDetails.placeAdress = b.getString("placeAdress");
        eventDetails.authorizeLevel = b.getString("authorizelevel");
        eventDetails.companyImage = b.getString("placeImage");
        eventDetails.sportName = b.getString("sportName");
        eventDetails.otherSportName = b.getString("otherSportName");
        eventDetails.capacity = b.getInt("capacity");
        eventDetails.numberOfParticipants = b.getInt("numberOfParticipants");
        eventDetails.points = b.getInt("points");
        eventDetails.chatId = b.getString("chatId");
        eventDetails.privacy = b.getInt("privacy");
        eventDetails.isParticipant = b.getInt("isParticipant");
        eventDetails.ludicoins = b.getInt("ludicoins");
        eventDetails.creatorName = b.getString("creatorName");
        eventDetails.creatorLevel = b.getInt("creatorLevel");
        eventDetails.creatorId = b.getString("creatorId");
        eventDetails.creatorProfilePicture = b.getString("creatorProfilePicture");
        for (int i = 0; i < b.getStringArrayList("participantsId").size(); i++) {
            Friend friend = new Friend();
            friend.userID = b.getStringArrayList("participantsId").get(i);
            friend.userName = b.getStringArrayList("participantsName").get(i);
            friend.profileImage = b.getStringArrayList("participantsProfileImage").get(i);
            friend.level = b.getIntegerArrayList("participantsLevel").get(i);
            eventDetails.listOfParticipants.add(friend);
        }
        if (!eventDetails.creatorProfilePicture.equals("")) {
            Bitmap bitmap = decodeBase64(eventDetails.creatorProfilePicture);
            creatorImageProfile.setImageBitmap(bitmap);
        }

        creatorName.setText(eventDetails.creatorName);
        Sport sport = new Sport(eventDetails.sportName);
        String weWillPlayString = "";

        if (sport.code.equalsIgnoreCase("JOG") ||
                sport.code.equalsIgnoreCase("GYM") || sport.code.equalsIgnoreCase("CYC"))
            weWillPlayString = "Will go to " + sport.sportName;
        else
            if (sport.code.equalsIgnoreCase("OTH")) {
                weWillPlayString = "Will play " + eventDetails.otherSportName;
            } else
                weWillPlayString = "Will play " + sport.sportName;

        sportPlayed.setText(weWillPlayString);

        ludicoinsNumber.setText("+ " + String.valueOf(eventDetails.ludicoins));
        pointsNumber.setText("+ " + String.valueOf(eventDetails.points));
        playerNumber.setText("PLAYERS " + String.valueOf(eventDetails.numberOfParticipants + "/" + String.valueOf(eventDetails.capacity)));

        LinearLayout locationCard = (LinearLayout) findViewById(R.id.root);
        TextView t = (TextView) locationCard.findViewById(R.id.ludicoinsNumber);
        t.setText("+ " + String.valueOf(eventDetails.ludicoins));
        t = (TextView) locationCard.findViewById(R.id.pointsNumber);
        t.setText("+ " + String.valueOf(eventDetails.points));

        if (eventDetails.numberOfParticipants > 1) {

            ViewGroup.LayoutParams params = imageProfileParticipantsLayout.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            imageProfileParticipantsLayout.setLayoutParams(params);
        }
        for (int i = 0; i < eventDetails.listOfParticipants.size(); i++) {
            if (i == 0) {
                participant0.setVisibility(View.VISIBLE);
                participantLevel0.setVisibility(View.VISIBLE);
                if (!eventDetails.listOfParticipants.get(i).profileImage.equals("")) {
                    Bitmap bitmap = decodeBase64(eventDetails.listOfParticipants.get(i).profileImage);
                    participant0.setImageBitmap(bitmap);
                }
                participantLevel0.setText(String.valueOf(eventDetails.listOfParticipants.get(i).level));
                if (eventDetails.creatorId.equals(Persistance.getInstance().getUserInfo(this).id)) {
                    deleteImageView0.setVisibility(View.VISIBLE);
                    deleteImageView0.setEnabled(true);
                    final String eventid = b.getString("eventId");
                    deleteImageView0.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, String> params = new HashMap<String, String>();
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("authKey", Persistance.getInstance().getUserInfo(ActivityDetailsActivity.this).authKey);
                            params.put("eventId", eventid);
                            params.put("userId", eventDetails.listOfParticipants.get(0).userID);
                            HTTPResponseController.getInstance().leaveEvent(params, headers, ActivityDetailsActivity.this, true);
                        }
                    });
                }

            }
            if (i == 1) {
                participant1.setVisibility(View.VISIBLE);
                participantLevel1.setVisibility(View.VISIBLE);
                if (!eventDetails.listOfParticipants.get(i).profileImage.equals("")) {
                    Bitmap bitmap = decodeBase64(eventDetails.listOfParticipants.get(i).profileImage);
                    participant1.setImageBitmap(bitmap);
                }
                participantLevel1.setText(String.valueOf(eventDetails.listOfParticipants.get(i).level));
                if (eventDetails.creatorId.equals(Persistance.getInstance().getUserInfo(this).id)) {
                    deleteImageView1.setVisibility(View.VISIBLE);
                    deleteImageView1.setEnabled(true);
                    final String eventid = b.getString("eventId");
                    deleteImageView1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, String> params = new HashMap<String, String>();
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("authKey", Persistance.getInstance().getUserInfo(ActivityDetailsActivity.this).authKey);
                            params.put("eventId", eventid);
                            params.put("userId", eventDetails.listOfParticipants.get(1).userID);
                            HTTPResponseController.getInstance().leaveEvent(params, headers, ActivityDetailsActivity.this, true);
                        }
                    });
                }

            }
            if (i == 2) {
                participant2.setVisibility(View.VISIBLE);
                participantLevel2.setVisibility(View.VISIBLE);
                if (!eventDetails.listOfParticipants.get(i).profileImage.equals("")) {
                    Bitmap bitmap = decodeBase64(eventDetails.listOfParticipants.get(i).profileImage);
                    participant2.setImageBitmap(bitmap);
                }
                participantLevel2.setText(String.valueOf(eventDetails.listOfParticipants.get(i).level));
                if (eventDetails.creatorId.equals(Persistance.getInstance().getUserInfo(this).id)) {
                    deleteImageView2.setVisibility(View.VISIBLE);
                    deleteImageView2.setEnabled(true);
                    final String eventid = b.getString("eventId");
                    deleteImageView2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, String> params = new HashMap<String, String>();
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("authKey", Persistance.getInstance().getUserInfo(ActivityDetailsActivity.this).authKey);
                            params.put("eventId", eventid);
                            params.put("userId", eventDetails.listOfParticipants.get(2).userID);
                            HTTPResponseController.getInstance().leaveEvent(params, headers, ActivityDetailsActivity.this, true);
                        }
                    });
                }

            }
            if (i == 3) {
                participant3.setVisibility(View.VISIBLE);
                participantLevel3.setVisibility(View.VISIBLE);
                if (!eventDetails.listOfParticipants.get(i).profileImage.equals("")) {
                    Bitmap bitmap = decodeBase64(eventDetails.listOfParticipants.get(i).profileImage);
                    participant3.setImageBitmap(bitmap);
                }
                participantLevel3.setText(String.valueOf(eventDetails.listOfParticipants.get(i).level));
                if (eventDetails.creatorId.equals(Persistance.getInstance().getUserInfo(this).id)) {
                    deleteImageView3.setVisibility(View.VISIBLE);
                    deleteImageView3.setEnabled(true);
                    final String eventid = b.getString("eventId");
                    deleteImageView3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, String> params = new HashMap<String, String>();
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("authKey", Persistance.getInstance().getUserInfo(ActivityDetailsActivity.this).authKey);
                            params.put("eventId", eventid);
                            params.put("userId", eventDetails.listOfParticipants.get(3).userID);
                            HTTPResponseController.getInstance().leaveEvent(params, headers, ActivityDetailsActivity.this, true);
                        }
                    });
                }

            }

        }
        if (eventDetails.listOfParticipants.size() + (eventDetails.numberOfParticipants - eventDetails.listOfParticipants.size() - 1) >= 1) {

            participant0.setVisibility(View.VISIBLE);
        }
        if (eventDetails.listOfParticipants.size() + (eventDetails.numberOfParticipants - eventDetails.listOfParticipants.size() - 1) >= 2) {

            participant1.setVisibility(View.VISIBLE);
        }
        if (eventDetails.listOfParticipants.size() + (eventDetails.numberOfParticipants - eventDetails.listOfParticipants.size() - 1) >= 3) {

            participant2.setVisibility(View.VISIBLE);
        }
        if (eventDetails.listOfParticipants.size() + (eventDetails.numberOfParticipants - eventDetails.listOfParticipants.size() - 1) >= 4) {

            participant3.setVisibility(View.VISIBLE);

        }

        if (eventDetails.numberOfParticipants > 5) {
            allParticipants.setVisibility(View.VISIBLE);
        }

        HashMap<View, Friend> data = new HashMap<>();
        try {
            data.put(participant0, eventDetails.listOfParticipants.get(0));
            data.put(participant1, eventDetails.listOfParticipants.get(1));
            data.put(participant2, eventDetails.listOfParticipants.get(2));
            data.put(participant3, eventDetails.listOfParticipants.get(3));
        } catch (IndexOutOfBoundsException e) {
        }
        this.addParticipantsImaeListeners(data);
        allParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!ifFirstTimeGetParticipants) {
                    getParticipants("0", b, eventDetails);

                } else {
                    Intent intent = new Intent(ActivityDetailsActivity.this, InviteFriendsActivity.class);
                    intent.putExtra("isParticipant", true);
                    intent.putExtra("isEdit", false);
                    InviteFriendsActivity.isFirstTimeInviteFriends = false;
                    startActivity(intent);
                }

            }
        });

        groupChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!eventDetails.chatId.equalsIgnoreCase("")) {
                    Intent intent = new Intent(activity, ChatActivity.class);
                    intent.putExtra("chatId", eventDetails.chatId);
                    intent.putExtra("groupChat", 1);
                    startActivity(intent);
                } else {
                    Toast.makeText(ActivityDetailsActivity.this, "Group chat isn't avaible yet for this event!", Toast.LENGTH_LONG).show();
                }
            }
        });
        inviteFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToNextActivity = new Intent(ActivityDetailsActivity.this, InviteFriendsActivity.class);
                InviteFriendsActivity.friendsList.clear();
                InviteFriendsActivity.numberOfOfflineFriends = 0;
                goToNextActivity.putExtra("isEdit", true);
                goToNextActivity.putExtra("isCustomInvite", true);
                goToNextActivity.putExtra("eventId", eventID);
                goToNextActivity.putExtra("eventId", getIntent().getStringExtra("eventId"));
                startActivity(goToNextActivity);
            }
        });

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateToDisplay = formatter.format((long) eventDetails.eventDate * 1000);
        // Event details set message for date and time
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        int todayDay = General.getDayOfMonth(today);
        int todayMonth = today.getMonth();
        int todayYear = today.getYear();
        String displayDate = "";
        String[] stringDateAndTime = dateToDisplay.split(" ");
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            displayDate = format.format(format.parse(stringDateAndTime[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] stringDate = displayDate.split("-");
        String date = "";

        Calendar cal = Calendar.getInstance();
        cal.set(Integer.valueOf(stringDate[0]), Integer.valueOf(stringDate[1]) - 1, Integer.valueOf(stringDate[2]));
        String dayName = new DateFormatSymbols().getWeekdays()[cal
                .get(Calendar.DAY_OF_WEEK)];

        if (Integer.parseInt(stringDate[1]) - 1 == todayMonth && Integer.parseInt(stringDate[2]) == todayDay) {
            date = "Today " + "at " + stringDateAndTime[1].substring(0, 5);
        } else
            if (Integer.parseInt(stringDate[1]) - 1 == todayMonth && Integer.parseInt(stringDate[2]) - 1 == todayDay) {
                date = "Tomorrow " + "at " + stringDateAndTime[1].substring(0, 5);
            } else {
                date = dayName + ", " + getMonth(Integer.parseInt(stringDate[1])) + " " + stringDate[2] + ", " + stringDate[0] + " at " + stringDateAndTime[1].substring(0, 5);
            }
        playTimeAndDate.setText(date);

        locationName.setText(eventDetails.placeName);
        adress.setText(eventDetails.placeAdress);

        if (!eventDetails.companyImage.equals("")) {
            Bitmap bitmap = decodeBase64(eventDetails.companyImage);
            companyImage.setImageBitmap(bitmap);
        }

        mapFragment.getMapAsync(this);
        latitude = eventDetails.latitude;
        longitude = eventDetails.longitude;

        description.setText(eventDetails.description);
        if (eventDetails.creatorId.equals(Persistance.getInstance().getUserInfo(this).id) && eventDetails.listOfParticipants.size() == 0) {

            deleteOrCancelEventButton.setText("DELETE");
            final String eventid = b.getString("eventId");
            deleteOrCancelEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final ConfirmationDialog confirmationDialog = new ConfirmationDialog(activity);
                    confirmationDialog.show();
                    confirmationDialog.title.setText("Confirm?");
                    confirmationDialog.title.setTypeface(typeFaceBold);
                    confirmationDialog.message.setText("Are you sure you want to delete this event?");
                    confirmationDialog.message.setTypeface(typeFace);
                    confirmationDialog.confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, String> params = new HashMap<String, String>();
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("authKey", Persistance.getInstance().getUserInfo(ActivityDetailsActivity.this).authKey);
                            params.put("eventId", eventid);
                            params.put("userId", Persistance.getInstance().getUserInfo(ActivityDetailsActivity.this).id);
                            HTTPResponseController.getInstance().deleteEvent(params, headers, ActivityDetailsActivity.this);
                            deleteOrCancelEventButton.setEnabled(false);
                            confirmationDialog.dismiss();
                        }
                    });
                    confirmationDialog.dismiss.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            confirmationDialog.dismiss();
                        }
                    });

                }
            });

        } else
            if (eventDetails.creatorId.equals(Persistance.getInstance().getUserInfo(this).id) && eventDetails.listOfParticipants.size() >= 1) {
                deleteOrCancelEventButton.setText("LEAVE");
                //call join api method

                final String eventid = b.getString("eventId");
                deleteOrCancelEventButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String, String> params = new HashMap<String, String>();
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("authKey", Persistance.getInstance().getUserInfo(ActivityDetailsActivity.this).authKey);
                        params.put("eventId", eventid);
                        params.put("userId", Persistance.getInstance().getUserInfo(ActivityDetailsActivity.this).id);
                        HTTPResponseController.getInstance().leaveEvent(params, headers, ActivityDetailsActivity.this, false);
                        deleteOrCancelEventButton.setEnabled(false);
                    }
                });
            } else
                if (!eventDetails.creatorId.equals(Persistance.getInstance().getUserInfo(this).id) && eventDetails.isParticipant == 1) {
                    deleteOrCancelEventButton.setVisibility(View.INVISIBLE);
                    editEventButton.setVisibility(View.INVISIBLE);
                    joinOrUnjoinButton.setVisibility(View.VISIBLE);
                    joinOrUnjoinButton.setText("UNJOIN");
                    joinOrUnjoinButton.setBackgroundResource(R.drawable.pink_stroke_rounded_button);
                    joinOrUnjoinButton.setTextColor(Color.parseColor("#d4498b"));
                    //call join api method

                    final String eventid = b.getString("eventId");
                    joinOrUnjoinButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final ConfirmationDialog confirmationDialog = new ConfirmationDialog(activity);
                            confirmationDialog.show();
                            confirmationDialog.title.setText("Confirm?");
                            confirmationDialog.title.setTypeface(typeFaceBold);
                            confirmationDialog.message.setText("Are you sure you want to unjoin this event?");
                            confirmationDialog.message.setTypeface(typeFace);
                            confirmationDialog.confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    HashMap<String, String> params = new HashMap<String, String>();
                                    HashMap<String, String> headers = new HashMap<String, String>();
                                    headers.put("authKey", Persistance.getInstance().getUserInfo(ActivityDetailsActivity.this).authKey);
                                    params.put("eventId", eventid);
                                    params.put("userId", Persistance.getInstance().getUserInfo(ActivityDetailsActivity.this).id);
                                    HTTPResponseController.getInstance().leaveEvent(params, headers, ActivityDetailsActivity.this, false);
                                    joinOrUnjoinButton.setEnabled(false);
                                    confirmationDialog.dismiss();
                                }
                            });
                            confirmationDialog.dismiss.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    confirmationDialog.dismiss();
                                }
                            });
                        }
                    });

                } else
                    if (!eventDetails.creatorId.equals(Persistance.getInstance().getUserInfo(this).id) && eventDetails.isParticipant == 0) {
                        deleteOrCancelEventButton.setVisibility(View.INVISIBLE);
                        editEventButton.setVisibility(View.INVISIBLE);
                        joinOrUnjoinButton.setVisibility(View.VISIBLE);
                        joinOrUnjoinButton.setText("JOIN");
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) chatAndInviteLayout.getLayoutParams();
                        params.height = 0;
                        params.topMargin = 0;
                        chatAndInviteLayout.setLayoutParams(params);

                        final String eventid = b.getString("eventId");
                        //call join api method
                        joinOrUnjoinButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HashMap<String, String> params = new HashMap<String, String>();
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("authKey", Persistance.getInstance().getUserInfo(ActivityDetailsActivity.this).authKey);
                                params.put("eventId", eventid);
                                params.put("userId", Persistance.getInstance().getUserInfo(ActivityDetailsActivity.this).id);
                                HTTPResponseController.getInstance().joinEvent(ActivityDetailsActivity.this, params, headers, eventid, ActivityDetailsActivity.this);
                                joinOrUnjoinButton.setEnabled(false);
                            }
                        });

                    }
        if (eventDetails.creatorId.equals(Persistance.getInstance().getUserInfo(this).id)) {
            final String eventid = b.getString("eventId");
            editEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityDetailsActivity.this, CreateNewActivity.class);
                    intent.putExtra("isParticipant", false);
                    intent.putExtra("isEdit", true);
                    InviteFriendsActivity.isFirstTimeInviteFriends = false;
                    intent.putExtra("eventId", eventid);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
        }

        switch (sport.code) {
        case "FOT":
            backgroundImage.setBackgroundResource(R.drawable.bg_sport_football);
            break;
        case "BAS":
            backgroundImage.setBackgroundResource(R.drawable.bg_sport_basketball);
            break;
        case "VOL":
            backgroundImage.setBackgroundResource(R.drawable.bg_sport_volleyball);
            break;
        case "JOG":
            backgroundImage.setBackgroundResource(R.drawable.bg_sport_jogging);
            break;
        case "GYM":
            backgroundImage.setBackgroundResource(R.drawable.bg_sport_gym);
            break;
        case "CYC":
            backgroundImage.setBackgroundResource(R.drawable.bg_sport_cycling);
            break;
        case "TEN":
            backgroundImage.setBackgroundResource(R.drawable.bg_sport_tennis);
            break;
        case "PIN":
            backgroundImage.setBackgroundResource(R.drawable.bg_sport_pingpong);
            break;
        case "SQU":
            backgroundImage.setBackgroundResource(R.drawable.bg_sport_squash);
            break;
        case "OTH":
            backgroundImage.setBackgroundResource(R.drawable.bg_sport_others);
            break;
        }

        isAuthorizedPlace = eventDetails.isAuthorized;
        if (isAuthorizedPlace == 1) {
            authorizedLevel = Integer.valueOf(eventDetails.authorizeLevel);
        }

        findViewById(R.id.creatorLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = eventDetails.creatorId;
                Activity ac = ActivityDetailsActivity.this;
                if (Persistance.getInstance().getUserInfo(ac).id.equals(id)) {
                    Toast.makeText(ac, "It's you :)", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(ActivityDetailsActivity.this, UserProfileActivity.class);
                intent.putExtra("UserId", eventDetails.creatorId);
                startActivity(intent);
            }
        });

        //CLOSE PROGRESS BAR FROM LISTVIEW

        if (AroundMeAdapter.progressBarCard != null) {
            AroundMeAdapter.progressBarCard.setAlpha(0);
        }
        if (MyAdapter.progressBarCard != null) {
            MyAdapter.progressBarCard.setAlpha(0);
        }


        creatorName.setTypeface(typeFace);
        sportPlayed.setTypeface(typeFace);
        ((TextView) findViewById(R.id.youGain)).setTypeface(typeFace);
        ((TextView) findViewById(R.id.perHour)).setTypeface(typeFace);
        pointsNumber.setTypeface(typeFace);
        ludicoinsNumber.setTypeface(typeFace);
        playerNumber.setTypeface(typeFace);
        groupChatButton.setTypeface(typeFaceBold);
        inviteFriendsButton.setTypeface(typeFaceBold);
        playTimeAndDate.setTypeface(typeFaceBold);
        joinOrUnjoinButton.setTypeface(typeFaceBold);
    }

    private void addParticipantsImaeListeners(final HashMap<View, Friend> data) {
        Set<View> set = data.keySet();
        for (final View v : set) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = data.get(v).userID;
                    Activity ac = ActivityDetailsActivity.this;
                    if (Persistance.getInstance().getUserInfo(ac).id.equals(id)) {
                        Toast.makeText(ac, "It's you :)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(ActivityDetailsActivity.this, UserProfileActivity.class);
                    intent.putExtra("UserId", id);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_gmap = googleMap;
        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
                if (authorizedLevel == 0) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1_selected)));
                } else {
                    switch (authorizedLevel) {
                    case 0:
                        m_gmap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1_selected)));
                        break;
                    case 1:
                        m_gmap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_2_selected)));
                        break;
                    case 2:
                        m_gmap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_3_selected)));
                        break;

                    case 3:
                        m_gmap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_4_selected)));
                        break;


                    }
                }
            }
        });
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        if (error instanceof NetworkError) {
            joinOrUnjoinButton.setEnabled(true);
            RelativeLayout ll = (RelativeLayout) findViewById(R.id.noInternetLayout);
            final float scale = super.getResources().getDisplayMetrics().density;
            int pixels = (int) (56 * scale + 0.5f);
            ll.getLayoutParams().height = pixels;
            ll.setLayoutParams(ll.getLayoutParams());
        }
    }
}
