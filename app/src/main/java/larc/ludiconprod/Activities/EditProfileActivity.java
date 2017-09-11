package larc.ludiconprod.Activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import larc.ludiconprod.Adapters.EditProfile.EditActivitiesAdapter;
import larc.ludiconprod.Adapters.EditProfile.EditInfoAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.ImagePicker;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.PasswordEncryptor;
import larc.ludiconprod.R;
import larc.ludiconprod.User;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.MyProfileUtils.EditViewPagerAdapter;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;
import larc.ludiconprod.Utils.util.Sport;

/**
 * Created by alex_ on 10.08.2017.
 */

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, Response.ErrorListener, Response.Listener<JSONObject> {
    public static final int PICK_IMAGE_ID = 1423;

    private static final CharSequence TITLES[] = {"SPORT DETAILS", "INFO DETAILS"};
    private int tabsNumber = 2;
    private EditViewPagerAdapter adapter;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    private EditActivitiesAdapter myAdapter;

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
    private boolean imageChanged;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();

            super.setContentView(R.layout.edit_profile_activity);

            findViewById(R.id.internetRefresh).setAlpha(0);

            View backButton = findViewById(R.id.backButton);
            TextView titleText=(TextView) findViewById(R.id.titleText);
            titleText.setText("Edit profile");

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

            //this.myAdapter = new EditActivitiesAdapter(null, this.getApplicationContext(), this, getResources(), this);

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(EditProfileActivity.this, Main.class);
                    intent.putExtra("Tab", R.id.tab_profile);
                    startActivity(intent);
                }
            });

            AssetManager assets = getAssets();// Is this the right asset?
            Typeface typeFace= Typeface.createFromAsset(assets, "fonts/Quicksand-Medium.ttf");
            Typeface typeFaceBold = Typeface.createFromAsset(assets,"fonts/Quicksand-Bold.ttf");

            titleText.setTypeface(typeFace);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sameProfileInfo() {
        User old = Persistance.getInstance().getProfileInfo(this);

        old.gender = "" + this.sex;
        old.firstName = this.firstName.getText().toString();
        old.lastName = this.lastName.getText().toString();
        old.range = 1 + this.range.getProgress() + "";

        try {
            int ageText = Integer.parseInt(this.date.getText().toString());
            old.age = ageText;
        } catch (NumberFormatException ex) {
            old.age = 0;
        }

        old.sports.clear();
        for(int i = 0; i < sports.size(); ++i){
            Sport sport = new Sport(sports.get(i));
            old.sports.add(sport);
        }

        Persistance.getInstance().setProfileInfo(this, old);

        User u = Persistance.getInstance().getUserInfo(this);

        if (!u.firstName.equals(old.firstName)) {
            return false;
        }
        if (!u.lastName.equals(old.lastName)) {
            return false;
        }
        if (!u.gender.equals(old.gender)) {
            return false;
        }
        if (u.age != old.age) {
            return false;
        }
        if (!u.range.equals(old.range)) {
            return false;
        }
        if (u.sports.size() != old.sports.size()) {
            return false;
        }
        Collections.sort(u.sports, new Comparator<Sport>() {
            @Override
            public int compare(Sport a, Sport b) {
                return a.code.compareTo(b.code);
            }
        });
        Collections.sort(old.sports, new Comparator<Sport>() {
            @Override
            public int compare(Sport a, Sport b) {
                return a.code.compareTo(b.code);
            }
        });
        int size = u.sports.size();
        for (int i = 0; i < size; ++i) {
            if (!old.sports.get(i).code.equals(u.sports.get(i).code)) {
                return false;
            }
        }
        if (this.imageChanged) {
            return false;
        }
        return this.newPassword.getText().length() == 0 && this.oldPassword.getText().length() == 0 && this.repeatPassword.getText().length() == 0;
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

        if (user.firstName.isEmpty()) {
            Toast.makeText(this, "Enter your first name!", Toast.LENGTH_SHORT).show();
            return;
        }

        user.lastName = this.lastName.getText().toString();
        if (user.lastName.isEmpty()) {
            Toast.makeText(this, "Enter your last name!", Toast.LENGTH_SHORT).show();
            return;
        }

        user.range = 1 + this.range.getProgress() + "";
        user.profileImage = old.profileImage;

        int yearBorn = -1;
        try {
            int age = Integer.parseInt(this.date.getText().toString());
            yearBorn = Calendar.getInstance().get(Calendar.YEAR) - age;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid year provided!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (this.newPassword.getText().length() > 0 || this.oldPassword.getText().length() > 0 || this.repeatPassword.getText().length() > 0) {
            if (!this.newPassword.getText().toString().equals(this.repeatPassword.getText().toString())) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            String pass = this.newPassword.getText().toString();
			String oldPass = this.oldPassword.getText().toString();

            if (pass.isEmpty()) {
                Toast.makeText(this, "Enter a new password!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.length() < 7) {
                Toast.makeText(this, "Password must have minimum 7 characters!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (oldPass.isEmpty()) {
                Toast.makeText(this, "Enter the old password!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                pass = PasswordEncryptor.generateSHA255FromString(pass);
                oldPass = PasswordEncryptor.generateSHA255FromString(oldPass);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return;
            }

            HashMap<String, String> params = new HashMap<>();
			params.put("userId", old.id);
			params.put("oldPassword", oldPass);
			params.put("newPassword", pass);
            HashMap<String, String> headers = new HashMap<>();
            headers.put("authKey", old.authKey);
            HTTPResponseController.getInstance().changePassword(params, headers, this);

            Log.d("Update password", "Sending!!!");
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", old.id);
        params.put("gender", user.gender);
        params.put("lastName", user.lastName);
        params.put("firstName", user.firstName);
        params.put("yearBorn", "" + yearBorn);
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
            this.imageChanged = true;
            findViewById(R.id.saveChangesButton).setAlpha(1);
            findViewById(R.id.saveChangesButton2).setAlpha(1);
        }

        //super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        if (error instanceof NetworkError) {
            RelativeLayout ll = (RelativeLayout) findViewById(R.id.noInternetLayout);
            final float scale = super.getResources().getDisplayMetrics().density;
            int pixels = (int) (56 * scale + 0.5f);
            ll.getLayoutParams().height = pixels;
            ll.setLayoutParams(ll.getLayoutParams());
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.d("Update sent", "Sent!!!");

        Intent intent = new Intent(this, Main.class);
        intent.putExtra("Tab", R.id.tab_profile);
        startActivity(intent);

        User t = Persistance.getInstance().getUserInfo(this);
        t.gender = "" + this.sex;
        t.firstName = this.firstName.getText().toString();
        t.lastName = this.lastName.getText().toString();
        t.range = 1 + this.range.getProgress() + "";
        t.sports.clear();
        for(int i = 0; i < sports.size(); ++i){
            Sport sport = new Sport(sports.get(i));
            t.sports.add(sport);
        }
        Persistance.getInstance().setUserInfo(this, t);

        finish();
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
