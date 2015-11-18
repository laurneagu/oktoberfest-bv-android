package larc.ludicon.Utils;

import android.widget.ImageView;

/**
 * Created by Ciprian on 11/18/2015.
 */
public class UserCredentials {

private String userName;
private String password;
private ImageView userPicture;

private static UserCredentials userCredentials;

private String id;

    public UserCredentials(String i_userName, String i_password){ // ImageView i_userPicture){
        userName = i_userName;
        password = i_password;
        //userPicture = i_userPicture;
    }

    public static void setUserCredentialsInstance(UserCredentials i_userEnrolledCredentials){
        if(userCredentials == null)
            userCredentials = i_userEnrolledCredentials;
    }

    public static UserCredentials getUserCredentialsInstance(){
        return  userCredentials;
    }
    public void setUserName(String i_userName){
        userName = i_userName;
    }
    public String getUserName(){
        return userName;
    }

    public void setPassword(String i_password){
        password = i_password;
    }
    public String getPassword(){
        return password;
    }

    public void setUserPicture(ImageView i_userPicture){
        userPicture = i_userPicture;
    }
    public ImageView getUserPicture(){
        return userPicture;
    }

    public void setId(String i_id){        id = i_id;   }
    public String getId(){  return id;  }

}
