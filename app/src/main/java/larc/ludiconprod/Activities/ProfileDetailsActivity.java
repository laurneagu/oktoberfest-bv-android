package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import larc.ludiconprod.Controller.ImagePicker;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;

import static android.R.color.transparent;


public class ProfileDetailsActivity extends Activity {
    View backButton;
    RadioButton male;
    RadioButton female;
    RadioGroup sexSwitch;
    ImageView chooseAPhoto;
    ImageView imgProfilePicture;
    private static final int PICK_IMAGE_ID = 234; // the number doesn't matter
    String myBase64Image;
    Button saveAndContinueButton;
    TextView introText;

    int sex = 0;
    EditText age;

    public String getImageForProfile() {
        SharedPreferences sharedPreferences = ProfileDetailsActivity.this.getSharedPreferences("ProfileImage", 0);
        String imageString = sharedPreferences.getString("ProfileImage", "0");
        return imageString;
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.profile_details_activity);
        final Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
        final Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.ttf");

        RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.tool_bar);
        TextView title = (TextView)toolbar.findViewById(R.id.titleText);
        title.setTypeface(typeFace);

        backButton = findViewById(R.id.backButton);
        backButton.setAlpha(0f);
        backButton.setClickable(false);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        TextView titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("Profile Details");
        sexSwitch = (RadioGroup) findViewById(R.id.sexSwitch);
        chooseAPhoto = (ImageView) findViewById(R.id.chooseAPhoto);
        chooseAPhoto.setImageResource(R.drawable.ic_image_add);
        imgProfilePicture = (ImageView) findViewById(R.id.imgProfilePicture);
        TextView descriptionText = (TextView) findViewById(R.id.textView5);

        descriptionText.setTypeface(typeFace);
        male.setTypeface(typeFace);
        female.setTypeface(typeFace);


        if (!getImageForProfile().equals("0")) {
            Bitmap bitmap = decodeBase64(getImageForProfile());
            imgProfilePicture.setImageBitmap(bitmap);
            chooseAPhoto.setImageResource(R.drawable.ic_image_edit);
            myBase64Image = getImageForProfile();
        }

        saveAndContinueButton = (Button) findViewById(R.id.saveAndContinueButton);
        saveAndContinueButton.setTypeface(typeFaceBold);
        introText = (TextView) findViewById(R.id.introText);
        introText.setText("New you out here " + Persistance.getInstance().getUserInfo(this).firstName + "?Cool!");
        introText.setTypeface(typeFace);
        age = (EditText) findViewById(R.id.age);
        age.setTypeface(typeFace);
        if (getIntent().getStringExtra("profileImage") != null) {
            myBase64Image = getIntent().getStringExtra("profileImage");
            imgProfilePicture.setImageBitmap(decodeBase64(getIntent().getStringExtra("profileImage")));
            chooseAPhoto.setImageResource(R.drawable.ic_image_edit);
        }
        if (getIntent().getStringExtra("yearBorn") != null) {
            age.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(getIntent().getStringExtra("yearBorn"))));
            age.setSelection(String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(getIntent().getStringExtra("yearBorn"))).length());
        }
        if (getIntent().getStringExtra("gender") != null) {
            if (getIntent().getStringExtra("gender").equals("0")) {
                male.setChecked(true);
                male.setTextColor(Color.parseColor("#ffffff"));
            } else
                if (getIntent().getStringExtra("gender").equals("1")) {
                    female.setChecked(true);
                    female.setTextColor(Color.parseColor("#ffffff"));
                }
        }
        age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                age.setBackgroundResource(R.drawable.rounded_edittext);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        if (male.isChecked()) {
            male.setBackgroundResource(R.drawable.toggle_male);
            male.setTextColor(Color.parseColor("#ffffff"));
        } else {
            female.setBackgroundResource(R.drawable.toggle_female);
            male.setTextColor(Color.parseColor("#ffffff"));
        }
        sexSwitch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (male.isChecked()) {
                    male.setBackgroundResource(R.drawable.toggle_male);
                    female.setBackgroundResource(transparent);
                    male.setTextColor(Color.parseColor("#ffffff"));
                    female.setTextColor(Color.parseColor("#1A0c3855"));
                    sex = 0;
                } else {
                    female.setBackgroundResource(R.drawable.toggle_female);
                    male.setBackgroundResource(transparent);
                    male.setTextColor(Color.parseColor("#1A0c3855"));
                    female.setTextColor(Color.parseColor("#ffffff"));
                    sex = 1;
                }
            }
        });
        chooseAPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(ProfileDetailsActivity.this);
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);

            }
        });
        saveAndContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (age.getText().length() > 0) {
                    Intent intent = new Intent(ProfileDetailsActivity.this, SportDetailsActivity.class);
                    intent.putExtra("yearBorn", String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(age.getText().toString())));
                    if (myBase64Image != null) {
                        intent.putExtra("profileImage", myBase64Image);
                    }
                    intent.putExtra("gender", String.valueOf(sex));
                    //ProfileDetailsActivity.this.startActivity(intent);
                    startActivityForResult(intent, 1);
                } else {
                    Animation shake = AnimationUtils.loadAnimation(ProfileDetailsActivity.this, R.anim.shake);
                    age.startAnimation(shake);
                    age.setBackgroundResource(R.drawable.rounded_edittext_red);
                    Toast.makeText(ProfileDetailsActivity.this, "Please insert your age!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case PICK_IMAGE_ID:
            Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
            if (bitmap != null) {
                imgProfilePicture.setImageBitmap(bitmap);
                myBase64Image = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 50);
                chooseAPhoto.setImageResource(R.drawable.ic_image_edit);
                System.out.println("dimensiunea este:" + myBase64Image.length());
            }
            break;
        default:
            super.onActivityResult(requestCode, resultCode, data);
            break;
        }
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
