package larc.ludiconprod.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import larc.ludiconprod.Adapters.CustomSpinner;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.General;
import larc.ludiconprod.Utils.Location.GPSTracker;
import larc.ludiconprod.ViewPagerHelper.MyFragment;

import static larc.ludiconprod.Activities.IntroActivity.decodeBase64;

/**
 * Created by ancuta on 7/31/2017.
 */

public class CreateNewActivity extends Activity implements AdapterView.OnItemSelectedListener,OnMapReadyCallback {

    String[] sportNames={"Football","Basketball","Volleyball","Jogging","Gym","Cycling","Tennis","Ping Pong","Squash","Others"};
    int sportImages[] = {R.drawable.ic_sport_football, R.drawable.ic_sport_basketball, R.drawable.ic_sport_voleyball, R.drawable.ic_sport_jogging, R.drawable.ic_sport_gym,
            R.drawable.ic_sport_cycling, R.drawable.ic_sport_tennis, R.drawable.ic_sport_pingpong, R.drawable.ic_sport_squash, R.drawable.ic_sport_others};
    String[] privacyNames={"Public","Private"};
    int privacyImages[] = {R.drawable.ic_bnav_user_selected,R.drawable.ic_lock};
    TextView privateText;
    RelativeLayout otherSportLayout;
    RelativeLayout dateLayout;
    TextView calendarTextView;
    RelativeLayout timeLayout;
    TextView playersNumber;
    TextView hourTextView;
    ImageView minusButton;
    ImageView plusButton;
    private GoogleMap m_gmap;
    double longitude=0;
    double latitude=0;
    ImageButton backButton;
    TextView tapHereTextView;
    static public int ASK_COORDS = 1000;
    static public int ASK_COORDS_DONE = 1001;

    public String getMonth(int month) {
        String date=new DateFormatSymbols().getMonths()[month-1];
        return date.substring(0,1).toUpperCase().concat(date.substring(1,3));
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.create_new_activity);
        privateText = (TextView) findViewById(R.id.privateText);
        otherSportLayout = (RelativeLayout) findViewById(R.id.chooseSportNameLayout);
        final Spinner sportSpinner = (Spinner) findViewById(R.id.sportSpinner);
        dateLayout = (RelativeLayout) findViewById(R.id.dateLayout);
        timeLayout = (RelativeLayout) findViewById(R.id.timeLayout);
        calendarTextView = (TextView) findViewById(R.id.calendarTextView);
        hourTextView = (TextView) findViewById(R.id.hourTextView);
        backButton=(ImageButton) findViewById(R.id.backButton);
        backButton.setBackgroundResource(R.drawable.ic_nav_up);
        minusButton=(ImageView) findViewById(R.id.minusButton);
        plusButton=(ImageView) findViewById(R.id.plusButton);
        playersNumber=(TextView) findViewById(R.id.playersNumber);
        TextView titleText=(TextView) findViewById(R.id.titleText);
        tapHereTextView=(TextView)findViewById(R.id.tapHereTextView);
        ImageView invitedFriends0=(ImageView) findViewById(R.id.invitedFriends0) ;

