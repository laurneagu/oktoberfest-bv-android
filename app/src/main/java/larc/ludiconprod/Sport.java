package larc.ludiconprod;

/**
 * Created by ancuta on 7/12/2017.
 */

public class Sport {
    public final String sportName;
    public final String code;


    public Sport(String code){

        this.code=code;
        switch (code){
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

        }


        }
    }

