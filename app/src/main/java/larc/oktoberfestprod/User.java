package larc.oktoberfestprod;

import java.util.ArrayList;

import larc.oktoberfestprod.Utils.util.Sport;

/**
 * Created by ancuta on 7/10/2017.
 */

public class User {
    public String authKey;
    public String id;
    public String email;
    public String firstName;
    public String lastName;
    public String gender;
    public String facebookId;
    public int ludicoins;
    public int points;
    public int pointsToNextLevel;
    public int pointsOfNextLevel;
    public int position;
    public String password;
    public  int level;
    public  int age;
    public  String profileImage;
    public  String range;
    public int countEventsAttended;
    public  ArrayList<Sport> sports=new ArrayList<Sport>();

/*
    public User(){
        this.authKey="";
        this.id="";
        this.firstName="";
        this.gender="";
        this.facebookId="";
        this.lastName="";
        this.ludicoins=-1;
        this.level=-1;
        this.profileImage="";
        this.range="";
        this.email="";
        this.password="";

    }*/

public User(){

}
    public User(String authKey, String id, String firstName, String gender, String facebookId, String lastName, int ludicoins, int level, String profileImage, String range, ArrayList<Sport> sports, String email, String password) {
        this.authKey=authKey;
        this.id=id;
        this.firstName = firstName;
        this.gender=gender;
        this.facebookId=facebookId;
        this.lastName = lastName;
        this.ludicoins=ludicoins;
        this.level=level;
        this.profileImage=profileImage;
        this.range=range;
        for(int i=0;i<sports.size();i++){
            this.sports.add(sports.get(i));
        }
        this.password=password;
        this.email=email;

    }


}