package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import larc.ludiconprod.R;


/**
 * Created by ancuta on 7/11/2017.
 */

public class ResetPasswordActivity extends Activity {

    ImageButton backButton;
    Button resetPassword;
    EditText email;
    public boolean checkFieldsConstraints() {
        boolean isVerified=false;
        if(email.getText().toString().length() == 0 || !email.getText().toString().contains("@") || !email.getText().toString().contains(".")){
            Animation shake = AnimationUtils.loadAnimation(ResetPasswordActivity.this, R.anim.shake);
            email.startAnimation(shake);
            email.setBackgroundResource(R.drawable.rounded_edittext_red);
            isVerified=true;
        }

        return isVerified;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);
        Typeface typeFace= Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Medium.ttf");
        Typeface typeFaceBold= Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Bold.ttf");
        TextView titleText=(TextView) findViewById(R.id.titleText);
        titleText.setText("Reset Password");
        backButton=(ImageButton) findViewById(R.id.backButton);
        backButton.setBackgroundResource(R.drawable.ic_nav_up);
        resetPassword=(Button) findViewById(R.id.resetPasswordButton);
        resetPassword.setTypeface(typeFaceBold);
        email=(EditText)findViewById(R.id.emailAdress);
        email.setTypeface(typeFace);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email.setBackgroundResource(R.drawable.rounded_edittext);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                startActivity(intent);
            }
        });
        resetPassword.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(!checkFieldsConstraints()) {
                    Intent intent = new Intent(getApplicationContext(), ResetPasswordFinalActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(ResetPasswordActivity.this,"Please provide email.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
