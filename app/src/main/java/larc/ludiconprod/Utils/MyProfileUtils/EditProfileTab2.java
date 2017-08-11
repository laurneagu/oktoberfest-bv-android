package larc.ludiconprod.Utils.MyProfileUtils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import larc.ludiconprod.Activities.CreateNewActivity;
import larc.ludiconprod.R;

import static android.R.color.transparent;

/**
 * Created by alex_ on 10.08.2017.
 */

public class EditProfileTab2 extends Fragment {

    private RadioButton male;
    private RadioButton female;
    private RadioGroup sexSwitch;
    private int sex = 0;
    private TextView calendarTextView;
    private RelativeLayout passwordLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edittab2, container, false);

        EditText firstName = (EditText) v.findViewById(R.id.editFirstName);
        EditText lastName = (EditText) v.findViewById(R.id.editLastName);
        male = (RadioButton) v.findViewById(R.id.editMale);
        female = (RadioButton) v.findViewById(R.id.editFemale);
        sexSwitch = (RadioGroup) v.findViewById(R.id.editSexSwitch);
        calendarTextView = (TextView) v.findViewById(R.id.editDate);
        Button changePassword = (Button) v.findViewById(R.id.editPasswordButton);
        passwordLayout = (RelativeLayout) v.findViewById(R.id.editPasswordLayout);

        AssetManager asstes = inflater.getContext().getAssets();// Is this the right asset?
        Typeface typeFace= Typeface.createFromAsset(asstes, "fonts/Quicksand-Medium.ttf");
        male.setTypeface(typeFace);
        female.setTypeface(typeFace);

        Intent intent = super.getActivity().getIntent();// Is this the right intent?

        if(intent.getStringExtra("firstName") != null){
            firstName.setText(intent.getStringExtra("firstName"));
        }
        if(intent.getStringExtra("lastName") != null){
            lastName.setText(intent.getStringExtra("lastName"));
        }

        if(intent.getStringExtra("gender")!=null){
            if(intent.getStringExtra("gender").equals("0")){
                male.setChecked(true);
                male.setTextColor(Color.parseColor("#ffffff"));
            }
            else if(intent.getStringExtra("gender").equals("1")){
                female.setChecked(true);
                male.setTextColor(Color.parseColor("#ffffff"));
            }
        }

        if(male.isChecked()){
            male.setBackgroundResource(R.drawable.toggle_male);
            male.setTextColor(Color.parseColor("#ffffff"));
        } else{
            female.setBackgroundResource(R.drawable.toggle_female);
            male.setTextColor(Color.parseColor("#ffffff"));
        }

        sexSwitch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(male.isChecked()){
                    male.setBackgroundResource(R.drawable.toggle_male);
                    female.setBackgroundResource(transparent);
                    male.setTextColor(Color.parseColor("#ffffff"));
                    female.setTextColor(Color.parseColor("#1A0c3855"));
                    sex = 0;
                }
                else{
                    female.setBackgroundResource(R.drawable.toggle_female);
                    male.setBackgroundResource(transparent);
                    male.setTextColor(Color.parseColor("#1A0c3855"));
                    female.setTextColor(Color.parseColor("#ffffff"));
                    sex = 1;
                }
            }
        });

        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                try {
                    myCalendar.getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String displayDate = formatter.format(myCalendar.getTime());
                    String[] stringDate = displayDate.split("-");
                    String date = stringDate[2] + " " + CreateNewActivity.getMonth(Integer.parseInt(stringDate[1])) + " " + stringDate[0];
                    calendarTextView.setText(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ;

            }

        };

        calendarTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                // TODO Auto-generated method stub
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), R.style.DialogTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup.LayoutParams params = passwordLayout.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                passwordLayout.setLayoutParams(params);
            }
        });

        return v;
    }
}