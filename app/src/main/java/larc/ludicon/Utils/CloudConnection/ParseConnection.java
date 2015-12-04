package larc.ludicon.Utils.CloudConnection;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Andrei on 12/4/2015.
 */
public class ParseConnection implements CloudConnection {

    private Context context;
    private String id;
    private String key;

    public ParseConnection(Context context, String id, String key){
        this.context = context;
        this.id = id;
        this.key = key;
    }

    public void initialize(){
        Parse.initialize(context,id,key);
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
