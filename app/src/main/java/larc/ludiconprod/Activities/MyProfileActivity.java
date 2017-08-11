package larc.ludiconprod.Activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;

import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.User;
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

            final User user = Persistance.getInstance().getUserInfo(super.getActivity());

            this.settings = (Button) v.findViewById(R.id.settings);
            this.settings.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, EditProfileActivity.class);
                    intent.putExtra("gender", user.gender);
                    intent.putExtra("lastName", user.firstName);
                    intent.putExtra("firstName", user.lastName);
                    startActivity(intent);
                }
            });

            final ArrayList<String> sportCodes = new ArrayList<>();
            for (Sport s : user.sports) {
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
            Log.i("Image added size", "" + allSportCodes.size());
            for (String sc : allSportCodes) {
                sportImage = new ImageView(getContext());
                //sportImage.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                //sportImage.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                sportImage.setImageResource(this.findSportImageResource(sc));
                if (!sportCodes.contains(sc)) {
                    sportImage.setAlpha(0.5f);
                }
                sportsLayout.addView(sportImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
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
