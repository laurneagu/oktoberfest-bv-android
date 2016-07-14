package larc.ludicon.Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LaurUser on 7/2/2016.
 */
public class Event {
    public Map<String, Boolean> usersUID = new HashMap<String,Boolean>();
    public Date date;
    public int noUsers;
    public boolean active;
    public String sport;
    public String creator;
    public String place;
    public double latitude;
    public double longitude;
    public String id;
    public String creatorName;
    public String profileImageURL;
    public String getFirstUser() {
        return creator;
    }
    public int isOfficial;
    public int roomCapacity;
    public int priority;
    public int description;

}
