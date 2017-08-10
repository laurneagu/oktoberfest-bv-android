package larc.ludiconprod.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import larc.ludiconprod.R;

/**
 * Created by alex_ on 10.08.2017.
 */

public class MyProfileActivity extends Fragment {

    protected Context mContext;
    protected  View v;
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

            this.settings = (Button) v.findViewById(R.id.button2);
            settings.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, EditProfileActivity.class);
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }
}
