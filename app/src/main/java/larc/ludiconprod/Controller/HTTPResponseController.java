package larc.ludiconprod.Controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import larc.ludiconprod.Activities.ActivitiesActivity;
import larc.ludiconprod.Activities.ActivityDetailsActivity;
import larc.ludiconprod.Activities.CreateNewActivity;
import larc.ludiconprod.Activities.GMapsActivity;
import larc.ludiconprod.Activities.IntroActivity;
import larc.ludiconprod.Activities.InviteFriendsActivity;
import larc.ludiconprod.Activities.LoginActivity;
import larc.ludiconprod.Activities.Main;
import larc.ludiconprod.Activities.MyProfileActivity;
import larc.ludiconprod.Activities.ProfileDetailsActivity;
import larc.ludiconprod.Utils.Friend;
import larc.ludiconprod.Utils.util.AuthorizedLocation;
import larc.ludiconprod.Utils.util.Sport;
import larc.ludiconprod.User;
import larc.ludiconprod.Utils.Event;

import static larc.ludiconprod.Activities.ActivitiesActivity.aroundMeEventList;
import static larc.ludiconprod.Activities.ActivitiesActivity.fradapter;
import static larc.ludiconprod.Activities.ActivitiesActivity.myEventList;

/**
 * Created by ancuta on 7/12/2017.
 */

public class HTTPResponseController {

    String prodServer ="http://207.154.236.13/";

    private static HTTPResponseController instance = null;

    protected HTTPResponseController() {
    }

    public static HTTPResponseController getInstance() {
        if(instance == null) {
            instance = new HTTPResponseController();
        }
        return instance;
    }

    public JSONObject json=null;
    Activity activity;
    String password;
    String email;
    String eventid;
    boolean deleteAnotherUser=false;
    String userId;


    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void animateProfileImage(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("ProfileImage", 0);
        String image=sharedPreferences.getString("ProfileImage", "0");
        if(image != null && !image.equals("0")){
            Bitmap bitmap =decodeBase64(image);
            IntroActivity.profileImage.setImageBitmap(bitmap);
            IntroActivity.profileImage.setAlpha(0.3f);
            IntroActivity.profileImage.animate().alpha(1f).setDuration(1000);}
       // }else if(!imageJson.equals("")){
           // Bitmap bitmap =decodeBase64(imageJson);
           // IntroActivity.profileImage.setImageBitmap(bitmap);

       // }



    }

