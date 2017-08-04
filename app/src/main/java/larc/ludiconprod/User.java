package larc.ludiconprod;

import java.util.ArrayList;

import larc.ludiconprod.Utils.util.Sport;

/**
 * Created by ancuta on 7/10/2017.
 */

public class User {
    public final String authKey;
    public final String id;
    public final String firstName;
    public final String gender;
    public final String facebookId;
    public final String lastName;
    public final int ludicoins;
    public final String email;
    public final String password;
    public final int level;
    public final String profileImage;
    public final String range;
    public final ArrayList<Sport> sports=new ArrayList<Sport>();

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