        invitedFriends0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateNewActivity.this, InviteFriendsActivity.class);
                startActivity(intent);
            }
        });
        GMapsActivity.markerSelected=null;


        titleText.setText("Create Activity");
        Typeface typeFace= Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Medium.ttf");
        Typeface typeFaceBold= Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Bold.ttf");


        sportSpinner.setOnItemSelectedListener(this);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CustomSpinner customAdapterSports = new CustomSpinner(getApplicationContext(), sportImages, sportNames);
        sportSpinner.setAdapter(customAdapterSports);
        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (sportSpinner.getSelectedItemPosition() == 9) {
                    ViewGroup.LayoutParams params = otherSportLayout.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    otherSportLayout.setLayoutParams(params);
                } else {
                    ViewGroup.LayoutParams params = otherSportLayout.getLayoutParams();
                    params.height = 0;
                    otherSportLayout.setLayoutParams(params);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateNewActivity.this, Main.class);
                CreateNewActivity.this.startActivity(intent);
            }
        });

        final Spinner privacySpinner = (Spinner) findViewById(R.id.privacySpinner);
        privacySpinner.setOnItemSelectedListener(this);

        CustomSpinner customAdapterPrivacy = new CustomSpinner(getApplicationContext(), privacyImages, privacyNames);
        privacySpinner.setAdapter(customAdapterPrivacy);
        privacySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (privacySpinner.getSelectedItemPosition() == 1) {
                    ViewGroup.LayoutParams params = privateText.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    privateText.setLayoutParams(params);
                    ViewGroup.MarginLayoutParams margins = (ViewGroup.MarginLayoutParams) privateText.getLayoutParams();
                    margins.topMargin = 16;
                    privateText.setLayoutParams(margins);
                } else {
                    ViewGroup.LayoutParams params = privateText.getLayoutParams();
                    params.height = 0;
                    privateText.setLayoutParams(params);
                    ViewGroup.MarginLayoutParams margins = (ViewGroup.MarginLayoutParams) privateText.getLayoutParams();
                    margins.topMargin = 0;
                    privateText.setLayoutParams(margins);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String displayDate = formatter.format(myCalendar.getTime());
                    String[] stringDate = displayDate.split("-");
                    String date = stringDate[2] + " " + getMonth(Integer.parseInt(stringDate[1])) + " " + stringDate[0];
                    calendarTextView.setText(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ;

            }

        };


        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                // TODO Auto-generated method stub
                DatePickerDialog dpd = new DatePickerDialog(CreateNewActivity.this, R.style.DialogTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        timeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CreateNewActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hourTextView.setText((selectedHour < 9 ? "0" : "") + selectedHour + ":" + (selectedMinute < 9 ? "0" : "") + selectedMinute);
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendar.set(Calendar.MINUTE, selectedMinute);

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!playersNumber.getText().toString().equals("2")){
                    playersNumber.setText(String.valueOf(Integer.valueOf(playersNumber.getText().toString())-1));
                }
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!playersNumber.getText().toString().equals("20")){
                    playersNumber.setText(String.valueOf(Integer.valueOf(playersNumber.getText().toString())+1));
                }
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_gmap = googleMap;
        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        GPSTracker gps = new GPSTracker(getApplicationContext(),  CreateNewActivity.this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            gps.stopUsingGPS();
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Intent goToNextActivity = new Intent(CreateNewActivity.this, GMapsActivity.class);
                        goToNextActivity.putExtra("latitude",String.valueOf(latitude));
                        goToNextActivity.putExtra("longitude",String.valueOf(longitude));
                        startActivityForResult(goToNextActivity, ASK_COORDS);
                    }
                });
            }
        });
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Double lat=0.0;
        Double lng=0.0;
        String addressName="";
        String placeName="";
        String image="";
        int ludicoins=0;
        int points=0;
        int authorizeEventLevel=-1;
        tapHereTextView.setText("");
        if (data != null) {
            lat = data.getDoubleExtra("latitude", 0);
            lng = data.getDoubleExtra("longitude", 0);
            authorizeEventLevel = data.getIntExtra("AuthorizeEventLevel", -2);
            addressName = data.getStringExtra("address");
            placeName = data.getStringExtra("placeName");
            if(authorizeEventLevel != -1){
                image=data.getStringExtra("image");
                ludicoins =data.getIntExtra("ludicoins", 0);
                points =data.getIntExtra("points", 0);
            }



        }

        View  selected_location_layout= findViewById(R.id.root);
        ViewGroup.LayoutParams params = selected_location_layout.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        selected_location_layout.setLayoutParams(params);




        ImageView companyImage=(ImageView)findViewById(R.id.companyImage) ;

        if(!image.equals("") ){
            Bitmap bitmap=decodeBase64(image);
            companyImage.setImageBitmap(bitmap);
        }
        TextView locationName=(TextView)findViewById(R.id.locationName);
        locationName.setText(placeName);

        TextView adress=(TextView)findViewById(R.id.adress);
        adress.setText(addressName);

        TextView ludicoinsNumber=(TextView)findViewById(R.id.ludicoinsNumber);
        TextView pointsNumber=(TextView)findViewById(R.id.pointsNumber);

        if(authorizeEventLevel != -1){
            ludicoinsNumber.setText(String.valueOf(ludicoins));
            pointsNumber.setText(String.valueOf(points));
        }
        else{
            ludicoinsNumber.setText(String.valueOf(-1));
            pointsNumber.setText(String.valueOf(-1));
        }







        LatLng latLng = new LatLng(lat, lng);

        m_gmap.clear();

        switch (authorizeEventLevel){
            case -1:
                m_gmap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1_selected)));
                break;
            case 0:
                m_gmap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1_selected)));
                break;
            case 1:
                m_gmap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_2_selected)));
                break;
            case 2:
                m_gmap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_3_selected)));
                break;

            case 3:
                m_gmap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_4_selected)));
                break;


        }
        m_gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }
    }

