package larc.ludiconprod.Utils;

import java.util.ArrayList;

/**
 * Created by ancuta on 8/10/2017.
 */

public class EventDetails {
    public int eventDate;
    public String description;
    public String placeName;
    public String placeAdress;
    public String authorizeLevel;
    public String companyImage;
    public double latitude;
    public double longitude;
    public String placeId;
    public int isAuthorized;
    public String sportName;
    public String otherSportName;
    public int capacity;
    public int numberOfParticipants;
    public int points;
    public int privacy;
    public int ludicoins;
    public String creatorName;
    public int creatorLevel;
    public String creatorId;
    public String creatorProfilePicture;
    public int isParticipant;
    public String chatId="";
    public ArrayList<Friend> listOfParticipants=new ArrayList<Friend>();
    public Boolean isFormBased;
    public ArrayList<String> formParameters = new ArrayList<String>();
}
