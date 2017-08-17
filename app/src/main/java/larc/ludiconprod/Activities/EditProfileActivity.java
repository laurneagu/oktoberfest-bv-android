package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import larc.ludiconprod.Adapters.EditProfile.EditActivitiesAdapter;
import larc.ludiconprod.Adapters.EditProfile.EditInfoAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.ImagePicker;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.User;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.GlobalResources;
import larc.ludiconprod.Utils.MyProfileUtils.EditViewPagerAdapter;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;
import larc.ludiconprod.Utils.util.Sport;

/**
 * Created by alex_ on 10.08.2017.
 */

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int PICK_IMAGE_ID = 1423;

    private static final CharSequence TITLES[] = {"SPORT DETAILS", "INFO DETAILS"};
    private int tabsNumber = 2;
    private View v;
    private EditViewPagerAdapter adapter;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    private ArrayList<Event> activities = new  ArrayList<>();
    private EditInfoAdapter infoAdapter;
    private EditActivitiesAdapter myAdapter;
    private ArrayList<Event> myEventList;

    private int sex = 0;
    private EditText firstName;
    private EditText lastName;
    private TextView date;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText repeatPassword;
    private ArrayList<String> sports = new ArrayList<>();
    private SeekBar range;
    private ImageView image;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.edit_profile_activity);

        try {
            this.adapter = new EditViewPagerAdapter(getSupportFragmentManager(), EditProfileActivity.TITLES, tabsNumber);

            pager = (ViewPager) findViewById(R.id.editPager);
            pager.setAdapter(adapter);

            // Assiging the Sliding Tab Layout View
            tabs = (SlidingTabLayout) findViewById(R.id.editTabs);
            tabs.setDistributeEvenly(false); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(pager);

            myAdapter = new EditActivitiesAdapter(myEventList, this.getApplicationContext(), this, getResources(), this);


            //infoAdapter = new EditInfoAdapter(getActivity().getApplicationContext(), getActivity(), getResources(), this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeProfile() {
        User old = Persistance.getInstance().getProfileInfo(this);

        old.gender = "" + this.sex;
        old.firstName = this.firstName.getText().toString();
        old.lastName = this.lastName.getText().toString();
        old.range = 1 + this.range.getProgress() + "";
        old.age = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(this.date.getText().toString());

        old.sports.clear();
        for(int i = 0; i < sports.size(); ++i){
            Sport sport = new Sport(sports.get(i));
            old.sports.add(sport);
        }

        Persistance.getInstance().setProfileInfo(this, old);
    }

    @Override
    public void onClick(View view) {
        Log.d("Changed", ""  + firstName.getText() + range.getProgress() + sex + sports);

        User old = Persistance.getInstance().getProfileInfo(this);

        User user = new User();
        user.id = old.id;
        user.authKey = old.authKey;
        user.gender = "" + this.sex;
        user.firstName = this.firstName.getText().toString();
        user.lastName = this.lastName.getText().toString();
        user.range = 1 + this.range.getProgress() + "";
        user.profileImage = old.profileImage;

        /*user.id= old.id;
        user.authKey=old.authKey;
        user.facebookId=old.facebookId;
        user.email=old.email;
        user.password=old.password;
        user.ludicoins=old.ludicoins;*/

        if (this.newPassword.getText().length() > 0 || this.oldPassword.getText().length() > 0 || this.repeatPassword.getText().length() > 0) {
            if (!this.newPassword.getText().equals(this.oldPassword.getText())) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!this.oldPassword.toString().equals(user.password)) {
                Toast.makeText(this, "Wrong password!", Toast.LENGTH_SHORT).show();
                return;
            }
            user.password = this.newPassword.toString();
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", old.id);
        params.put("gender", user.gender);
        params.put("lastName", user.firstName);
        params.put("firstName", user.lastName);
        params.put("yearBorn", this.date.getText().toString());
        params.put("range", user.range);
        params.put("profileImage", user.profileImage);

        user.sports.clear();
        for(int i = 0; i < sports.size(); ++i){
            params.put("sports[" + i + "]", sports.get(i));
            Sport sport = new Sport(sports.get(i));
            user.sports.add(sport);
        }

        HashMap<String, String> headers = new HashMap<>();
        headers.put("authKey", old.authKey);
        HTTPResponseController.getInstance().updateUser(params, headers, this);

        Log.d("Update sent", "Sent!!!");

        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
        Log.d("B64 picture", "" + bitmap);
        if(bitmap != null) {
            image.setImageBitmap(bitmap);
            String b64i = ProfileDetailsActivity.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 50);
            Log.d("B64 picture", b64i);
            User user = Persistance.getInstance().getProfileInfo(this);
            user.profileImage = b64i;
            Persistance.getInstance().setProfileInfo(this, user);
        }

        //super.onActivityResult(requestCode, resultCode, data);
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public EditText getFirstName() {
        return firstName;
    }

    public void setFirstName(EditText firstName) {
        this.firstName = firstName;
    }

    public EditText getLastName() {
        return lastName;
    }

    public void setLastName(EditText lastName) {
        this.lastName = lastName;
    }

    public TextView getDate() {
        return date;
    }

    public void setDate(TextView date) {
        this.date = date;
    }

    public EditText getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(EditText oldPassword) {
        this.oldPassword = oldPassword;
    }

    public EditText getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(EditText newPassword) {
        this.newPassword = newPassword;
    }

    public EditText getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(EditText repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public ArrayList<String> getSports() {
        return sports;
    }

    public SeekBar getRange() {
        return range;
    }

    public void setRange(SeekBar range) {
        this.range = range;
    }


    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }
}
