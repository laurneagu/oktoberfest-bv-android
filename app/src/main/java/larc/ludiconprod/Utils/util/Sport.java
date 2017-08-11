package larc.ludiconprod.Utils.util;

import android.support.annotation.NonNull;

import java.util.TreeMap;

/**
 * Created by ancuta on 7/12/2017.
 */

public class Sport {
    private static final TreeMap<String, String> SPORT_MAP = new TreeMap<>();
    static {
        SPORT_MAP.put("BAS", "basketball");
        SPORT_MAP.put("CYC", "cycling");
        SPORT_MAP.put("FOT", "football");
        SPORT_MAP.put("GYM", "gym");
        SPORT_MAP.put("JOG", "jogging");
        SPORT_MAP.put("OTH", "other");
        SPORT_MAP.put("PIN", "ping-pong");
        SPORT_MAP.put("SQU", "squash");
        SPORT_MAP.put("TEN", "tennis");
        SPORT_MAP.put("VOL", "volleyball");
    }
    public final String sportName;
    public final String code;


    public Sport(String code){

        this.code=code;
        /*switch (code){
           case "BAS":this.sportName="basketball";
               break;
            case "CYC":this.sportName="cycling";
                break;
            case "FOT":this.sportName="football";
                break;
            case "GYM":this.sportName="gym";
                break;
            case "JOG":this.sportName="jogging";
                break;
            case "OTH":this.sportName="other";
                break;
            case "PIN":this.sportName="ping-pong";
                break;
            case "SQU":this.sportName="squash";
                break;
            case "TEN":this.sportName="tennis";
                break;
            case "VOL":this.sportName="volleyball";
                break;
            default: this.sportName="";
                break;

        }*/

        String temp = SPORT_MAP.get(this.code);
        if (temp != null) {
            this.sportName = temp;
        } else {
            this.sportName = "";
        }
    }

    public static TreeMap<String, String> getSportMap() {
        return SPORT_MAP;
    }
}

