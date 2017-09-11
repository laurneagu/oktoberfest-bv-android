package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.User;
import larc.ludiconprod.Utils.util.Sport;

/**
 * Created by ancuta on 7/18/2017.
 */

public class SportDetailsActivity extends Activity {
    RadioButton football;
    boolean footballSelected=false;
    RadioButton basketball;
    boolean basketballSelected=false;
    RadioButton volleyball;
    boolean volleyballSelected=false;
    RadioButton jogging;
    boolean joggingSelected=false;
    RadioButton gym;
    boolean gymSelected=false;
    RadioButton cycling;
    boolean cyclingSelected=false;
    RadioButton tennis;
    boolean tennisSelected=false;
    RadioButton pingPong;
    boolean pingPongSelected=false;
    RadioButton squash;
    boolean squashSelected=false;
    RadioButton others;
    boolean othersSelected=false;
    RelativeLayout backButton;
    Button savePreferincesButton;
    SeekBar seekBar;
    int counter=10;
    int progress=20;
    TextView progressText;
    String sportsArray[]={"FOT","BAS","VOL","JOG","GYM","CYC","TEN","PIN","SQU","OTH"};
    ArrayList<String> sports=new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.sport_details_activity);
        football=(RadioButton)findViewById(R.id.football);
        TextView titleText=(TextView) findViewById(R.id.titleText);
        titleText.setText("Sport Details");
        backButton=(RelativeLayout) findViewById(R.id.backButton);
        seekBar=(SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(19);
        seekBar.setProgress(19);
        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#d4498b"), PorterDuff.Mode.SRC);
        seekBar.getThumb().setColorFilter(Color.parseColor("#d4498b"), PorterDuff.Mode.SRC_IN);
        savePreferincesButton=(Button) findViewById(R.id.savePreferincesButton);
        progressText=(TextView) findViewById(R.id.rangeTextView);
        Typeface typeFace= Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Medium.ttf");
        Typeface typeFaceBold= Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Bold.ttf");
        savePreferincesButton.setTypeface(typeFaceBold);
        for(int i=0;i<sportsArray.length;i++){
            sports.add(sportsArray[i]);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                if(seekBar.getProgress() >= 1) {
                    progress = progressValue+1;
                    progressText.setText(progress + " km");
                }
                else{
                    progressText.setText("1" + " km");
                    progress=1;
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });



        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String profileImage = getIntent().getStringExtra("profileImage");
                String yearBorn=getIntent().getStringExtra("yearBorn");
                String gender=getIntent().getStringExtra("gender");
                Intent intent = new Intent(SportDetailsActivity.this, ProfileDetailsActivity.class);
                intent.putExtra("profileImage", profileImage);
                intent.putExtra("yearBorn",yearBorn);
                intent.putExtra("gender",gender);
                SportDetailsActivity.this.startActivity(intent);
            }
        });

        football.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!footballSelected && counter > 1 ){
                    football.setAlpha(0.4f);
                    footballSelected=true;
                    football.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_football,0,0,0);
                    counter--;
                    for(int i=0;i<sports.size();i++){
                        if(sports.get(i).equals("FOT"))
                            sports.remove(i);
                    }
                }
                else if(footballSelected){
                    football.setAlpha(1f);
                    footballSelected=false;
                    football.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_football,0,R.drawable.ic_check,0);
                    counter++;
                    sports.add("FOT");
                }

            }
        });
        basketball=(RadioButton)findViewById(R.id.basketball);
        basketball.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!basketballSelected && counter > 1){
                    basketball.setAlpha(0.4f);
                    basketballSelected=true;
                    basketball.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_basketball,0,0,0);
                    counter--;
                    for(int i=0;i<sports.size();i++){
                        if(sports.get(i).equals("BAS"))
                            sports.remove(i);
                    }
                }
                else if(basketballSelected){
                    basketball.setAlpha(1f);
                    basketballSelected=false;
                    basketball.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_basketball,0,R.drawable.ic_check,0);
                    counter++;
                    sports.add("BAS");
                }

            }
        });
        volleyball=(RadioButton)findViewById(R.id.volleyball);
        volleyball.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!volleyballSelected && counter > 1){
                    volleyball.setAlpha(0.4f);
                    volleyballSelected=true;
                    volleyball.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_voleyball,0,0,0);
                    counter--;
                    for(int i=0;i<sports.size();i++){
                        if(sports.get(i).equals("VOL"))
                            sports.remove(i);
                    }
                }
                else if(volleyballSelected){
                    volleyball.setAlpha(1f);
                    volleyballSelected=false;
                    volleyball.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_voleyball,0,R.drawable.ic_check,0);
                    counter++;
                    sports.add("VOL");
                }

            }
        });
        jogging=(RadioButton)findViewById(R.id.jogging);
        jogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!joggingSelected && counter > 1){
                    jogging.setAlpha(0.4f);
                    joggingSelected=true;
                    jogging.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_jogging,0,0,0);
                    counter--;
                    for(int i=0;i<sports.size();i++){
                        if(sports.get(i).equals("JOG"))
                            sports.remove(i);
                    }
                }
                else if(joggingSelected){
                    jogging.setAlpha(1f);
                    joggingSelected=false;
                    jogging.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_jogging,0,R.drawable.ic_check,0);
                    counter++;
                    sports.add("JOG");
                }

            }
        });
        gym=(RadioButton)findViewById(R.id.gym);
        gym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!gymSelected && counter > 1){
                    gym.setAlpha(0.4f);
                    gymSelected=true;
                    gym.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_gym,0,0,0);
                    counter--;
                    for(int i=0;i<sports.size();i++){
                        if(sports.get(i).equals("GYM"))
                            sports.remove(i);
                    }
                }
                else if(gymSelected){
                    gym.setAlpha(1f);
                    gymSelected=false;
                    gym.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_gym,0,R.drawable.ic_check,0);
                    counter++;
                    sports.add("GYM");
                }

            }
        });
        cycling=(RadioButton)findViewById(R.id.cycling);
        cycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!cyclingSelected && counter > 1){
                    cycling.setAlpha(0.4f);
                    cyclingSelected=true;
                    cycling.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_cycling,0,0,0);
                    counter--;
                    for(int i=0;i<sports.size();i++){
                        if(sports.get(i).equals("CYC"))
                            sports.remove(i);
                    }
                }
                else if(cyclingSelected){
                    cycling.setAlpha(1f);
                    cyclingSelected=false;
                    cycling.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_cycling,0,R.drawable.ic_check,0);
                    counter++;
                    sports.add("CYC");
                }

            }
        });
        tennis=(RadioButton)findViewById(R.id.tennis);
        tennis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!tennisSelected && counter > 1){
                    tennis.setAlpha(0.4f);
                    tennisSelected=true;
                    tennis.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_tennis,0,0,0);
                    counter--;
                    for(int i=0;i<sports.size();i++){
                        if(sports.get(i).equals("TEN"))
                            sports.remove(i);
                    }
                }
                else if(tennisSelected){
                    tennis.setAlpha(1f);
                    tennisSelected=false;
                    tennis.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_tennis,0,R.drawable.ic_check,0);
                    counter++;
                    sports.add("TEN");
                }

            }
        });
        pingPong=(RadioButton)findViewById(R.id.pingPong);
        pingPong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!pingPongSelected && counter > 1){
                    pingPong.setAlpha(0.4f);
                    pingPongSelected=true;
                    pingPong.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_pingpong,0,0,0);
                    counter--;
                    for(int i=0;i<sports.size();i++){
                        if(sports.get(i).equals("PIN"))
                            sports.remove(i);
                    }
                }
                else if(pingPongSelected){
                    pingPong.setAlpha(1f);
                    pingPongSelected=false;
                    pingPong.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_pingpong,0,R.drawable.ic_check,0);
                    counter++;
                    sports.add("PIN");
                }

            }
        });
        squash=(RadioButton)findViewById(R.id.squash);
        squash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!squashSelected && counter > 1){
                    squash.setAlpha(0.4f);
                    squashSelected=true;
                    squash.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_squash,0,0,0);
                    counter--;
                    for(int i=0;i<sports.size();i++){
                        if(sports.get(i).equals("SQU"))
                            sports.remove(i);
                    }
                }
                else if(squashSelected){
                    squash.setAlpha(1f);
                    squashSelected=false;
                    squash.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_squash,0,R.drawable.ic_check,0);
                    counter++;
                    sports.add("SQU");
                }

            }
        });
        others=(RadioButton)findViewById(R.id.others);
        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!othersSelected && counter > 1){
                    others.setAlpha(0.4f);
                    othersSelected=true;
                    others.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_others,0,0,0);
                    counter--;
                    for(int i=0;i<sports.size();i++){
                        if(sports.get(i).equals("OTH"))
                            sports.remove(i);
                    }
                }
                else if(othersSelected){
                    others.setAlpha(1f);
                    othersSelected=false;
                    others.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sport_others,0,R.drawable.ic_check,0);
                    counter++;
                    sports.add("OTH");
                }

            }
        });
        savePreferincesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user=new User();
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("userId", Persistance.getInstance().getUserInfo(SportDetailsActivity.this).id);
                params.put("gender", getIntent().getStringExtra("gender"));
                params.put("yearBorn", getIntent().getStringExtra("yearBorn"));
                if (getIntent().getStringExtra("profileImage") != null) {
                    params.put("profileImage",getIntent().getStringExtra("profileImage"));
                    user.profileImage=params.get("profileImage");
                }
                params.put("range", String.valueOf(progress));
                for(int i=0;i<sports.size();i++){
                    params.put("sports["+i+"]",sports.get(i));
                    Sport sport =new Sport(sports.get(i));
                    user.sports.add(sport);
                }


                user.gender=params.get("gender");
                user.age=Integer.valueOf(params.get("yearBorn"));
                user.id= Persistance.getInstance().getUserInfo(SportDetailsActivity.this).id;
                user.authKey=Persistance.getInstance().getUserInfo(SportDetailsActivity.this).authKey;
                user.firstName=Persistance.getInstance().getUserInfo(SportDetailsActivity.this).firstName;
                user.lastName=Persistance.getInstance().getUserInfo(SportDetailsActivity.this).lastName;
                user.facebookId=Persistance.getInstance().getUserInfo(SportDetailsActivity.this).facebookId;
                user.email=Persistance.getInstance().getUserInfo(SportDetailsActivity.this).email;
                user.password=Persistance.getInstance().getUserInfo(SportDetailsActivity.this).password;
                user.ludicoins=Persistance.getInstance().getUserInfo(SportDetailsActivity.this).ludicoins;






                Persistance.getInstance().setUserInfo(SportDetailsActivity.this,user);


                 HashMap<String, String> headers = new HashMap<String, String>();
                 headers.put("authKey", Persistance.getInstance().getUserInfo(SportDetailsActivity.this).authKey);
                 HTTPResponseController.getInstance().returnResponse(params, headers, SportDetailsActivity.this, "http://207.154.236.13/api/user/");
                SharedPreferences settings = SportDetailsActivity.this.getSharedPreferences("ProfileImage", SportDetailsActivity.this.MODE_PRIVATE);
                settings.edit().clear().commit();

            }
        });




    }

    @Override
    public void onBackPressed() {
        String profileImage = getIntent().getStringExtra("profileImage");
        String yearBorn=getIntent().getStringExtra("yearBorn");
        String gender=getIntent().getStringExtra("gender");
        Intent intent = new Intent(SportDetailsActivity.this, ProfileDetailsActivity.class);
        intent.putExtra("profileImage", profileImage);
        intent.putExtra("yearBorn",yearBorn);
        intent.putExtra("gender",gender);
        SportDetailsActivity.this.startActivity(intent);
    }
}
