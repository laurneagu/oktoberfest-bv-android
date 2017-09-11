package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
        Typeface typeFaceBold= Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Bold.ttf");
        TextView titleText=(TextView) findViewById(R.id.titleText);
        titleText.setText("Reset Password");
        backButton=(RelativeLayout) findViewById(R.id.backButton);
        backToLogin=(Button) findViewById(R.id.returnLoginButton);
        backToLogin.setTypeface(typeFaceBold);
        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                startActivity(intent);
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
