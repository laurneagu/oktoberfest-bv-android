package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import larc.ludiconprod.R;


/**
 * Created by ancuta on 7/11/2017.
 */

public class ResetPasswordFinalActivity extends Activity {
    RelativeLayout backButton;
    Button backToLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_final_activity);
        Bundle extras = getIntent().getExtras();
        String from = extras.getString("from");
        Typeface typeFaceBold= Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Bold.ttf");
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
        TextView titleText=(TextView) findViewById(R.id.titleText);
        titleText.setTypeface(typeFace);
        TextView sendText = (TextView) findViewById(R.id.textViewSent);
        TextView emailText = (TextView) findViewById(R.id.textViewEmail);
        if(from.equals("reset")) {
            titleText.setText("Reset Password");
        }else if(from.equals("register")){
            titleText.setText("Register success");
        }
        sendText.setTypeface(typeFace);
        emailText.setTypeface(typeFace);
        if(from.equals("register")){
            sendText.setText("We've sent you an email with confirmation code");
        }
        backButton=(RelativeLayout) findViewById(R.id.backButton);
        backToLogin=(Button) findViewById(R.id.returnLoginButton);
        backToLogin.setTypeface(typeFaceBold);
        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                startActivity(intent);*/
                finish();
            }
        });
        backToLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });


    }

}
