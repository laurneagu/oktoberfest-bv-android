package larc.ludiconprod.Utils.MyProfileUtils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import larc.ludiconprod.R;

/**
 * Created by alex_ on 10.08.2017.
 */

public class EditProfileTab1 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.edittab1,container,false);
        return v;
    }
}