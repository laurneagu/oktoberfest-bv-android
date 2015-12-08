package larc.ludicon.Utils.CloudConnection;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import larc.ludicon.UserInfo.User;

/**
 * Created by Andrei on 12/4/2015.
 */
public class ParseConnection extends android.app.Application implements CloudConnection  {


    private Context context;
    private String id;
    private String key;
    private boolean connected;

    public ParseConnection(Context context, String id, String key){
        this.context = context;
        this.id = id;
        this.key = key;
        this.connected = false;
    }

    @Override
    public boolean checkConnection() {
        return connected;
    }

    public void initialize(){
        if(!connected) {
            Parse.initialize(context, id, key);
            this.connected = true;
        }
    }

    public void queryUsers(String column, String value){ // eg: column = "username" | value = "gheorghe"
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(column, value);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    // The query was successful.
                } else {
                    // Something went wrong.
                }
            }
        });
    }
}