    private Response.Listener<JSONObject>  createRequestSuccessListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(activity.getLocalClassName().toString().equals("Activities.LoginActivity")||activity.getLocalClassName().toString().equals("Activities.IntroActivity")) {
                    try {
                        json = jsonObject;
                        ArrayList<String> listOfSports =new ArrayList<String>();
                        for(int i=0;i<jsonObject.getJSONObject("user").getJSONArray("sports").length();i++){
                            listOfSports.add(jsonObject.getJSONObject("user").getJSONArray("sports").get(i).toString());
                        }
                        ArrayList<Sport> sports =new ArrayList<Sport>();
                        for(int i=0;i<listOfSports.size();i++){
                            Sport sport=new Sport(listOfSports.get(i));
                            sports.add(sport);
                        }
                        User user=new User(jsonObject.getString("authKey"),jsonObject.getJSONObject("user").getString("id"),
                                jsonObject.getJSONObject("user").getString("firstName"),jsonObject.getJSONObject("user").getString("gender"),
                                jsonObject.getJSONObject("user").getString("facebookId"),jsonObject.getJSONObject("user").getString("lastName"),
                                jsonObject.getJSONObject("user").getInt("ludicoins"),jsonObject.getJSONObject("user").getInt("level"),
                                jsonObject.getJSONObject("user").getString("profileImage"),jsonObject.getJSONObject("user").getString("range"),
                                sports,email,password);
                        Persistance.getInstance().setUserInfo(activity, user);


                        if(user.range.equals("0")){
                           /* new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(activity, ProfileDetailsActivity.class);
                                    activity.startActivity(intent);
                                }
                            }, 5000);*/

                            new CountDownTimer(1100, 100) {
                                @Override
                                public void onTick(long l) {
                                    animateProfileImage();
                                }

                                @Override
                                public void onFinish() {
                                    Intent intent = new Intent(activity, ProfileDetailsActivity.class);
                                    activity.startActivity(intent);
                                }
                            }.start();
                            }



                        else{
                            /*new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(activity, Main.class);
                                    activity.startActivity(intent);
                                }
                            }, 5000);*/

                            new CountDownTimer(1100, 100) {
                                @Override
                                public void onTick(long l) {
                                    animateProfileImage();
                                }

                                @Override
                                public void onFinish() {
                                    Intent intent = new Intent(activity, Main.class);
                                    activity.startActivity(intent);
                                }
                            }.start();


                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else if(activity.getLocalClassName().toString().equals("Activities.RegisterActivity")){

                    Toast.makeText(activity,"Account has created!!",Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                        }
                    }, 3000);


                }
                else if(activity.getLocalClassName().toString().equals("Activities.SportDetailsActivity")){


                    Intent intent = new Intent(activity, Main.class);
                    activity.startActivity(intent);

                }

            }
        };
    }
    private Response.Listener<JSONObject>  createEventSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Intent intent =new Intent(activity,Main.class);
                activity.startActivity(intent);
                activity.finish();
            }
        };
    }
    private Response.Listener<JSONObject>  leaveEventSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(!deleteAnotherUser) {
                    Toast.makeText(activity, "You leave the event successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, Main.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
                else{
                    Toast.makeText(activity, "You exclude that user!", Toast.LENGTH_SHORT).show();
                    HashMap<String, String> params = new HashMap<String, String>();
                    HashMap<String, String> headers = new HashMap<String, String>();
                    HashMap<String, String> urlParams = new HashMap<String, String>();
                    headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

                    //set urlParams

                    urlParams.put("eventId",eventid);
                    urlParams.put("userId",userId);
                    HTTPResponseController.getInstance().getEventDetails(params, headers, activity,urlParams);
                }

            }
        };
    }
    private Response.Listener<JSONObject>  cancelEventSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Toast.makeText(activity,"You cancel the event successfully!",Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(activity,Main.class);
                activity.startActivity(intent);
                activity.finish();

            }
        };
    }

    private Response.Listener<JSONObject>  createAroundMeEventSuccesListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println(jsonObject+" ceva");
                try {
                    for (int i = 0; i < jsonObject.getJSONArray("aroundMe").length();i++ ){
                        Event event=new Event();
                        event.id=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("id");
                        int date=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("eventDate");
                        java.util.Date date1=new java.util.Date((long)date*1000);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String displayDate = formatter.format(date1);
                        event.eventDate=displayDate;
                        event.placeName=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("placeName");
                        event.sportCode=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("sportName");
                        event.capacity=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("capacity");
                        event.creatorName=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("creatorName");
                        event.creatorId=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("creatorId");
                        event.creatorLevel=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("creatorLevel");
                        event.creatorProfilePicture=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("creatorProfilePicture");
                        event.numberOfParticipants=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("numberOfParticipants");
                        event.points=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("points");
                        event.ludicoins=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("ludicoins");
                        for(int j=0;j < jsonObject.getJSONArray("aroundMe").getJSONObject(i).getJSONArray("participantsProfilePicture").length();j++){
                            event.participansProfilePicture.add(jsonObject.getJSONArray("aroundMe").getJSONObject(i).getJSONArray("participantsProfilePicture").getString(j));
                        }
                        System.out.println(event.id+" eventid:"+i+"  "+  event.numberOfParticipants + " profilepicture"+ jsonObject.getJSONArray("aroundMe").getJSONObject(i).getJSONArray("participantsProfilePicture").length() );
                        aroundMeEventList.add(event);

                    }
                    ActivitiesActivity.currentFragment.updateListOfEventsAroundMe(false);
                    if(jsonObject.getJSONArray("aroundMe").length() >= 1){
                        ActivitiesActivity.NumberOfRefreshAroundMe++;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
    }

    private Response.Listener<JSONObject>  createMyEventSuccesListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println(jsonObject+" myevent");
                try {
                    for (int i = 0; i < jsonObject.getJSONArray("myEvents").length();i++ ){
                        Event event=new Event();
                        event.id=jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("id");
                        int date=jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("eventDate");
                        java.util.Date date1=new java.util.Date((long)date*1000);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String displayDate = formatter.format(date1);
                        event.eventDate=displayDate;
                        event.placeName=jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("placeName");
                        event.sportCode=jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("sportName");
                        event.capacity=jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("capacity");
                        event.creatorName=jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("creatorName");
                        event.creatorLevel=jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("creatorLevel");
                        event.numberOfParticipants=jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("numberOfParticipants");
                        event.points=jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("points");
                        event.creatorProfilePicture=jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("creatorProfilePicture");
                        event.ludicoins=jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("ludicoins");
                        for(int j=0;j < jsonObject.getJSONArray("myEvents").getJSONObject(i).getJSONArray("participantsProfilePicture").length();j++){
                            event.participansProfilePicture.add(jsonObject.getJSONArray("myEvents").getJSONObject(i).getJSONArray("participantsProfilePicture").getString(j));

                        }
                        myEventList.add(event);

                    }
                    ActivitiesActivity.currentFragment.updateListOfMyEvents(false);
                    //adapter notifydatssetchanged
                   // myAdapter.notifyDataSetChanged();
                    if(jsonObject.getJSONArray("myEvents").length() >= 1){
                        ActivitiesActivity.NumberOfRefreshMyEvents++;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
    }
    private Response.Listener<JSONObject>  getLocationSuccesListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println(jsonObject +" location");
                GMapsActivity.authLocation.clear();
                try {
                    for (int i = 0; i < jsonObject.getJSONArray("locations").length();i++ ) {
                        AuthorizedLocation authLocation=new AuthorizedLocation();
                        authLocation.locationId = jsonObject.getJSONArray("locations").getJSONObject(i).getString("locationId");
                        authLocation.latitude=jsonObject.getJSONArray("locations").getJSONObject(i).getDouble("latitude");
                        authLocation.longitude=jsonObject.getJSONArray("locations").getJSONObject(i).getDouble("longitude");
                        authLocation.points=jsonObject.getJSONArray("locations").getJSONObject(i).getInt("points");
                        authLocation.authorizeLevel=jsonObject.getJSONArray("locations").getJSONObject(i).getInt("authorizeLevel");
                        authLocation.ludicoins=jsonObject.getJSONArray("locations").getJSONObject(i).getInt("ludicoins");
                        authLocation.name=jsonObject.getJSONArray("locations").getJSONObject(i).getString("name");
                        authLocation.description=jsonObject.getJSONArray("locations").getJSONObject(i).getString("description");
                        authLocation.address=jsonObject.getJSONArray("locations").getJSONObject(i).getString("address");
                        authLocation.image=jsonObject.getJSONArray("locations").getJSONObject(i).getString("image");
                        authLocation.schedule=jsonObject.getJSONArray("locations").getJSONObject(i).getString("schedule");
                        authLocation.phoneNumber=jsonObject.getJSONArray("locations").getJSONObject(i).getString("phoneNumber");

                        GMapsActivity.authLocation.add(authLocation);
                    }
                    GMapsActivity.putMarkers(GMapsActivity.authLocation);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
    }
    private Response.Listener<JSONObject>  getFriendsSuccesListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println(jsonObject +" friends");
                try {
                    for (int i = 0; i < jsonObject.getJSONArray("friends").length();i++ ) {
                        Friend friend=new Friend();
                        friend.userID = jsonObject.getJSONArray("friends").getJSONObject(i).getString("userId");
                        friend.userName=jsonObject.getJSONArray("friends").getJSONObject(i).getString("userName");
                        friend.profileImage=jsonObject.getJSONArray("friends").getJSONObject(i).getString("profileImage");
                        friend.numberOfMutuals=jsonObject.getJSONArray("friends").getJSONObject(i).getInt("numberOfMutuals");
                        friend.level=jsonObject.getJSONArray("friends").getJSONObject(i).getInt("level");
                        friend.offlineFriend=false;
                        friend.isInvited=false;

                        InviteFriendsActivity.friendsList.add(InviteFriendsActivity.numberOfOfflineFriends+1,friend);
                    }
                    InviteFriendsActivity.inviteFriendsAdapter.notifyDataSetChanged();
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
    }
    private Response.Listener<JSONObject>  getInvitedFriendsSuccesListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println(jsonObject +" invitedfriends");
                try {
                    for (int i = 0; i < jsonObject.getJSONArray("friends").length();i++ ) {
                        Friend friend=new Friend();
                        friend.userID = jsonObject.getJSONArray("friends").getJSONObject(i).getString("userID");
                        friend.userName=jsonObject.getJSONArray("friends").getJSONObject(i).getString("userName");
                        friend.profileImage=jsonObject.getJSONArray("friends").getJSONObject(i).getString("profilePicture");
                        friend.level=jsonObject.getJSONArray("friends").getJSONObject(i).getInt("level");
                        friend.isAlreadyInvited=jsonObject.getJSONArray("friends").getJSONObject(i).getInt("isInvited");
                        friend.offlineFriend=false;
                        friend.isInvited=false;

                        InviteFriendsActivity.friendsList.add(InviteFriendsActivity.numberOfOfflineFriends+1,friend);
                    }
                    InviteFriendsActivity.inviteFriendsAdapter.notifyDataSetChanged();
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
    }
    private Response.Listener<JSONObject>  getEventDetailsSuccesListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Bundle b=new Bundle();
                System.out.println(jsonObject +" eventDetails");
                try {
                    b.putInt("eventDate",jsonObject.getInt("eventDate"));
                    b.putString("description",jsonObject.getString("description"));
                    b.putString("placeName",jsonObject.getJSONObject("location").getString("placeName"));
                    b.putDouble("latitude",jsonObject.getJSONObject("location").getDouble("latitude"));
                    b.putDouble("longitude",jsonObject.getJSONObject("location").getDouble("longitude"));
                    b.putString("placeId",jsonObject.getJSONObject("location").getString("placeId"));
                    b.putString("placeAdress",jsonObject.getJSONObject("location").getString("placeAdress"));
                    b.putString("placeImage",jsonObject.getJSONObject("location").getString("placeImage"));
                    b.putInt("isAuthorized",jsonObject.getJSONObject("location").getInt("isAuthorized"));
                    b.putString("authorizelevel",jsonObject.getJSONObject("location").getString("authorizelevel"));
                    b.putString("sportName",jsonObject.getString("sportName"));
                    if(jsonObject.getString("sportName").equals("OTH")) {
                        b.putString("otherSportName", jsonObject.getString("otherSportName"));
                    }else{
                        b.putString("otherSportName", "");
                    }
                    b.putInt("capacity",jsonObject.getInt("capacity"));
                    b.putInt("numberOfParticipants",jsonObject.getInt("numberOfParticipants"));
                    b.putInt("points",jsonObject.getInt("points"));
                    b.putInt("ludicoins",jsonObject.getInt("ludicoins"));
                    b.putInt("privacy",jsonObject.getInt("privacy"));
                    b.putString("creatorName",jsonObject.getString("creatorName"));
                    b.putInt("creatorLevel",jsonObject.getInt("creatorLevel"));
                    b.putString("creatorId",jsonObject.getString("creatorId"));
                    b.putInt("isParticipant",jsonObject.getInt("isParticipant"));
                    b.putString("creatorProfilePicture",jsonObject.getString("creatorProfilePicture"));
                    ArrayList<String> participantsId=new ArrayList<>();
                    ArrayList<String> participantsName=new ArrayList<>();
                    ArrayList<String> participantsProfileImage=new ArrayList<>();
                    ArrayList<Integer> participantsLevel=new ArrayList<>();
                    for(int i=0;i < jsonObject.getJSONArray("participants").length();i++) {
                        participantsId.add(jsonObject.getJSONArray("participants").getJSONObject(i).getString("id"));
                        participantsName.add(jsonObject.getJSONArray("participants").getJSONObject(i).getString("name"));
                        participantsProfileImage.add(jsonObject.getJSONArray("participants").getJSONObject(i).getString("profilePicture"));
                        participantsLevel.add(jsonObject.getJSONArray("participants").getJSONObject(i).getInt("level"));
                    }
                    b.putStringArrayList("participantsId",participantsId);
                    b.putStringArrayList("participantsName",participantsName);
                    b.putStringArrayList("participantsProfileImage",participantsProfileImage);
                    b.putIntegerArrayList("participantsLevel",participantsLevel);
                    b.putString("eventId",eventid);


                    Intent intent = new Intent(activity, ActivityDetailsActivity.class);

                    intent.putExtras(b);
                    activity.startActivity(intent);


                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
    }
    private Response.Listener<JSONObject>  createJoinEventSuccesListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    myEventList.clear();
                    ActivitiesActivity.currentFragment.getMyEvents("0");
                    for(int i=0;i < aroundMeEventList.size();i++){
                        if(aroundMeEventList.get(i).id.equals(eventid)){
                            aroundMeEventList.remove(i);
                        }
                    }
                    fradapter.notifyDataSetChanged();
                    Toast.makeText(activity,"Join was successful!",Toast.LENGTH_SHORT).show();

                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
    }
    public String trimMessage(String json, String key){
        String trimmedString = null;
        if(activity.getLocalClassName().toString().equals("Activities.LoginActivity")) {
            LoginActivity.progressBar.setAlpha(0f);
        }

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }


    private  Response.ErrorListener createRequestErrorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = error.getMessage();
                json = trimMessage(json, "error");
                if(json != null) displayMessage(json);


            }

        };
    }

    private Response.Listener<JSONObject> createGetProfileListener(final Fragment fragment) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    User u = Persistance.getInstance().getProfileInfo(fragment.getActivity());

                    u.email = jsonObject.getString("email");
                    u.firstName = jsonObject.getString("firstName");
                    u.lastName = jsonObject.getString("lastName");
                    u.gender = jsonObject.getString("gender");
                    u.ludicoins = Integer.parseInt(jsonObject.getString("ludicoins"));
                    u.level = Integer.parseInt(jsonObject.getString("level"));
                    u.points = Integer.parseInt(jsonObject.getString("points"));
                    u.pointsToNextLevel = Integer.parseInt(jsonObject.getString("pointsToNextLevel"));
                    u.pointsOfNextLevel = Integer.parseInt(jsonObject.getString("pointsOfNextLevel"));
                    u.position = Integer.parseInt(jsonObject.getString("position"));
                    u.range = jsonObject.getString("range");
                    u.age = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(jsonObject.getString("yearBorn"));
                    u.profileImage = jsonObject.getString("profileImage");

                    JSONArray sports = jsonObject.getJSONArray("sports");
                    u.sports.clear();
                    for (int i = 0; i < sports.length(); ++i) {
                        u.sports.add(new Sport(sports.getString(i)));
                    }

                    MyProfileActivity mpa = (MyProfileActivity) fragment;
                    Persistance.getInstance().setProfileInfo(fragment.getActivity(), u);
                    mpa.printInfo(u);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private  Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = error.getMessage();
                json = trimMessage(json, "error");
                if(json != null) {
                    displayMessage(json);
                }
            }
        };
    }

    public void setActivity(Activity activity, String email, String password){
        this.activity=activity;
        this.email=email;
        this.password=password;
    }
    public void setEventId(String eventId,boolean deleteAnotherUser,String userId){
        this.eventid=eventId;
        this.deleteAnotherUser=deleteAnotherUser;
        this.userId=userId;
    }

    public void displayMessage(String toastString) {
        Toast.makeText(activity, toastString, Toast.LENGTH_LONG).show();
        if(activity.getLocalClassName().toString().equals("Activities.IntroActivity")){
            SharedPreferences settings = activity.getSharedPreferences("UserDetails",activity.MODE_PRIVATE);
            settings.edit().clear().commit();
            SharedPreferences profile = activity.getSharedPreferences("ProfileImage", activity.MODE_PRIVATE);
            profile.edit().clear().commit();
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(activity, IntroActivity.class);
            activity.startActivity(intent);
        }else if(activity.getLocalClassName().toString().equals("Activities.CreateNewActivity")){
            CreateNewActivity.createActivityButton.setEnabled(true);

        }
    }

    public JSONObject returnResponse(HashMap<String,String> params, HashMap<String,String> headers, Activity activity, String url){
        setActivity(activity,params.get("email"),params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params,headers,this.createRequestSuccessListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);

        return json;
    }
    public void getAroundMeEvent(HashMap<String,String> params, HashMap<String,String> headers, Activity activity,HashMap<String,String> urlParams){
        setActivity(activity,params.get("email"),params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer+"api/events?userId="+urlParams.get("userId")+"&pageNumber="+urlParams.get("pageNumber")+"&userLatitude="+urlParams.get("userLatitude")+
                "&userLongitude="+urlParams.get("userLongitude")+"&userRange="+urlParams.get("userRange")+"&userSports="+urlParams.get("userSports"), params,headers,this.createAroundMeEventSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);

    }
    public void getMyEvent(HashMap<String,String> params, HashMap<String,String> headers, Activity activity,HashMap<String,String> urlParams){
        setActivity(activity,params.get("email"),params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer+"api/events?userId="+urlParams.get("userId")+"&pageNumber="+urlParams.get("pageNumber"),params,headers,this.createMyEventSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);

    }
    public void joinEvent(HashMap<String,String> params, HashMap<String,String> headers,String eventId){
        setActivity(activity,params.get("email"),params.get("password"));
        eventid=eventId;
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer+"api/joinEvent/", params,headers,this.createJoinEventSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);

    }

    public void getAuthorizeLocations(HashMap<String,String> params, HashMap<String,String> headers, Activity activity,HashMap<String,String> urlParams){
        setActivity(activity,params.get("email"),params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer+"api/locations?latitudeNE="+urlParams.get("latitudeNE")+"&longitudeNE="+
                urlParams.get("longitudeNE")+"&latitudeSW="+urlParams.get("latitudeSW")+"&longitudeSW="+urlParams.get("longitudeSW")+"&sportCode="+urlParams.get("sportCode"),params,headers,this.getLocationSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }
    public void getFriends(HashMap<String,String> params, HashMap<String,String> headers, Activity activity,HashMap<String,String> urlParams){
        setActivity(activity,params.get("email"),params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer+"api/friends?userId="+urlParams.get("userId")+"&pageNumber="+urlParams.get("pageNumber"),params,headers,this.getFriendsSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }
    public void createEvent(HashMap<String,String> params, HashMap<String,String> headers, Activity activity){
        setActivity(activity,params.get("email"),params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer+"api/event/",params,headers,this.createEventSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }
    public void getEventDetails(HashMap<String,String> params, HashMap<String,String> headers, Activity activity,HashMap<String,String> urlParams){
        setActivity(activity,params.get("email"),params.get("password"));
        setEventId(urlParams.get("eventId"),false,"");
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer+"api/event?eventId="+urlParams.get("eventId")+"&userId="+urlParams.get("userId"),params,headers,this.getEventDetailsSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }

    public void leaveEvent(HashMap<String,String> params, HashMap<String,String> headers, Activity activity,boolean deleteAnotherUser){
        setActivity(activity,params.get("email"),params.get("password"));
        setEventId(params.get("eventId"),deleteAnotherUser,params.get("userId"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer+"api/leaveEvent",params,headers,this.leaveEventSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }
    public void deleteEvent(HashMap<String,String> params, HashMap<String,String> headers, Activity activity){
        setActivity(activity,params.get("email"),params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer+"api/cancelEvent",params,headers,this.cancelEventSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }

    public void getInvitedFriends(HashMap<String,String> params, HashMap<String,String> headers, Activity activity,HashMap<String,String> urlParams){
        setActivity(activity,params.get("email"),params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer+"api/friendsInvite?userId="+urlParams.get("userId")+"&eventId="+urlParams.get("eventId")+"&pageNumber="+urlParams.get("pageNumber"),params,headers,this.getInvitedFriendsSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }

    public void updateUser(HashMap<String,String> params, HashMap<String,String> headers, Activity activity) {
       RequestQueue requestQueue = Volley.newRequestQueue(activity);
       CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer + "api/user", params, headers, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               Log.d("User response", response.toString());
           }
       }, this.createErrorListener());
       requestQueue.add(jsObjRequest);
    }
    public void getUserProfile(HashMap<String,String> params, HashMap<String,String> headers, String id, Fragment fragment) {
        RequestQueue requestQueue = Volley.newRequestQueue(fragment.getActivity());
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer + "api/user?userId=" + id, params, headers, this.createGetProfileListener(fragment), this.createErrorListener());
        requestQueue.add(jsObjRequest);
    }

    public void changePassword(HashMap<String,String> params, HashMap<String,String> headers, Activity activity) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer + "api/changePassword", params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Change Password response", response.toString());
            }
        }, this.createErrorListener());
        requestQueue.add(jsObjRequest);
    }
}
