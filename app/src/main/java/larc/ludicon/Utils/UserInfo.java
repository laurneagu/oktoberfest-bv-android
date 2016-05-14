package larc.ludicon.Utils;

/**
 * Created by LaurUser on 5/14/2016.
 */
public class UserInfo {
    public String name;
    public String points;
    public String photo;
    public int index;
    public String uid;

    public UserInfo(String name, String photo, String points, int index, String uid){
        this.name = name;
        this.points = points;
        this.photo = photo;
        this.index = index;
        this.uid = uid;
    }
}