package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import larc.ludiconprod.Adapters.EditProfile.EditActivitiesAdapter;
import larc.ludiconprod.Adapters.EditProfile.EditInfoAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.User;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.MyProfileUtils.EditViewPagerAdapter;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;
import larc.ludiconprod.Utils.util.Sport;

/**
 * Created by alex_ on 10.08.2017.
 */

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final CharSequence TITLES[] = {"SPORT DETAILS", "INFO DETAILS"};
    private int tabsNumber = 2;
    private Context mContext;
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

    @Override
    public void onClick(View view) {
        Log.d("Changed", ""  + firstName.getText() + range.getProgress() + sex + sports);

        User old = Persistance.getInstance().getUserInfo(this);

        User user = old;
        user.gender = "" + this.sex;
        //user.age=Integer.valueOf(params.get("yearBorn"));
        user.firstName = this.firstName.toString();
        user.lastName = this.lastName.toString();
        user.range = 1 + this.range.getProgress() + "";

        /*user.id= old.id;
        user.authKey=old.authKey;
        user.facebookId=old.facebookId;
        user.email=old.email;
        user.password=old.password;
        user.ludicoins=old.ludicoins;*/

        if (this.newPassword.getText().length() > 0 || this.oldPassword.getText().length() > 0 || this.repeatPassword.getText().length() > 0) {
            if (!this.newPassword.getText().equals(this.oldPassword.getText())) {
                Toast.makeText(mContext, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!this.oldPassword.toString().equals(user.password)) {
                Toast.makeText(mContext, "Wrong password!", Toast.LENGTH_SHORT).show();
                return;
            }
            user.password = this.newPassword.toString();
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", old.id);
        params.put("gender", user.gender);
        params.put("lastName", user.firstName);
        params.put("firstName", user.lastName);
        params.put("range", user.range);
        //params.put("yearBorn", "" + user.age);
        //params.put("yearBorn", getIntent().getStringExtra("yearBorn"));
        user.sports.clear();
        for(int i = 0; i < sports.size(); ++i){
            params.put("sports[" + i + "]", sports.get(i));
            Sport sport = new Sport(sports.get(i));
            user.sports.add(sport);
        }

        Persistance.getInstance().setUserInfo(this, user);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("authKey", old.authKey);
        HTTPResponseController.getInstance().updateUser(params, headers, this);

        Log.d("Update sent", "Sent!!!");
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
}
