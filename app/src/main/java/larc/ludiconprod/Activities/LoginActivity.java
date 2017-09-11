package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.PasswordEncryptor;
import larc.ludiconprod.R;

/**
 * Created by ancuta on 7/11/2017.
 */

public class LoginActivity extends Activity {
    RelativeLayout backButton;
    TextView forgotPasswordText;
    Button loginButton;
    EditText email;
    EditText password;
    public static ProgressBar progressBar;
    private static String TAG = LoginActivity.class.getSimpleName();
    Toast mToast;





    public boolean checkFieldsConstraints(){
        boolean isVerified=false;

        if(email.getText().toString().length() == 0 || !email.getText().toString().contains("@") || !email.getText().toString().contains(".")){
            Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
            email.startAnimation(shake);
            email.setBackgroundResource(R.drawable.rounded_edittext_red);
            isVerified=true;
        }
        if(password.getText().length() == 0){
            Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
            password.startAnimation(shake);
            password.startAnimation(shake);
            password.setBackgroundResource(R.drawable.rounded_edittext_red);
            isVerified=true;
        }
        return isVerified;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        backButton = (RelativeLayout) findViewById(R.id.backButton);
        forgotPasswordText=(TextView) findViewById(R.id.forgotPasswordText);
        TextView titleText=(TextView) findViewById(R.id.titleText);
        titleText.setText("Login");
        Typeface typeFace= Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Medium.ttf");
        Typeface typeFaceBold= Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Bold.ttf");

        password=(EditText) findViewById(R.id.password) ;
        password.setTypeface(typeFace);
        email=(EditText) findViewById(R.id.email);
        email.setTypeface(typeFace);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        progressBar.setAlpha(0f);
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
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password.setBackgroundResource(R.drawable.rounded_edittext);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        loginButton=(Button) findViewById(R.id.loginButton);
        loginButton.setTypeface(typeFaceBold);
        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        forgotPasswordText.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(intent);
            }
        });





        loginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (!checkFieldsConstraints()) {
                try {

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("email", email.getText().toString());
                        params.put("password", PasswordEncryptor.generateSHA255FromString(password.getText().toString()));//PasswordEncryptor.generateSHA255FromString(password.getText().toString()));
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("apiKey", HTTPResponseController.API_KEY);
                       // headers.put("Content-Type","application/json;charset=utf-8");
                        HTTPResponseController.getInstance().returnResponse(params, headers, LoginActivity.this, "http://207.154.236.13/api/login/");
                        progressBar.setIndeterminate(true);
                        progressBar.setAlpha(1f);
                    }
                catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else{
                    showAToast();
                }
            }
        });





    }

    public void showAToast (){
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, R.string.errorMessage, Toast.LENGTH_LONG);
        mToast.show();
    }

}
