package larc.ludiconprod.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import larc.ludiconprod.Adapters.MainActivity.AroundMeAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.R;

import static android.R.color.transparent;

public class Pop extends Activity {

    RelativeLayout popWindow;
    TextView textView11;
    RelativeLayout rel;
    Button join;

    ArrayList<EditText> editTextList = new ArrayList<EditText>();
    ArrayList<LinearLayout> linearLayoutList = new ArrayList<LinearLayout>();

    public boolean checkFieldsConstraints() {
        boolean isVerified = false;
        for (int i=0; i<linearLayoutList.size();i++) {
            if (editTextList.get(i).length() == 0) {
                linearLayoutList.get(i).setBackgroundResource(R.drawable.rounded_edittext_red);
                Animation shake = AnimationUtils.loadAnimation(Pop.this, R.anim.shake);
                linearLayoutList.get(i).startAnimation(shake);
                linearLayoutList.get(i).setBackgroundResource(R.drawable.rounded_edittext_red);
                isVerified = true;

            }

        }
        return isVerified;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = 370;

            popWindow = (RelativeLayout) findViewById(R.id.popWindow);
            textView11 = (TextView) findViewById(R.id.textView11);
            rel = (RelativeLayout) findViewById(R.id.rel);
            join = (Button) findViewById((R.id.join));

            final RelativeLayout relLayout = new RelativeLayout(this);
            RelativeLayout.LayoutParams paramsR = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            relLayout.setLayoutParams(paramsR);
            paramsR.addRule(RelativeLayout.BELOW, R.id.textView11);


            final ArrayList<String> myList = (ArrayList<String>) getIntent().getSerializableExtra("formParameters");
            final String eventId = (String) getIntent().getSerializableExtra("eventId");
            final String authKey = (String) getIntent().getSerializableExtra("authKey");
            final String userId = (String) getIntent().getSerializableExtra("userId");


            for (int i = 0; i<myList.size(); i++) {

                LinearLayout linLayout = new LinearLayout(this);
                linLayout.setId(i + 1);
                linLayout.setOrientation(LinearLayout.HORIZONTAL);
                linLayout.setBackgroundDrawable(ContextCompat.getDrawable(Pop.this, R.drawable.rounded_edittext));


                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(140*.99));
                linLayout.setLayoutParams(params);
                params.setMargins(20, 0, 20, 20);



                if(i != 0) {
                    params.addRule(RelativeLayout.BELOW, linLayout.getId()-1);
                }
                ImageView img = new ImageView(this);
                RelativeLayout.LayoutParams paramsImg = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsImg.addRule(RelativeLayout.CENTER_VERTICAL);
                paramsImg.setMargins(50, 0, 50, 0);
                LinearLayout.LayoutParams lparamsImg = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lparamsImg.gravity = Gravity.CENTER;
                img.setLayoutParams(paramsImg);
                img.setLayoutParams(lparamsImg);
                if (myList.get(i).equals("Address")) {
                    img.setImageResource(R.drawable.pin_1_normal);
                } else img.setImageResource(R.drawable.ic_info);
                img.setPadding(20, 0, 0, 0);
                linLayout.addView(img);


                EditText text = new EditText(this);
                text.setHint(myList.get(i));
                text.setHintTextColor(getResources().getColor(R.color.lightGray));;
                text.setBackgroundColor(transparent);
                editTextList.add(text);
                LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                text.setLayoutParams(paramsText);
                linLayout.addView(text);
                linearLayoutList.add(linLayout);


                    height = height + 160;
                relLayout.addView(linLayout);
            }

            if (height >= 1000){
                height = 1000;
            }

            getWindow().setLayout((int)(width*.7),(int) (height*.99));



           rel.addView(relLayout);

            join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkFieldsConstraints()) {
                        try {
                        HashMap<String, String> params = new HashMap<String, String>();
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("authKey", authKey);
                        params.put("eventId", eventId);
                        params.put("userId", userId);
                        int counter = 0;
                        if (editTextList.size() > 0) {
                            for (int i = 0; i < editTextList.size(); i++) {
                                params.put("formValues[" + counter + "]", editTextList.get(i).getText().toString());
                                counter++;
                            }
                        }
                        HTTPResponseController.getInstance().joinEvent(Pop.this, params, headers, eventId, null);
                        join.setEnabled(false);
                        finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                } else {
                    for (int i =0; i<editTextList.size();i++) {
                        if (editTextList.get(i).getText().length() == 0) {
                            Toast.makeText(Pop.this, "", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

    }
}

