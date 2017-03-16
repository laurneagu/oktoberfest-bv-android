package larc.ludiconprod.UserInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.DatabaseReference;


import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ciprian on 11/18/2015.
 */
public class User {

    private static String mfilename = "UserDetails";
    private static String mUserId = "Id";
    public static  String firstName = "firstName";
    public static  String lastName= "lastName";
    public static  String email= "email";
    public static  String gender = "unknown";
    public static String name = "unknown";
    public static String profilePictureURL = "";
    public static  Date birthDate = new Date();
    public static String uid = "";
    public static ArrayList<String> favouriteSports;

    // DatabaseReferenceRef
    public static DatabaseReference firebaseRef;

    /* Important: We get the data in these static fields, but after log in
    our user is "parsUser" */
   // public static ParseUser parseUser;
    public static Bitmap image;
    public static ProfilePictureView profilePictureView;

    public static final String password = "pass";

    /* This function will fill the user info from parse */
//    public static void updateUserFromParse(Context context){
//        User.firstName = (String)User.parseUser.get("firstName");
//        User.lastName = (String)User.parseUser.get("lastName");
//
//    }

    public static void setImage(){
        ImageView fbImage = ( ( ImageView)profilePictureView.getChildAt(0));
        Bitmap    bitmap  = ( (BitmapDrawable) fbImage.getDrawable()).getBitmap();
        User.image = bitmap;

    }

//    public static void updateParseImage(Context context){
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        User.image.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] data = stream.toByteArray();
//
//        ParseFile imageFile = new ParseFile("profileImage"+User.parseUser.getObjectId()+".png", data);
//        try {
//            imageFile.save();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        User.parseUser.put("image",imageFile);
//    }

    public static void setPassword(String i_password, Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString(password, i_password);
        edit.commit();
    }
    public static String getPassword(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        return sharedPref.getString(password, "");
    }

    public static String getId(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        String jsonDataString = sharedPref.getString(mUserId, "");
        return jsonDataString;
    }
    public static String getFirstName(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        return sharedPref.getString(firstName, "");
    }
    public static String getLastName(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        return sharedPref.getString(lastName, "");
    }
    public static String getEmail(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        return sharedPref.getString(email, "");
    }
    public static String getNumberOfSports(Context context){
        if(User.favouriteSports != null){
            if(User.favouriteSports.size() == 1) return User.favouriteSports.size()+ " sport";
            return User.favouriteSports.size()+ " sports";
        }
        return  "no sports";
    }

    public static void setInfo(String fName, String lName, String id ,String mail, Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename, 0);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString(mUserId, id);
        edit.putString(firstName, fName);
        edit.putString(lastName, lName);
        edit.putString(email,mail);
        edit.commit();
    }

    public static void clear(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(mfilename,0);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.clear().commit();
    }


}
