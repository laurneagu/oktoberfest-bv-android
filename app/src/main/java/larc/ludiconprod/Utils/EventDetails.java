package larc.ludiconprod.Utils;

import java.util.ArrayList;

/**
 * Created by ancuta on 8/10/2017.
 */

public class EventDetails {
    public int eventDate;
    public String description;
    public String placeName;
    public int latitude;
    public int longitude;
    public String placeId;
    public boolean isAuthorized;
    public String sportName;
    public String otherSportName;
    public int capacity;
    public int numberOfParticipants;
    public int points;
    public int ludicoins;
    public String creatorName;
    public int creatorLevel;
    public String creatorId;
    public String creatorProfilePicture;
    public boolean isParticipant;
    public ArrayList<Friend> listOfParticipants=new ArrayList<Friend>();
}
