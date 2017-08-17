package larc.ludiconprod.Activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.User;
import larc.ludiconprod.Utils.GlobalResources;
import larc.ludiconprod.Utils.util.Sport;

/**
 * Created by alex_ on 10.08.2017.
 */

public class MyProfileActivity extends Fragment {

    protected Context mContext;
    protected View v;
    protected Button settings;

    public MyProfileActivity() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = inflater.getContext();
        v = inflater.inflate(R.layout.my_profile_activity, container, false);

        try {
            super.onCreate(savedInstanceState);

            //this.requestInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        this.requestInfo();
    }

    private void requestInfo() {
        View tv = v.findViewById(R.id.profileContent);
        tv.setAlpha(0);
        tv = v.findViewById(R.id.profileProgressBar);
        tv.setAlpha(1);

        User u = Persistance.getInstance().getUserInfo(super.getActivity());

        User set = new User();


        set.authKey = u.authKey;
        set.id = u.id;

        Persistance.getInstance().setProfileInfo(super.getActivity(), set);

        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("authKey", u.authKey);
        HTTPResponseController.getInstance().getUserProfile(params, headers, u.id, this);
    }

    public void printInfo(User u) {
        try {
            TextView toNextLevel = (TextView) v.findViewById(R.id.profileToNextLevel);
            toNextLevel.setText("" + u.pointsToNextLevel);
            TextView sportsCount = (TextView) v.findViewById(R.id.profilePracticeSportsCountLabel);
            this.settings = (Button) v.findViewById(R.id.settings);
            this.settings.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, EditProfileActivity.class);
                    startActivity(intent);
                }
            });

            ImageView image = (ImageView) v.findViewById(R.id.profileImage);
            if (u.profileImage != null && !u.profileImage.isEmpty()) {
                Bitmap im = IntroActivity.decodeBase64(u.profileImage);
                image.setImageBitmap(im);
            }

            TextView name = (TextView) v.findViewById(R.id.profileName);
            TextView level = (TextView) v.findViewById(R.id.profileLevel);
            TextView points = (TextView) v.findViewById(R.id.profilePoints);
            TextView position = (TextView) v.findViewById(R.id.profilePosition);

            name.setText(u.firstName + " " + u.lastName);
            level.setText("" + u.level);
            points.setText("" + u.points);
            position.setText("" + u.position);

            ProgressBar levelBar = (ProgressBar) v.findViewById(R.id.profileLevelBar);
            levelBar.setProgress(u.points * levelBar.getMax() / u.pointsOfNextLevel );

            final ArrayList<String> sportCodes = new ArrayList<>();
            for (Sport s : u.sports) {
                sportCodes.add(s.code);
            }

            LinearLayout sportsLayout = (LinearLayout) v.findViewById(R.id.profileSports);
            ImageView sportImage;
            Class t = R.drawable.class;
            ArrayList<String> allSportCodes = new ArrayList<>(Sport.getSportMap().keySet());
            Collections.sort(allSportCodes, new Comparator<String>() {
                @Override
                public int compare(String s, String t1) {
                    if (sportCodes.contains(s) && !sportCodes.contains(t1)) {
                        return -1;
                    }
                    return s.compareTo(t1);
                }
            });

            sportsCount.setText(sportCodes.size() + "/" + allSportCodes.size());
            Resources r = mContext.getResources();
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

            sportsLayout.removeAllViews();
            for (int i = 0; i < allSportCodes.size(); ++i) {
                String sc = allSportCodes.get(i);
                sportImage = new ImageView(getContext());

                sportImage.setImageResource(this.findSportImageResource(sc));
                if (!sportCodes.contains(sc)) {
                    sportImage.setAlpha(0.4f);
                }
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                if (i == allSportCodes.size() - 1) {
                    lp.setMargins(0, 0, 0, 0);
                } else {
                    lp.setMargins(0, 0, px, 0);
                }

                sportsLayout.addView(sportImage, lp);
            }

            View tv = v.findViewById(R.id.profileContent);
            tv.setAlpha(1);
            tv = v.findViewById(R.id.profileProgressBar);
            tv.setAlpha(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int findSportImageResource(String sportCode) {
        switch (sportCode){
            case "BAS":
                return R.drawable.ic_sport_basketball_large;
            case "CYC":
                return R.drawable.ic_sport_cycling_large;
            case "FOT":
                return R.drawable.ic_sport_football_large;
            case "GYM":
                return R.drawable.ic_sport_gym_large;
            case "JOG":
                return R.drawable.ic_sport_jogging_large;
            case "PIN":
                return R.drawable.ic_sport_pingpong_large;
            case "SQU":
                return R.drawable.ic_sport_squash_large;
            case "TEN":
                return R.drawable.ic_sport_tennis_large;
            case "VOL":
                return R.drawable.ic_sport_voleyball_large;
            default:
                return R.drawable.ic_sport_others_large;
        }
    }
}
