package larc.ludiconprod.Utils.ChatUtils;



import larc.ludiconprod.Utils.util.DateManager;
import java.util.*;
/**
 * Created by LaurUser on 12/11/2016.
 */
public class Chat1to1 implements Comparable<Chat1to1>{
    public String userUID;
    public String chatID;
    public String userName;
    public String friendPhoto;
    public String lastTimeOnline;
    public String lastMessageText;
    public String lastMessageAuthor;
    public String lastMessageDateString;

    @Override
    public int compareTo(Chat1to1 another) {
        String dateString1 = this.lastMessageDateString;
        String dateString2 = another.lastMessageDateString;

        Date date1 = DateManager.convertFromTextToDate(dateString1);
        Date date2 = DateManager.convertFromTextToDate(dateString2);
        if (date1.compareTo(date2) > 0) {
            return -1;
        } else if (date1.compareTo(date2) < 0){
            return 1;
        } else {
            return 0;
        }
    }
}