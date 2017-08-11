package larc.ludiconprod.Activities;

import android.app.Activity;
import android.os.Bundle;

import java.util.HashMap;

import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;

/**
 * Created by ancuta on 8/9/2017.
 */

public class ActivityDetailsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_details_activity);
        getEventsDetails();

    }

    public void getEventsDetails(){
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> urlParams = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(this).authKey);

        //set urlParams

        urlParams.put("eventId",getIntent().getStringExtra("eventId"));
        urlParams.put("userId",Persistance.getInstance().getUserInfo(this).id);


        //get Around Me Event
        HTTPResponseController.getInstance().getEventDetails(params, headers, this,urlParams);
    }
}
