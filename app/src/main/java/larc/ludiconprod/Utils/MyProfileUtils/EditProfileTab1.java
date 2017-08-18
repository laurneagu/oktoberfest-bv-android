package larc.ludiconprod.Utils.MyProfileUtils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import larc.ludiconprod.Activities.EditProfileActivity;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.User;
import larc.ludiconprod.Utils.util.Sport;

public class EditProfileTab1 extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private int counter;
    private ArrayList<String> sports;
    private TreeMap<Integer, String> codes = new TreeMap<>();
    private TreeMap<Integer, Integer> compoundDrawables = new TreeMap<>();
    private int progress;
    private TextView progressText;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edittab1,container,false);

        try {
            EditProfileActivity epa = (EditProfileActivity) getActivity();

            User u = Persistance.getInstance().getProfileInfo(super.getActivity());
            SeekBar seekBar = (SeekBar) v.findViewById(R.id.editRangeBar);
            seekBar.setOnSeekBarChangeListener(this);
            Button save = (Button) v.findViewById(R.id.saveChangesButton2);
            this.progressText = (TextView) v.findViewById(R.id.editRangeTextView);

            this.sports = epa.getSports();
            epa.setRange(seekBar);
            this.sports.clear();

            int progress = Integer.parseInt(u.range);
            seekBar.setProgress(progress - 1);

            codes.put(R.id.editFootball, "FOT");
            codes.put(R.id.editBasketball, "BAS");
            codes.put(R.id.editVolleyball, "VOL");
            codes.put(R.id.editJogging, "JOG");
            codes.put(R.id.editGym, "GYM");
            codes.put(R.id.editCycling, "CYC");
            codes.put(R.id.editTennis, "TEN");
            codes.put(R.id.editPingPong, "PIN");
            codes.put(R.id.editSquash, "SQU");
            codes.put(R.id.editOthers, "OTH");

            TreeMap<Integer, Integer> cd = this.compoundDrawables;
            cd.put(R.id.editFootball, R.drawable.ic_sport_football);
            cd.put(R.id.editBasketball, R.drawable.ic_sport_basketball);
            cd.put(R.id.editVolleyball, R.drawable.ic_sport_voleyball);
            cd.put(R.id.editJogging, R.drawable.ic_sport_jogging);
            cd.put(R.id.editGym, R.drawable.ic_sport_gym);
            cd.put(R.id.editCycling, R.drawable.ic_sport_cycling);
            cd.put(R.id.editTennis, R.drawable.ic_sport_tennis);
            cd.put(R.id.editPingPong, R.drawable.ic_sport_pingpong);
            cd.put(R.id.editSquash, R.drawable.ic_sport_squash);
            cd.put(R.id.editOthers, R.drawable.ic_sport_others);

            Set<Integer> codesKeys = this.codes.keySet();

            for (Integer id : codesKeys) {
                RadioButton rb = (RadioButton) v.findViewById(id);
                Integer compoundDrawable = this.compoundDrawables.get(id);
                rb.setSelected(true);
                rb.setAlpha(0.4f);
                rb.setCompoundDrawablesWithIntrinsicBounds(compoundDrawable,0,0,0);
                rb.setOnClickListener(this);
            }

            for (Sport s : u.sports) {
                for (Integer id : codesKeys) {
                    if (s.code.equals(this.codes.get(id))) {
                        Log.d("Sport already", s.code);
                        RadioButton rb = (RadioButton) v.findViewById(id);
                        Integer compoundDrawable = this.compoundDrawables.get(id);
                        rb.setAlpha(1f);
                        rb.setSelected(false);
                        rb.setCompoundDrawablesWithIntrinsicBounds(compoundDrawable,0,R.drawable.ic_check,0);
                        counter++;
                        sports.add(s.code);
                        break;
                    }
                }
            }

            save.setOnClickListener(epa);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    @Override
    public void onClick(View view) {
        RadioButton rb = (RadioButton) view;
        String code = this.codes.get(view.getId());
        Integer compoundDrawable = this.compoundDrawables.get(view.getId());
        if(!rb.isSelected() && counter > 1 ) {
            rb.setSelected(true);
            view.setAlpha(0.4f);
            rb.setCompoundDrawablesWithIntrinsicBounds(compoundDrawable,0,0,0);
            counter--;
            sports.remove(code);
        } else if(rb.isSelected()) {
            view.setAlpha(1f);
            rb.setSelected(false);
            rb.setCompoundDrawablesWithIntrinsicBounds(compoundDrawable,0,R.drawable.ic_check,0);
            counter++;
            sports.add(code);
        }
        Log.d("New sports", this.sports.toString());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
        if(seekBar.getProgress() >= 1) {
            progress = progressValue+1;
            progressText.setText(progress + " km");
        } else{
            progressText.setText("1" + " km");
            progress=1;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}