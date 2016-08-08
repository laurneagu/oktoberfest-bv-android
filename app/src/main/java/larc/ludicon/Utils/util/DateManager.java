package larc.ludicon.Utils.util;

import java.security.spec.ECField;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Andrei on 8/8/2016.
 */

public class DateManager {

    public static long ONEoverMILI = 1000;
    public static DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");

    public static long convertFromTextToSeconds(String text){
        long sec=0;

        Date date;
        try {
            date = new Date(text);
            sec = date.getTime()/ONEoverMILI;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sec;
    }

    public static String convertFromSecondsToText(long sec){
        String text = "";

        try {

            Date date = new Date(sec*ONEoverMILI);
            text = dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return text;
    }

    public static Date convertFromTextToDate(String text){
        Date date = null;

        try {
            date = new Date(text);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String convertFromDateToText(Date date){
        String text = "";

        try {
            text = dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return text;
    }

    public static Date convertFromSecondsToDate(long sec){
        Date date = null;

        try{
            date = new Date(sec*ONEoverMILI);
        }catch(Exception e){
            e.printStackTrace();
        }

        return date;
    }

    public static long convertFromDateToSeconds(Date date){
        long sec = 0;

        try{
            sec = date.getTime()/ONEoverMILI;
        }catch (Exception e){
            e.printStackTrace();
        }

        return sec;
    }


}
