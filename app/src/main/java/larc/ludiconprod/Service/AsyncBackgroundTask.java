/*
package larc.ludiconprod.Service;

import android.content.Context;
import android.os.AsyncTask;

import larc.ludiconprod.UserInfo.User;

*
 * Created by Andrei on 2/27/2016.


public class AsyncBackgroundTask extends AsyncTask<Void, Void, Void> {

    Context context;
    public AsyncBackgroundTask(Context context)    {
        this.context=context;
    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        //Do stuff that you want after completion of background task and also dismiss progress here.
    }

    @Override
    protected Void doInBackground(Void... params) {
        int i = 0;
        while(true){
            User.firebaseRef.child("mesg").child(User.uid).child("backgroundTask").setValue(i + " times");
            i++;
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(i == Integer.MAX_VALUE){
                i = 0;
            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //create and show progress dialog here
    }

}
*/
