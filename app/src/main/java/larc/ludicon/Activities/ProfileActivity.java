package larc.ludicon.Activities;


import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.InputStream;

import larc.ludicon.R;
import larc.ludicon.UserInfo.User;

public class ProfileActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Bundle extras = getIntent().getExtras();
        final String uid = extras.getString("uid");

        Firebase userRef = User.firebaseRef.child("users").child(uid).child("name"); // check user
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.getValue() != null) {
                    TextView name = (TextView) findViewById(R.id.hello_message_activity);
                    name.setText(snapshot.getValue().toString());

                    Firebase userRef = User.firebaseRef.child("users").child(uid).child("profileImageURL");
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                ImageView imageView = (ImageView) findViewById(R.id.profileImageView);
                                new DownloadImageTask(imageView).execute(snapshot.getValue().toString());
                            } else {}
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                }

                else { }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }


    // Method which downloads Image from URL
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
